package swing.slyumCustomizedComponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicRadioButtonMenuItemUI;
import swing.Slyum;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public class SRadioButtonMenuItem extends JRadioButtonMenuItem {

  private boolean isMouseHover = false,
                  isMousePressed = false;

  public SRadioButtonMenuItem() {
    initialize();
  }

  public SRadioButtonMenuItem(Icon icon) {
    super(icon);
    initialize();
  }

  public SRadioButtonMenuItem(String text) {
    super(text);
    initialize();
  }

  public SRadioButtonMenuItem(Action a) {
    super(a);
    initialize();
  }

  public SRadioButtonMenuItem(String text, Icon icon) {
    super(text, icon);
    initialize();
  }

  public SRadioButtonMenuItem(String text, boolean selected) {
    super(text, selected);
    initialize();
  }

  public SRadioButtonMenuItem(Icon icon, boolean selected) {
    super(icon, selected);
    initialize();
  }

  public SRadioButtonMenuItem(String text, Icon icon, boolean selected) {
    super(text, icon, selected);
    initialize();
  }
  
  private void initialize() {
    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        isMouseHover = true;
      }

      @Override
      public void mouseExited(MouseEvent e) {
        isMouseHover = false;
      }

      @Override
      public void mousePressed(MouseEvent e) {
        isMousePressed = true;
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        isMousePressed = false;
      }
    });
    
    setUI(new BasicRadioButtonMenuItemUI() {
      
      @Override
      protected void paintMenuItem(Graphics g, JComponent c, Icon checkIcon, Icon arrowIcon, Color background, Color foreground, int defaultTextIconGap) {
        
        paintBackground(g, menuItem, background);
        
        final int SIZE = 10;
        Graphics2D g2 = (Graphics2D)g;
        utility.Utility.setRenderQuality(g2);
        Rectangle bounds = c.getBounds(),
                  checkBox = new Rectangle(
                      getText().isEmpty() ? (bounds.width - SIZE) / 2 : 4,
                      (bounds.height - SIZE) / 2, SIZE, SIZE);
        Color borderColor = Slyum.DEFAULT_BORDER_COLOR,
              checkBackground = Color.WHITE,
              checkColor = Slyum.THEME_COLOR,
              disabledColor = new Color(150, 150, 150, 150);
        
        if (isEnabled()) {
          if (isMouseHover || isFocusOwner())
            if (isMousePressed) {
              checkBackground = new Color(220, 220, 220);
              borderColor = Slyum.DEFAULT_BORDER_COLOR.darker().darker();
            } else {
              borderColor = Slyum.DEFAULT_BORDER_COLOR.darker();
            }
        }
        
        g2.setColor(checkBackground);
        g2.fillOval(checkBox.x, checkBox.y, checkBox.width, checkBox.height);
        g2.setColor(borderColor);
        g2.drawOval(checkBox.x, checkBox.y, checkBox.width, checkBox.height);
        
        // Dessin du "check"
        if (isSelected()) {
          g2.translate(checkBox.x, checkBox.y);
          g2.setColor(checkColor);
          g2.fillOval(3, 3, SIZE - 5, SIZE - 5);
          g2.translate(-checkBox.x, -checkBox.y);
        }
        
        if (!isEnabled()) {
          g2.setColor(disabledColor);
          g2.fillOval(checkBox.x, checkBox.y, checkBox.width, checkBox.height);
        }
        
        g2.setColor(getForeground());
        g2.setFont(UIManager.getFont("RadioButton.font"));
        g2.drawString(
            getText(), 
            checkBox.x + checkBox.width + 5, g2.getFontMetrics().getMaxAscent() + checkBox.y - 3);
      }
    });
  }
}
