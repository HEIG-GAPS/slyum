/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package swing.slyumCustomizedComponents;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.text.Document;
import swing.Slyum;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
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
