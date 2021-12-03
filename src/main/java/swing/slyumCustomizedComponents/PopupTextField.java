package swing.slyumCustomizedComponents;

import swing.Slyum;

import javax.swing.*;
import javax.swing.text.Document;

/**
 * @author David Miserez
 */
public class PopupTextField extends JTextField {

  public PopupTextField() {
    initialize();
  }

  public PopupTextField(String text) {
    super(text);
    initialize();
  }

  public PopupTextField(int columns) {
    super(columns);
    initialize();
  }

  public PopupTextField(String text, int columns) {
    super(text, columns);
    initialize();
  }

  public PopupTextField(Document doc, String text, int columns) {
    super(doc, text, columns);
    initialize();
  }

  private void initialize() {
    setBorder(BorderFactory.createLineBorder(Slyum.THEME_COLOR, 1));
  }

}
