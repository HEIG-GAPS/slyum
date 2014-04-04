package swing.slyumCustomizedComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.metal.MetalButtonUI;
import swing.Slyum;

public class FlatButton extends JButton {

  private Color savedBackgroundColor;
  
  public FlatButton() {
    super();
    initialize();
  }

  public FlatButton(Action a) {
    super(a);
    initialize();
  }

  public FlatButton(Icon icon) {
    super(icon);
    initialize();
  }

  public FlatButton(String text, Icon icon) {
    super(text, icon);
    initialize();
  }

  public FlatButton(String name) {
    super(name);
    initialize();
  }

  private void initialize() {
    setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, Slyum.THEME_COLOR),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)));
    setBackground(Slyum.DEFAULT_BACKGROUND);

    setUI(new MetalButtonUI() {
      @Override
      protected void paintFocus(Graphics g, AbstractButton b,
              Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {}
    });

    addMouseListener(new MouseAdapter() {
      @Override
      public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        if (isEnabled()) {
          savedBackgroundColor = getBackground();
          setBackground(savedBackgroundColor.darker());
          setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        if (isEnabled()) {
          setBackground(savedBackgroundColor);
          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      }
    });
  }

}
