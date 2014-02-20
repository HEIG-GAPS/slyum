package swing.propretiesView;

import graphic.GraphicComponent;
import graphic.relations.LineCommentary;
import graphic.relations.LineView;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import swing.slyumCustomizedComponents.FlatPanel;
import swing.slyumCustomizedComponents.SButton;
import swing.Slyum;
import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent.UpdateMessage;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import swing.slyumCustomizedComponents.SScrollPane;

public class NoteProperties extends GlobalPropreties {
  private static NoteProperties instance;

  private JList<LineCommentary> list;
  private SButton btnDelete;

  public static NoteProperties getInstance() {
    if (instance == null) instance = new NoteProperties();
    return instance;
  }

  public NoteProperties() {
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    JPanel panel = new FlatPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

    JScrollPane scrollPane = new SScrollPane();
    panel.add(scrollPane);
    panel.add(Box.createHorizontalStrut(10));
    list = new JList<LineCommentary>() {

      @Override
      protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        repaint();
        if (!isEnabled()) {
          Graphics2D g2 = (Graphics2D)g;
          utility.Utility.setRenderQuality(g2);
          Rectangle bounds = getBounds();
          Color color = new Color(100, 100, 100, 50),
                colorText = new Color(20, 20, 20, 150);
          String text = "No link note";
          int stringWidth;
          
          g2.setColor(color);
          g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
          
          g2.setFont(Slyum.getDefaultFont().deriveFont(16.f));
          g2.setColor(colorText);
          stringWidth = g2.getFontMetrics().stringWidth(text);
          g2.drawString(text, (bounds.x + bounds.width - stringWidth) / 2, 
                        bounds.y + (bounds.height > 30 ? 30 : bounds.height));
        }
      }
      
    };
    list.setEnabled(false);
    list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    scrollPane.setViewportView(list);
    list.setModel(new ListLineCommentaryModel());
    list.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        super.mousePressed(e);
        List<LineCommentary> l = list.getSelectedValuesList();
        for (LineCommentary lc : getLineCommentary())
          lc.setSelected(l.contains(lc));
      }
    });

    list.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent e) {
        btnDelete.setEnabled(list.getSelectedIndex() != -1);
      }
    });

    btnDelete = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
            + "minus.png"), "Remove link");
    btnDelete.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        final int i = list.getSelectedIndex();

        for (LineCommentary lc : list.getSelectedValuesList())
          lc.delete();

        updateComponentInformations(null);

        SwingUtilities.invokeLater(new Runnable() {

          @Override
          public void run() {
            int j = i;
            if (i >= list.getModel().getSize()) j--;
            list.setSelectedIndex(j);
          }
        });
      }
    });
    btnDelete.setEnabled(false);
    panel.add(btnDelete);
    panel.setMaximumSize(new Dimension(400, Integer.MAX_VALUE));
    add(panel);
  }

  @Override
  public void updateComponentInformations(UpdateMessage msg) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        list.setModel(new ListLineCommentaryModel());
      }
    });
  }

  public void setSelectedItem(final LineCommentary lc) {
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        list.setSelectedValue(lc, true);
      }
    });
  }

  private class ListLineCommentaryModel extends
          AbstractListModel<LineCommentary> {
    @Override
    public LineCommentary getElementAt(int i) {
      if (currentObject == null) return null;
      return (LineCommentary) getLineCommentary().get(i);
    }

    @Override
    public int getSize() {
      if (currentObject == null) return 0;
      int size = getLineCommentary().size();
      list.setEnabled(size > 0);
      return size;
    }
  }

  private LinkedList<LineCommentary> getLineCommentary() {
    GraphicComponent gc = (GraphicComponent) currentObject;
    LinkedList<LineCommentary> ll = new LinkedList<>();
    for (LineView lv : gc.getGraphicView().getLinesViewAssociedWith(gc))
      ll.add((LineCommentary) lv);
    return ll;
  }
}
