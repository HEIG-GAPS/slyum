package swing.slyumCustomizedComponents;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.metal.MetalButtonUI;

import swing.SlyumAction;

public class SButton extends JButton {
  
  private final Color BACKGROUND = Color.WHITE;
  
  public SButton(SlyumAction a) {
    super(a);
    Icon icon = (Icon)a.getValue(Action.SMALL_ICON);
    setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
    setContentAreaFilled(false);
    setBorderPainted(false);
    setBackground(BACKGROUND);

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
          if (getBackground().equals(Color.white)) {
            setBackground(BACKGROUND);
            setContentAreaFilled(true);
          }
          setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
      }

      @Override
      public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        if (getBackground().equals(BACKGROUND)) setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    });
  }
  
  public void resetBackground() {
    setContentAreaFilled(false);
    setBackground(BACKGROUND);
  }
}
