package swing.slyumCustomizedComponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicCheckBoxUI;
import swing.Slyum;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 */
public class SCheckBox extends JCheckBox {

  private boolean isMouseHover = false,
                  isMousePressed = false;
  
  public SCheckBox() {
    initialize();
  }

  public SCheckBox(Icon icon) {
    super(icon);
    initialize();
  }

  public SCheckBox(Icon icon, boolean selected) {
    super(icon, selected);
    initialize();
  }

  public SCheckBox(String text) {
    super(text);
    initialize();
  }

  public SCheckBox(Action a) {
    super(a);
    initialize();
  }

  public SCheckBox(String text, boolean selected) {
    super(text, selected);
    initialize();
  }

  public SCheckBox(String text, Icon icon) {
    super(text, icon);
    initialize();
  }

  public SCheckBox(String text, Icon icon, boolean selected) {
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
    
    setUI(new BasicCheckBoxUI() {

      @Override
      public synchronized void paint(Graphics g, JComponent c) {
        final int SIZE = 12;
        Graphics2D g2 = (Graphics2D)g;
        utility.Utility.setRenderQuality(g2);
        Rectangle bounds = c.getBounds(),
                  checkBox = new Rectangle(
                      getText().isEmpty() ? (bounds.width - SIZE) / 2 : 0,
                      (bounds.height - SIZE) / 2, SIZE, SIZE);
        Color borderColor = Slyum.DEFAULT_BORDER_COLOR,
              background = Color.WHITE,
              checkColor = Slyum.THEME_COLOR,
              disabledColor = new Color(150, 150, 150, 150);
        
        if (isEnabled()) {
          if (isMouseHover || isFocusOwner())
            if (isMousePressed) {
              background = new Color(220, 220, 220);
              borderColor = Slyum.DEFAULT_BORDER_COLOR.darker().darker();
            } else {
              borderColor = Slyum.DEFAULT_BORDER_COLOR.darker();
            }
        }
        
        g2.setColor(background);
        g2.fillRect(checkBox.x, checkBox.y, checkBox.width, checkBox.height);
        g2.setColor(borderColor);
        g2.drawRect(checkBox.x, checkBox.y, checkBox.width, checkBox.height);
        
        // Dessin du "check"
        if (isSelected()) {
          int[] xpoints = new int[] {2, 5, 11, 9, 5, 4},
                ypoints = new int[] {8, 11, 5, 2, 7, 5};
          Polygon check = new Polygon(xpoints, ypoints, 6);
          g2.translate(checkBox.x, checkBox.y);
          g2.setColor(checkColor);
          g2.fill(check);
          g2.translate(-checkBox.x, -checkBox.y);
        }
        
        if (!isEnabled()) {
          g2.setColor(disabledColor);
          g2.fillRect(checkBox.x, checkBox.y, checkBox.width, checkBox.height);
        }
        
        g2.setColor(getForeground());
        g2.setFont(UIManager.getFont("CheckBox.font"));
        g2.drawString(
            getText(), 
            checkBox.x + checkBox.width + 5, g2.getFontMetrics().getMaxAscent() + checkBox.y - 3);
      }
    });
  }
}
