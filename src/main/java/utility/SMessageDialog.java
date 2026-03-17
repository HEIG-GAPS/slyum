package utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Window;
import ui.SlyumApp;

import java.util.Optional;

public class SMessageDialog {
  /* ------------------------ MESSAGES FOR SLYUM --------------------------- */

  /* Warning messages */
  public static final String WARNING_OPTION_DECREASE_PERF
      = "This option can decrease performance.";

  /* Errors messages */
  public static final String ERROR_LOAD_PROPERTY_FILE =
      "Error with properties. Cannot load or save properties file.\n" +
      "Try to launch Slyum with administrators rights.";

  /* Return value constants matching JOptionPane for backward compatibility */
  public static final int YES_OPTION = 0;
  public static final int NO_OPTION = 1;
  public static final int CANCEL_OPTION = 2;
  public static final int OK_OPTION = 0;
  public static final int CLOSED_OPTION = -1;

  /* ----------------------------------------------------------------------- */

  private static String getTitle() {
    return "Slyum";
  }

  private static Window ownerOrNull() {
    return null;
  }

  public static void showErrorMessage(String message) {
    showErrorMessage(message, ownerOrNull());
  }

  public static void showErrorMessage(String message, Window owner) {
    Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
    alert.setTitle(getTitle());
    alert.setHeaderText(null);
    if (owner != null) alert.initOwner(owner);
    alert.showAndWait();
  }

  public static void showWarningMessage(String message) {
    showWarningMessage(message, ownerOrNull());
  }

  public static void showWarningMessage(String message, Window owner) {
    Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK);
    alert.setTitle(getTitle());
    alert.setHeaderText(null);
    if (owner != null) alert.initOwner(owner);
    alert.showAndWait();
  }

  public static void showInformationMessage(String message) {
    showInformationMessage(message, ownerOrNull());
  }

  public static void showInformationMessage(String message, Window owner) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
    alert.setTitle(getTitle());
    alert.setHeaderText(null);
    if (owner != null) alert.initOwner(owner);
    alert.showAndWait();
  }

  public static int showQuestionMessageYesNo(String message) {
    return showQuestionMessageYesNo(message, ownerOrNull());
  }

  public static int showQuestionMessageYesNo(String message, Window owner) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                            ButtonType.YES, ButtonType.NO);
    alert.setTitle(getTitle());
    alert.setHeaderText(null);
    if (owner != null) alert.initOwner(owner);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isEmpty()) return CLOSED_OPTION;
    return result.get() == ButtonType.YES ? YES_OPTION : NO_OPTION;
  }

  public static int showQuestionMessageOkCancel(String message) {
    return showQuestionMessageOkCancel(message, ownerOrNull());
  }

  public static int showQuestionMessageOkCancel(String message, Window owner) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                            ButtonType.OK, ButtonType.CANCEL);
    alert.setTitle(getTitle());
    alert.setHeaderText(null);
    if (owner != null) alert.initOwner(owner);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isEmpty()) return CLOSED_OPTION;
    return result.get() == ButtonType.OK ? OK_OPTION : CANCEL_OPTION;
  }

  public static int showQuestionMessageYesNoCancel(String message) {
    return showQuestionMessageYesNoCancel(message, ownerOrNull());
  }

  public static int showQuestionMessageYesNoCancel(String message, Window owner) {
    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                            ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
    alert.setTitle(getTitle());
    alert.setHeaderText(null);
    if (owner != null) alert.initOwner(owner);
    Optional<ButtonType> result = alert.showAndWait();
    if (result.isEmpty()) return CLOSED_OPTION;
    if (result.get() == ButtonType.YES) return YES_OPTION;
    if (result.get() == ButtonType.NO) return NO_OPTION;
    return CANCEL_OPTION;
  }

}

