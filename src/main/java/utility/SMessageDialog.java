package utility;

import swing.Slyum;

import javax.swing.*;
import java.awt.*;

public class SMessageDialog {
  /* ------------------------ MESSAGES FOR SLYUM --------------------------- */

  /* Warning messages */
  public static final String WARNING_OPTION_DECREASE_PERF
      = "This option can decrease performance.";

  /* Errors messages */
  public static final String ERROR_LOAD_PROPERTY_FILE =
      "Error with properties. Cannot load or save properties file.\n" +
      "Try to launch Slyum with administrators rights.";

  /* ----------------------------------------------------------------------- */

  private static final String TITLE_WINDOW = Slyum.getInstance().getName();

  public static void showErrorMessage(String message) {
    showErrorMessage(message, Slyum.getInstance());
  }

  public static void showErrorMessage(String message, Component c) {
    JOptionPane.showMessageDialog(c, message, TITLE_WINDOW,
                                  JOptionPane.ERROR_MESSAGE);
  }

  public static void showWarningMessage(String message) {
    showWarningMessage(message, Slyum.getInstance());
  }

  public static void showWarningMessage(String message, Component c) {
    JOptionPane.showMessageDialog(c, message, TITLE_WINDOW,
                                  JOptionPane.WARNING_MESSAGE);
  }

  public static void showInformationMessage(String message) {
    showInformationMessage(message, Slyum.getInstance());
  }

  public static void showInformationMessage(String message, Component c) {
    JOptionPane.showMessageDialog(c, message, TITLE_WINDOW,
                                  JOptionPane.INFORMATION_MESSAGE);
  }

  public static int showQuestionMessageYesNo(String message) {
    return showQuestionMessageYesNo(message, Slyum.getInstance());
  }

  public static int showQuestionMessageYesNo(String message, Component c) {
    return JOptionPane.showConfirmDialog(c, message, TITLE_WINDOW,
                                         JOptionPane.YES_NO_OPTION);
  }

  public static int showQuestionMessageOkCancel(String message) {
    return showQuestionMessageOkCancel(message, Slyum.getInstance());
  }

  public static int showQuestionMessageOkCancel(String message, Component c) {
    return JOptionPane.showConfirmDialog(c, message, TITLE_WINDOW,
                                         JOptionPane.OK_CANCEL_OPTION);
  }

  public static int showQuestionMessageYesNoCancel(String message) {
    return showQuestionMessageYesNoCancel(message, Slyum.getInstance());
  }

  public static int showQuestionMessageYesNoCancel(String message, Component c) {
    return JOptionPane.showConfirmDialog(c, message, TITLE_WINDOW,
                                         JOptionPane.YES_NO_CANCEL_OPTION);
  }

}
