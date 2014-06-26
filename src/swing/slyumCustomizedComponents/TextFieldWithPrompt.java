package swing.slyumCustomizedComponents;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.FocusManager;
import javax.swing.JTextField;
import javax.swing.text.Document;
import swing.Slyum;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public class TextFieldWithPrompt extends JTextField {
  
  private final static int BORDER_SIZE = 3;
  
  private String placeholder = "";
  private Color defaultBorderColor = Slyum.DEFAULT_BORDER_COLOR;
  private boolean isMouseHover;

  public TextFieldWithPrompt() {
    initialize();
  }

  public TextFieldWithPrompt(String text) {
    super(text);
    initialize();
  }

  public TextFieldWithPrompt(String text, String placeholder) {
    super(text);
    initialize();
    this.placeholder = placeholder;
  }

  public TextFieldWithPrompt(int columns) {
    super(columns);
    initialize();
  }

  public TextFieldWithPrompt(String text, int columns) {
    super(text, columns);
    initialize();
  }

  public TextFieldWithPrompt(Document doc, String text, int columns) {
    super(doc, text, columns);
    initialize();
  }
  
  private void initialize() {
    setBorder(BorderFactory.createEmptyBorder(
        BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE));
    
    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(MouseEvent e) {
        isMouseHover = true;
      }

      @Override
      public void mouseExited(MouseEvent e) {
        isMouseHover = false;
      }
    });
  }

  public String getPlaceholder() {
    return placeholder;
  }

  public void setPlaceholder(String placeholder) {
    this.placeholder = placeholder;
  }
  
  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    repaint();
    Graphics2D g2 = (Graphics2D)g.create();
    utility.Utility.setRenderQuality(g2);
    
    Insets bounds = getInsets();
    Dimension size = getSize();
    
    if (isFocusOwner())
      g2.setColor(Slyum.THEME_COLOR);
    else if (isMouseHover)
      g2.setColor(defaultBorderColor.darker());
    else
      g2.setColor(defaultBorderColor);
    
    g2.drawRect(bounds.left - BORDER_SIZE, 
                bounds.top - BORDER_SIZE, size.width - 1, size.height - 1);
    
    if(getText().isEmpty() && 
       !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)){
      g2.setColor(getDisabledTextColor().darker());
      g2.setFont(getFont().deriveFont(Font.ITALIC));
      g2.drawString(placeholder, getInsets().left, 
                    g.getFontMetrics().getMaxAscent() + getInsets().top);
    }
    g2.dispose();
  }
}
