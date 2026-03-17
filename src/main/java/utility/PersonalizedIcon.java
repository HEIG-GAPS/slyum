package utility;

import javafx.scene.image.Image;
import javax.swing.ImageIcon;
import ui.SlyumApp;

import java.net.URL;

public class PersonalizedIcon {
  public final static String PATH_ICON = SlyumApp.ICON_PATH;
  public final static String PATH_WARNING_ICON = "warning.png";
  public final static String PATH_QUESTION_ICON = "question.png";
  public final static String PATH_INFO_ICON = "information.png";
  public final static String PATH_ERROR_ICON = "error.png";

  /**
   * Create a new Image with the specified path. If the path is invalid, {@code null} is returned.
   *
   * @param imagePath the path for creating a new Image
   *
   * @return the Image created, or {@code null} if the resource was not found
   */
  public static Image createImageIcon(String imagePath) {
    URL imageURL = SlyumApp.class.getResource(SlyumApp.ICON_PATH + imagePath);

    if (imageURL == null) return null;

    return new Image(imageURL.toExternalForm());
  }

  public static Image getWarningIcon() {
    return createImageIcon(PATH_WARNING_ICON);
  }

  public static Image getQuestionIcon() {
    return createImageIcon(PATH_QUESTION_ICON);
  }

  public static Image getErrorIcon() {
    return createImageIcon(PATH_ERROR_ICON);
  }

  public static Image getInfoIcon() {
    return createImageIcon(PATH_INFO_ICON);
  }

  /**
   * Create a Swing {@link ImageIcon} from the bundled icon at the given path.
   * Used by Swing components that have not yet been migrated to JavaFX.
   *
   * @param imagePath relative path inside the icon directory
   *
   * @return the {@link ImageIcon}, or {@code null} if the resource was not found
   */
  public static ImageIcon createSwingImageIcon(final String imagePath) {
    java.net.URL imageURL = SlyumApp.class.getResource(SlyumApp.ICON_PATH + imagePath);
    if (imageURL == null) return null;
    return new ImageIcon(imageURL);
  }

  public static Image getLogo() {
    return createImageIcon("logo32.png");
  }

}

