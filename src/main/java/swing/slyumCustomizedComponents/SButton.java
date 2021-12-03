package swing.slyumCustomizedComponents;

import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

public class SButton extends JButton {

  private final Color BACKGROUND = Color.WHITE;
  private LinkedList<Component> linkedComponents = new LinkedList<>();

  public SButton() {
    super();
    init("", "", null);
  }

  public SButton(Icon icon, String tooltip) {
    super(icon);
    init("", tooltip, null);
  }

  public SButton(Icon icon, String action, String tooltip, ActionListener al) {
    super(icon);
    init(action, tooltip, al);
  }

  private void init(String action, String tooltip, ActionListener al) {

    if (getIcon() != null)
      setPreferredSize(new Dimension(getIcon().getIconWidth(),
                                     getIcon().getIconHeight()));
    setActionCommand(action);
    addActionListener(al);
    setContentAreaFilled(false);
    setBorderPainted(false);
    setBackground(BACKGROUND);
    setToolTipText(tooltip);

    setUI(new MetalButtonUI() {
      @Override
      protected void paintFocus(Graphics g, AbstractButton b,
                                Rectangle viewRect, Rectangle textRect, Rectangle iconRect) { }
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

  @Override
  public void setEnabled(boolean b) {
    super.setEnabled(b);

    for (Component c : linkedComponents)
      c.setEnabled(b);
  }

  public void linkComponent(Component c) {
    if (!linkedComponents.contains(c))
      linkedComponents.add(c);
  }

}
