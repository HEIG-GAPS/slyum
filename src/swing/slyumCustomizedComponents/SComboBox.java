package swing.slyumCustomizedComponents;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.ComboPopup;
import swing.Slyum;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 * @param <E> TODO
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
        if (!isFocusOwner())
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
              Graphics2D g2 = (Graphics2D)g;
              
              utility.Utility.setRenderQuality(g);
              Rectangle bounds = new Rectangle(c.getWidth(), c.getHeight());
              
              // Background
              g.setColor(Color.WHITE);
              g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
              
              // Arrow
              g.setColor(Slyum.DEFAULT_BORDER_COLOR.darker());
              float edgeX = (float)bounds.width / 4.f,
                    edgeY = (float)bounds.height / 9.f;
              
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
        return super.createPopup();
      }

      @Override
      protected ComboBoxEditor createEditor() {
        return new BasicComboBoxEditor() {
          @Override
          protected JTextField createEditorComponent() {
            return new JTextField(){
              { setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));}
            };
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
        g.setColor(Color.WHITE);
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
