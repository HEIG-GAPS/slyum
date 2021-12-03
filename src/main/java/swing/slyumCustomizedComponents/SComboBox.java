package swing.slyumCustomizedComponents;

import swing.Slyum;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.BasicListUI;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Vector;

/**
 * @param <E> TODO
 *
 * @author David Miserez
 */
public class SComboBox<E> extends JComboBox<E> {

  public SComboBox(ComboBoxModel<E> aModel) {
    super(aModel);
    initialize();
  }

  public SComboBox(E[] items) {
    super(items);
    initialize();
  }

  public SComboBox(Vector<E> items) {
    super(items);
    initialize();
  }

  public SComboBox() {
    initialize();
  }

  private void initialize() {
    setBorder(BorderFactory.createLineBorder(Slyum.DEFAULT_BORDER_COLOR));

    addMouseListener(new MouseAdapter() {

      @Override
      public void mouseEntered(MouseEvent e) {
        if (!isFocusOwner() && isEnabled())
          setBorder(BorderFactory.createLineBorder(
              Slyum.DEFAULT_BORDER_COLOR.darker()));
      }

      @Override
      public void mouseExited(MouseEvent e) {
        if (!isFocusOwner())
          setBorder(BorderFactory.createLineBorder(
              Slyum.DEFAULT_BORDER_COLOR));
      }
    });

    addFocusListener(new FocusListener() {

      @Override
      public void focusGained(FocusEvent e) {
        setBorder(BorderFactory.createLineBorder(Slyum.THEME_COLOR));
      }

      @Override
      public void focusLost(FocusEvent e) {
        setBorder(BorderFactory.createLineBorder(Slyum.DEFAULT_BORDER_COLOR));
      }
    });

    setUI(new BasicComboBoxUI() {

      @Override
      protected JButton createArrowButton() {
        return new JButton() {{
          setBorderPainted(false);
          setUI(new BasicButtonUI() {

            @Override
            public void paint(Graphics g, JComponent c) {
              Graphics2D g2 = (Graphics2D) g;

              utility.Utility.setRenderQuality(g);
              Rectangle bounds = new Rectangle(c.getWidth(), c.getHeight());

              // Background
              if (isEnabled())
                g.setColor(Color.WHITE);
              else
                g.setColor(Color.LIGHT_GRAY);

              g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

              // Arrow
              g.setColor(Slyum.DEFAULT_BORDER_COLOR.darker());
              float edgeX = (float) bounds.width / 4.f,
                  edgeY = (float) bounds.height / 9.f;

              Path2D arrow = new Path2D.Float();
              arrow.moveTo(edgeX, edgeY * 3);
              arrow.lineTo(edgeX * 2, edgeY * 6);
              arrow.lineTo(edgeX * 3, edgeY * 3);
              arrow.lineTo(edgeX, edgeY * 3);
              g2.fill(arrow);
            }
          });
        }};
      }

      @Override
      protected ComboPopup createPopup() {
        return new BasicComboPopup(comboBox) {
          {
            setBorder(
                BorderFactory.createLineBorder(Slyum.THEME_COLOR));
          }

          @Override
          protected JList createList() {
            JList l = super.createList();
            l.setUI(new BasicListUI() {

              @Override
              protected void paintCell(
                  Graphics g, int row, Rectangle rowBounds,
                  ListCellRenderer cellRenderer, ListModel dataModel,
                  ListSelectionModel selModel, int leadIndex) {
                utility.Utility.setRenderQuality(g);
                Graphics2D g2 = (Graphics2D) g;
                int mouseHoverIndex = selModel.getLeadSelectionIndex();

                // Drawin' background
                g2.setColor(Color.WHITE);
                g2.fillRect(rowBounds.x, rowBounds.y, rowBounds.width, rowBounds.height);

                // Drawin' mouse hover lightning
                g2.setColor(Slyum.THEME_COLOR);
                g2.fillRect(rowBounds.x, rowBounds.height * mouseHoverIndex,
                            rowBounds.width, rowBounds.height);

                // Drain' text
                g2.setColor(mouseHoverIndex == row ? Color.WHITE : getForeground());
                g2.drawString(
                    dataModel.getElementAt(row).toString(), rowBounds.x + 5,
                    rowBounds.y + g2.getFontMetrics().getMaxAscent());
              }
            });
            return l;
          }

          @Override
          public void paint(Graphics g) {
            utility.Utility.setRenderQuality(g);
            super.paint(g);
          }
        };
      }

      @Override
      protected ComboBoxEditor createEditor() {
        return new BasicComboBoxEditor() {
          @Override
          protected JTextField createEditorComponent() {
            JTextField textField = super.createEditorComponent();
            textField.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
            return textField;
          }
        };
      }

      @Override
      public void paintCurrentValue(
          Graphics g, Rectangle bounds, boolean hasFocus) {
        g.setColor(Color.BLACK);
        g.drawString(
            comboBox.getSelectedItem().toString(),
            bounds.x + 3, bounds.y +
                          ((bounds.height + g.getFontMetrics().getMaxAscent()) / 2) - 2);
      }

      @Override
      public void paintCurrentValueBackground(
          Graphics g, Rectangle bounds, boolean hasFocus) {

        if (isEnabled())
          g.setColor(Color.WHITE);
        else
          g.setColor(Color.LIGHT_GRAY);

        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
      }

      @Override
      public void paint(Graphics g, JComponent c) {
        utility.Utility.setRenderQuality(g);
        super.paint(g, c);
      }
    });
  }

}
