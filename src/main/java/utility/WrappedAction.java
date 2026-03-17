package utility;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * JavaFX equivalent of the Swing WrappedAction convenience class. Allows you to replace an
 * installed {@link EventHandler} with a custom one while still being able to invoke the original
 * handler when needed.
 *
 * <p>Subclasses must implement {@link #handle(ActionEvent)} and may call
 * {@link #invokeOriginalAction(ActionEvent)} to delegate to the wrapped handler.
 *
 * <p>Usage:
 * <pre>
 *   WrappedAction myAction = new WrappedAction(originalHandler) {
 *     {@literal @}Override
 *     public void handle(ActionEvent event) {
 *       // custom behaviour …
 *       invokeOriginalAction(event); // optionally delegate
 *     }
 *   };
 * </pre>
 */
public abstract class WrappedAction implements EventHandler<ActionEvent> {

  private final EventHandler<ActionEvent> originalHandler;

  /**
   * Wraps the given {@code originalHandler}.
   *
   * @param originalHandler the handler to wrap; may be {@code null}
   */
  public WrappedAction(EventHandler<ActionEvent> originalHandler) {
    this.originalHandler = originalHandler;
  }

  /**
   * Invoke the original handler that this action wraps.
   *
   * @param event the action event to forward
   */
  protected void invokeOriginalAction(ActionEvent event) {
    if (originalHandler != null) {
      originalHandler.handle(event);
    }
  }

  @Override
  public abstract void handle(ActionEvent event);

}
