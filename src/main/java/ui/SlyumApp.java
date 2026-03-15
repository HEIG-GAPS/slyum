package ui;

import javafx.application.Application;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * JavaFX Application entry point for Slyum.
 *
 * <p>This class serves as the main entry point during the Swing-to-JavaFX
 * migration. It bootstraps the JavaFX runtime and will progressively replace
 * {@link swing.Slyum} as the primary window is migrated.
 *
 * @author migration
 */
public class SlyumApp extends Application {

    /** Path prefix used for loading bundled icon resources. */
    public static final String ICON_PATH = swing.Slyum.ICON_PATH;

    private static SlyumApp instance;

    /** Returns the singleton application instance, or {@code null} if not yet initialised. */
    public static SlyumApp getInstance() {
        return instance;
    }

    /**
     * JavaFX entry point. Stores the singleton and delegates to the
     * Swing-based main window until the full migration is complete.
     *
     * @param primaryStage the primary stage provided by the JavaFX runtime
     */
    @Override
    public void start(final Stage primaryStage) {
        instance = this;
        primaryStage.setTitle("Slyum");
        // TODO: replace with a fully JavaFX-based scene once migration is complete.
        // For now, the Swing main window is launched via the legacy entry point.
        swing.Slyum.launchSwingUI(primaryStage);
    }

    /**
     * Returns the default font for drawing diagram labels.
     * Falls back to the system default font when the Swing layer is not available.
     *
     * @return the default {@link Font}
     */
    public static Font getDefaultFont() {
        java.awt.Font awtFont = swing.Slyum.getDefaultFont();
        if (awtFont != null) {
            return Font.font(awtFont.getFamily(), awtFont.getSize());
        }
        return Font.getDefault();
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments forwarded to the JavaFX runtime
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
