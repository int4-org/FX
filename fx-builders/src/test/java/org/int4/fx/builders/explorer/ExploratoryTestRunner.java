package org.int4.fx.builders.explorer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.int4.common.function.ThrowingConsumer;
import org.int4.common.function.ThrowingFunction;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.MultipleFailuresError;
import org.opentest4j.TestAbortedException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * A test runner that performs exhaustive exploration of an {@link Explorable} model's state space.
 * <p>
 * The runner discovers methods annotated with {@link Action} and {@link Assertion} in the
 * provided {@link Explorable} class. It then systematically executes all possible sequences
 * of actions, using the {@link Explorable#snapshot()} to identify and prune already visited states.
 * <p>
 * This approach is particularly effective for testing complex state machines or UI controls
 * where many different interaction sequences could lead to an inconsistent state.
 */
public final class ExploratoryTestRunner {

  /**
   * Explores all reachable states of the specified {@link Explorable} class.
   * <p>
   * The exploration starts from a fresh instance of the class and applies actions
   * recursively. For each reached state, all assertions are verified.
   *
   * @param explorableClass the class to explore, must implement {@link Explorable} and
   *   have a public no-arg constructor
   * @throws AssertionError if any assertion fails during exploration
   * @throws MultipleFailuresError if multiple assertions fail for a single state
   * @throws IllegalStateException if an unexpected error occurs during exploration
   */
  public static void explore(Class<? extends Explorable> explorableClass) {
    doAll(explorableClass);
  }

  record NamedAction(String description, ThrowingFunction<Explorable, Runnable, Exception> executor) {}

  record RecordedStep(NamedAction action, Object resultingState) {
    @Override
    public final String toString() {
      return "RecordedStep[" + action.description + " -> " + resultingState + "]";
    }
  }

  record Path(List<RecordedStep> steps, NamedAction action) {
    @Override
    public final String toString() {
      return "Path[steps=" + steps + " -> " + action.description + "]";
    }
  }

  static void doAll(Class<? extends Explorable> testClass) {
    List<NamedAction> actions = new ArrayList<>();
    List<Method> assertions = new ArrayList<>();

    for(Method method : testClass.getDeclaredMethods()) {
      if(method.getAnnotation(Action.class) != null) {
        if(Runnable.class.isAssignableFrom(method.getReturnType())) {
          ValueSource valueSource = method.getAnnotation(ValueSource.class);

          if(valueSource != null) {
            for(String v : valueSource.strings()) {
              actions.add(new NamedAction(method.getName() + "(\"" + v + "\")", instance -> (Runnable)method.invoke(instance, v)));
            }
          }
          else {
            actions.add(new NamedAction(method.getName(), instance -> (Runnable)method.invoke(instance)));
          }
        }
        else {
          throw new IllegalStateException("Method annotated with @Action must return a Runnable with the effect to execute: " + method);
        }
      }

      if(method.getAnnotation(Assertion.class) != null) {
        assertions.add(method);
      }
    }

    // Ensure actions are in a deterministic order for test reproducability:
    actions.sort(Comparator.comparing(NamedAction::description));

    if(actions.isEmpty() || assertions.isEmpty()) {
      fail("The test class must define at least one action and assertion: " + testClass);
    }

    Set<Object> seenStates = new HashSet<>();
    Deque<Path> pathsToExplore = new ArrayDeque<>();

    for(NamedAction action : actions) {
      pathsToExplore.add(new Path(List.of(), action));
    }

    ThrowingConsumer<Object, Throwable> asserter = instance -> {
      List<Throwable> failures = new ArrayList<>();

      for(Method assertion : assertions) {
        try {
          try {
            assertion.invoke(instance);
          }
          catch (InvocationTargetException e) {
            throw e.getCause();
          }
        }
        catch (MultipleFailuresError e) {
          failures.addAll(e.getFailures());
        }
        catch (Throwable t) {
          failures.add(t);
        }
      }

      if(!failures.isEmpty()) {
        throw new MultipleFailuresError("Assertion failures", failures);
      }
    };

    int pathsExplored = 0;
    int maxPathLength = 0;

    while(!pathsToExplore.isEmpty()) {
      Path path = pathsToExplore.pop();
      Object finalState = null;

      try {
        Explorable instance = executePath(testClass, path, asserter);

        finalState = instance.snapshot();

        asserter.accept(instance);  // throws AssertionError/MultipleFailuresError

        pathsExplored++;
        maxPathLength = Math.max(maxPathLength, path.steps.size() + 1);

        if(seenStates.add(finalState)) {
          // not seen before state found
          List<RecordedStep> execution = Stream.concat(
            path.steps.stream(),
            Stream.of(new RecordedStep(path.action, finalState))
          ).toList();

          for(NamedAction action : actions) {
            pathsToExplore.add(new Path(execution, action));
          }
        }
      }
      catch(TestAbortedException e) {
        // assumption failure, skip
      }
      catch(MultipleFailuresError e) {
        throw new MultipleFailuresError(
          "Path " + pathsExplored + ": " + constructPath(path, finalState),
          e.getFailures()
        );
      }
      catch(AssertionError e) {
        fail("Path " + pathsExplored + ": " + constructPath(path, finalState), e);
      }
      catch(Throwable e) {
        throw new IllegalStateException(e);
      }
    }

    System.out.println(ExploratoryTestRunner.class.getSimpleName() + ": " + testClass + " -- Explored " + pathsExplored + " paths, longest path: " + maxPathLength);
  }

  private static String constructPath(Path path, Object finalState) {
    StringBuilder builder = new StringBuilder();

    for(RecordedStep step : path.steps) {
      builder.append("\n - " + step.action.description + " -> " + step.resultingState);
    }

    builder.append("\n - " + path.action.description + " -> " + finalState + "\n");

    return builder.toString();
  }

  private static Explorable executePath(Class<? extends Explorable> cls, Path path, ThrowingConsumer<Object, Throwable> asserter) throws Throwable {
    Explorable instance = cls.getDeclaredConstructor().newInstance();

    for(RecordedStep step : path.steps) {
      runAction(instance, step.action());

      asserter.accept(instance);

      assertThat(step.resultingState).isEqualTo(instance.snapshot());
    }

    try {
      runAction(instance, path.action);
    }
    catch(InvocationTargetException e) {
      throw e.getCause();
    }

    return instance;
  }

  private static void runAction(Explorable instance, NamedAction action) throws Exception {
    Runnable runnable = action.executor().apply(instance);

    Object stateBeforeEffect = instance.snapshot();

    runnable.run();

    Object stateAfterEffect = instance.snapshot();

    if (!stateBeforeEffect.equals(stateAfterEffect)) {
      throw new IllegalStateException("Action " + action + " violates strict seperation of expectation modification and effect execution");
    }
  }

  private ExploratoryTestRunner() {}

}