package swing.slyumCustomizedComponents;

import swing.MultiViewManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

/**
 * @author David Miserez
 */
public class SColorMenuItem extends JMenu {

  public SColorMenuItem(String text) {
    super("Recent Color");
    setEnabled(false);
  }

  public void updateColors(Color... colors) {
    if (colors.length == 0 || getItemCount() > colors.length)
      throw new IllegalArgumentException("You cannot update colors with 0 or " +
                                         "less than the current color's number.");

    createMenuItems(colors.length);

    // Update item menu's color and display it.
    boolean atLeastOneColor = false;
    for (int i = 0; i < colors.length; ++i)
      if (colors[i] != null) {
        getItem(i).setColor(colors[i]);
        atLeastOneColor = true;
      }

    setEnabled(atLeastOneColor);
  }

  @Override
  public JMenuItem add(JMenuItem menuItem) {
    if (!(menuItem instanceof MenuItemColor))
      throw new IllegalArgumentException(
          "You can only add instances of MenuItemColor in a SColorMenuItem.");

    return super.add(menuItem);
  }

  @Override
  public MenuItemColor getItem(int pos) {
    if (pos < getItemCount())
      return (MenuItemColor) super.getItem(pos);
    return null;
  }

  @Override
  public final void setEnabled(boolean b) {
    if (getItemCount() == 0)
      b = false;
    super.setEnabled(b);
  }

  private void createMenuItems(int size) {
    for (int i = 0; i < size; ++i)
      if (getItem(i) == null)
        add(new MenuItemColor(i));
  }

  public class MenuItemColor extends JMenuItem {
    private final short SIZE = 12;

    private Color color;

    private MenuItemColor(int index) {
      super("Color " + ++index);
      setVisible(false);

      addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          MultiViewManager.getSelectedGraphicView()
                          .setColorForSelectedItems(color);
        }
      });
    }

    private void setColor(Color color) {
      this.color = color;
      setIcon(getColoredIcon(color));
      setVisible(true);
    }

    private Icon getColoredIcon(Color color) {
      BufferedImage image =
          new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

      Graphics2D g2d = image.createGraphics();
      final short size = SIZE - 1;

      g2d.setColor(color);
      g2d.fillRect(0, 0, size, size);

      g2d.setColor(color.darker());
      g2d.drawRect(0, 0, size, size);

      return new ImageIcon(image);
    }

  }

}
