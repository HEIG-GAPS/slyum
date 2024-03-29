package utility;

import swing.Slyum;

import javax.swing.*;
import java.net.URL;

public class PersonalizedIcon {
  public final static String PATH_ICON = Slyum.ICON_PATH;
  public final static String PATH_WARNING_ICON = "warning.png";
  public final static String PATH_QUESTION_ICON = "question.png";
  public final static String PATH_INFO_ICON = "information.png";
  public final static String PATH_ERROR_ICON = "error.png";

  /**
   * Create a new ImageIcon with the specified path. If path is invalid a null object is returned.
   *
   * @param imagePath the path for create a new ImageIcon
   *
   * @return the ImageIcon created
   */
  public static ImageIcon createImageIcon(String imagePath) {
    URL imageURL = Slyum.class.getResource(Slyum.ICON_PATH + imagePath);

    ImageIcon icon = null;

    if (imageURL != null) icon = new ImageIcon(imageURL);

    return icon;
  }

  public static ImageIcon getWarningIcon() {
    return createImageIcon(PATH_WARNING_ICON);
  }

  public static ImageIcon getQuestionIcon() {
    return createImageIcon(PATH_QUESTION_ICON);
  }

  public static ImageIcon getErrorIcon() {
    return createImageIcon(PATH_ERROR_ICON);
  }

  public static ImageIcon getInfoIcon() {
    return createImageIcon(PATH_INFO_ICON);
  }

  public static ImageIcon getLogo() {
    return createImageIcon("logo32.png");
  }

}
