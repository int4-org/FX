package org.int4.fx.builders.control;

import javafx.scene.Scene;
import javafx.scene.control.TextField;

import org.int4.fx.values.domain.Domain;
import org.int4.fx.values.model.DoubleModel;
import org.int4.fx.values.model.IntegerModel;
import org.int4.fx.values.model.LongModel;
import org.int4.fx.values.model.StringModel;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TextFieldBuilderTest  {

  @Nested
  class IntegerTests extends AbstractTextFieldBuilderTest<Integer, IntegerModel> {
    @Override
    protected IntegerModel createModel() {
      return IntegerModel.of(4, Domain.bounded(0, 10, 2));
    }

    @Override
    protected TextField createControl(IntegerModel model) {
      return new TextFieldBuilder().model(model);
    }

    @Override
    protected Values<Integer> modelValues() {
      return new Values<>(4, 6, -10);
    }

    @Override
    protected Values<String> controlValues() {
      return new Values<>("4", "6", "");
    }

    @Override
    protected Values<String> notNormalizedControlValues() {
      return new Values<>("04", "006", "text");
    }
  }

  @Nested
  class LongTests extends AbstractTextFieldBuilderTest<Long, LongModel> {
    @Override
    protected LongModel createModel() {
      return LongModel.of(4L, Domain.bounded(0L, 10, 2));
    }

    @Override
    protected TextField createControl(LongModel model) {
      return new TextFieldBuilder().model(model);
    }

    @Override
    protected Values<Long> modelValues() {
      return new Values<>(4L, 6L, -10L);
    }

    @Override
    protected Values<String> controlValues() {
      return new Values<>("4", "6", "");
    }

    @Override
    protected Values<String> notNormalizedControlValues() {
      return new Values<>("04", "006", "text");
    }
  }

  @Nested
  class DoubleTests {

    static abstract class AbstractDoubleTextFieldBuilderTest extends AbstractTextFieldBuilderTest<Double, DoubleModel> {
      @Override
      protected DoubleModel createModel() {
        return DoubleModel.of(5.0, Domain.bounded(0, 10, 0.1));
      }

      @Override
      protected TextField createControl(DoubleModel model) {
        return new TextFieldBuilder().model(model);
      }

      @Override
      protected Values<Double> modelValues() {
        return new Values<>(5.0, 6.5, -10.0);
      }

      @Override
      protected Values<String> controlValues() {
        return new Values<>("5", "6.5", "");
      }
    }

    @Nested
    class _1 extends AbstractDoubleTextFieldBuilderTest {
      @Override
      protected Values<String> notNormalizedControlValues() {
        return new Values<>("5.0", "6.500", "text");  // invalid value results in null when parsing
      }
    }

    @Nested
    class _2 extends AbstractDoubleTextFieldBuilderTest {
      @Override
      protected Values<String> notNormalizedControlValues() {
        return new Values<>("5.0", "6.500", "1.text");  // invalid value results in incomplete parse of "1"
      }
    }
  }

  @Nested
  class StringTests extends AbstractTextFieldBuilderTest<String, StringModel> {
    @Override
    protected StringModel createModel() {
      return StringModel.of("A", Domain.regex("[A-C]"));
    }

    @Override
    protected TextField createControl(StringModel model) {
      return new TextFieldBuilder().model(model);
    }

    @Override
    protected Values<String> modelValues() {
      return new Values<>("A", "B", "!");
    }

    @Override
    protected Values<String> controlValues() {
      return new Values<>("A", "B", "!");
    }

    @Override
    protected Values<String> notNormalizedControlValues() {
      return new Values<>("A", "B", "!");
    }

    @Test
    void shouldCoerceBlankStringToNullWhenDomainAllowsIt() {
      StringModel nullableModel = StringModel.of("A", Domain.regex("[A-C]").nullable());
      TextField control = createControl(nullableModel);
      @SuppressWarnings("unused")
      Scene scene = new Scene(control);

      control.setText(" ");
      control.fireEvent(new ModelLinker.TestFocusEvent(false));

      assertThat(nullableModel.getRawValue()).isNull();
      assertThat(nullableModel.getValue()).isNull();
      assertThat(nullableModel.isValid()).isTrue();
    }

    @Test
    void shouldNotCoerceBlankStringToNullWhenDomainDoesNotAllowIt() {
      StringModel model = StringModel.of("A", Domain.regex("[A-C]"));
      TextField control = createControl(model);
      @SuppressWarnings("unused")
      Scene scene = new Scene(control);

      control.setText(" ");
      control.fireEvent(new ModelLinker.TestFocusEvent(false));

      assertThat(model.getRawValue()).isEqualTo(" ");
      assertThat(model.getValue()).isNull();
      assertThat(model.isValid()).isFalse();
    }

    @Test
    void shouldNotCoerceBlankStringToNullWhenDomainAllowsTheBlankString() {
      StringModel nullableModel = StringModel.of("A", Domain.regex("[A-C ]").nullable());
      TextField control = createControl(nullableModel);
      @SuppressWarnings("unused")
      Scene scene = new Scene(control);

      control.setText(" ");
      control.fireEvent(new ModelLinker.TestFocusEvent(false));

      assertThat(nullableModel.getRawValue()).isEqualTo(" ");
      assertThat(nullableModel.getValue()).isEqualTo(" ");
      assertThat(nullableModel.isValid()).isTrue();
    }
  }
}
