package swing.slyumCustomizedComponents;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.FocusManager;
import javax.swing.JTextField;
import javax.swing.text.Document;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public class TextFieldWithPrompt extends JTextField {
  
  private String placeholder = "";

  public TextFieldWithPrompt() {
  }

  public TextFieldWithPrompt(String text) {
    super(text);
  }

  public TextFieldWithPrompt(String text, String placeholder) {
    super(text);
    this.placeholder = placeholder;
  }

  public TextFieldWithPrompt(int columns) {
    super(columns);
  }

  public TextFieldWithPrompt(String text, int columns) {
    super(text, columns);
  }

  public TextFieldWithPrompt(Document doc, String text, int columns) {
    super(doc, text, columns);
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }
  
  @Override
  protected void paintComponent(java.awt.Graphics g) {
    super.paintComponent(g);

    if(getText().isEmpty() && 
       !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)){
      Graphics2D g2 = (Graphics2D)g.create();
      g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                          RenderingHints.VALUE_ANTIALIAS_ON);
      g2.setColor(getDisabledTextColor());
      g2.setFont(getFont().deriveFont(Font.ITALIC));
      g2.drawString(placeholder, getInsets().left, 
                    g.getFontMetrics().getMaxAscent() + getInsets().top);
    }
  }
}
