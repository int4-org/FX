package org.int4.fx.builders.internal;

import javafx.scene.Node;
import javafx.scene.Parent;

// TODO not quite "transparent" for CSS descendant selector for now
public class VisibilityProxy extends Parent {
  private final Node delegate;

  private double width;
  private double height;

  private boolean wrapperVisible;
  private boolean wrapperManaged;

  public VisibilityProxy(Node delegate) {
    this.delegate = delegate;

    getChildren().add(delegate);

    delegate.visibleProperty().subscribe(this::updateEffectiveVisibility);
    delegate.managedProperty().subscribe(this::updateEffectiveManaged);
  }

  public void setWrapperVisible(boolean visible) {
    this.wrapperVisible = visible;

    updateEffectiveVisibility(delegate.isVisible());
  }

  public void setWrapperManaged(boolean managed) {
    this.wrapperManaged = managed;

    updateEffectiveManaged(delegate.isManaged());
  }

  @Override
  public double minWidth(double height) {
    return delegate.minWidth(height);
  }

  @Override
  public double minHeight(double width) {
    return delegate.minHeight(width);
  }

  @Override
  public double prefWidth(double height) {
    return delegate.prefWidth(height);
  }

  @Override
  public double prefHeight(double width) {
    return delegate.prefHeight(width);
  }

  @Override
  public double maxWidth(double height) {
    return delegate.maxWidth(height);
  }

  @Override
  public double maxHeight(double width) {
    return delegate.maxHeight(width);
  }

  @Override
  public boolean isResizable() {
    return delegate.isResizable();
  }

  @Override
  public void resize(double width, double height) {
    this.width = width;
    this.height = height;
  }

  @Override
  protected void layoutChildren() {
    delegate.resizeRelocate(0, 0, width, height);
  }

  private void updateEffectiveVisibility(boolean delegateVisible) {
    setVisible(wrapperVisible && delegateVisible);
  }

  private void updateEffectiveManaged(boolean delegateManaged) {
    setManaged(wrapperManaged && delegateManaged);
  }
}
