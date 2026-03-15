package ui;

import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX migration stub for the main application class.
 * Bridges to {@link swing.Slyum} during the migration period until the full
 * swing package is replaced by a proper JavaFX Application subclass.
 *
 * @author migration
 */
public class SlyumApp {

  /** Path prefix used for loading bundled icon resources. */
  public static final String ICON_PATH = swing.Slyum.ICON_PATH;

  private static SlyumApp instance;
  private Stage primaryStage;

  /** Returns the singleton application instance, or {@code null} if not yet initialised. */
  public static SlyumApp getInstance() {
    return instance;
  }

  /** Called by the JavaFX Application start() method to register the singleton. */
  public static void setInstance(SlyumApp app) {
    instance = app;
  }

  /** Human-readable application name used in dialog titles. */
  public String getName() {
    return "Slyum";
  }

  /**
   * Returns the default font for drawing diagram labels.
   * Falls back to the system default font when the Swing layer is not available.
   */
  public static Font getDefaultFont() {
    java.awt.Font awtFont = swing.Slyum.getDefaultFont();
    if (awtFont != null) {
      return Font.font(awtFont.getFamily(), awtFont.getSize());
    }
    return Font.getDefault();
  }

  public Stage getPrimaryStage() {
    return primaryStage;
  }

  public void setPrimaryStage(Stage stage) {
    this.primaryStage = stage;
  }

}
