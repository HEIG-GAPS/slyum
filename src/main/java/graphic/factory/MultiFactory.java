package graphic.factory;

import change.Change;
import classDiagram.components.ClassEntity;
import classDiagram.relationships.Multi;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.ClassView;
import graphic.entity.EntityView;
import graphic.relations.MultiView;
import swing.SPanelDiagramComponent;
import swing.Slyum;
import swing.slyumCustomizedComponents.FlatButton;
import utility.PersonalizedIcon;
import utility.Utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.util.LinkedList;

/**
 * MultiFactory allows to create a new multi-association view associated with a new association UML. Give this factory
 * at the graphic view using the method initNewComponent() for initialize a new factory. Next, graphic view will use the
 * factory to allow creation of a new component, according to the specificity of the factory.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class MultiFactory extends CreateComponent {
  private final JButton[] buttons = new JButton[2];
  private ClassView classMouseHover = null;
  private final LinkedList<ClassView> classSelected = new LinkedList<>();
  private final KeyAdapter keyListener;
  private boolean onButton = false;

  private Area subArea = new Area();

  /**
   * Create a new factory allowing the creation of a multi-association.
   *
   * @param parent the graphic view
   */
  public MultiFactory(final GraphicView parent) {
    super(parent);

    parent.unselectAll();

    parent.getScene().repaint();

    final MouseAdapter ma = new MouseAdapter() {

      @Override
      public void mouseEntered(MouseEvent e) {
        onButton = true;
        parent.getScene().setCursor(getCursor());
      }

      @Override
      public void mouseExited(MouseEvent e) {
        onButton = false;
        parent.getScene().setCursor(getCursor());
      }
    };

    buttons[0] = new FlatButton(
        PersonalizedIcon
            .createImageIcon("check-mark.png"));
    buttons[0].addMouseListener(ma);
    buttons[0].setEnabled(false);
    buttons[0].addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        createdComponent = create();
      }
    });
    parent.getScene().add(buttons[0]);
    buttons[1] = new FlatButton(
        PersonalizedIcon.createImageIcon("delete-24.png"));
    buttons[1].addMouseListener(ma);
    buttons[1].addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        parent.deleteCurrentFactory();
      }
    });
    parent.getScene().add(buttons[1]);

    AdjustmentListener listener = new AdjustmentListener() {

      @Override
      public void adjustmentValueChanged(AdjustmentEvent event) {
        locateButtons();
      }
    };

    parent.getScrollPane().getHorizontalScrollBar()
          .addAdjustmentListener(listener);

    parent.getScrollPane().getVerticalScrollBar()
          .addAdjustmentListener(listener);

    keyListener = new KeyAdapter() {

      @Override
      public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == '\n')
          buttons[0].doClick();

        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) buttons[1].doClick();
      }
    };

    parent.getScene().addKeyListener(keyListener);
    parent.getScene().requestFocusInWindow();
    locateButtons();

    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
                                                       .getBtnMulti());
  }

  private void locateButtons() {
    final int bw = 40;
    final int bh = 30;
    Rectangle rect = parent.getScene().getVisibleRect();

    buttons[0].setBounds(rect.x + 10, rect.y + 10, bw, bh);
    buttons[1].setBounds(buttons[0].getX() + buttons[0].getWidth() + 10,
                         buttons[0].getY(), bw, bh);
    parent.getScrollPane().getViewport().repaint();
  }

  /**
   * Substract the rectangle r from the area a.
   *
   * @param r the rectangle
   * @param a the area
   */
  private void addClassClipped(Rectangle r, Area a) {
    r.width++;
    r.height++;
    a.subtract(new Area(r));
  }

  @Override
  public GraphicComponent create() {
    final LinkedList<ClassEntity> ce = getClassEntity(classSelected);

    MultiView mv = null;
    if (Multi.canCreate(ce)) {
      final Multi multi = new Multi(ce);
      mv = createMulti(parent, multi);

      parent.addMultiView(mv);
      classDiagram.addMulti(multi);
    }

    parent.deleteCurrentFactory();

    return mv;
  }

  public static MultiView createMulti(GraphicView gv, Multi m) {
    MultiView mv;

    boolean isRecord = Change.isRecord();
    boolean isStopRepaint = gv.getStopRepaint();

    Change.record();
    gv.setStopRepaint(true);

    mv = new MultiView(gv, m);

    if (!isStopRepaint) gv.goRepaint();

    if (!isRecord) Change.stopRecord();

    return mv;
  }

  @Override
  public void deleteFactory() {
    super.deleteFactory();

    parent.getScene().removeKeyListener(keyListener);

    for (final JButton button : buttons)
      parent.getScene().remove(button);
  }

  @Override
  public Rectangle getBounds() {
    return parent.getScene().getVisibleRect();
  }

  /**
   * Get a list of all associated entities contains in classView.
   *
   * @param classView a list of classView
   *
   * @return an array containing all classEntity associated to classView
   */
  private LinkedList<ClassEntity> getClassEntity(LinkedList<ClassView> classView) {
    LinkedList<ClassEntity> ce = new LinkedList<ClassEntity>();
    for (final ClassView cv : classView)
      ce.add((ClassEntity) cv.getAssociatedComponent());
    return ce;
  }

  @Override
  public Cursor getCursor() {
    if (classMouseHover != null || onButton)
      return new Cursor(Cursor.HAND_CURSOR);
    else
      return new Cursor(Cursor.CROSSHAIR_CURSOR);
  }

  @Override
  public void gMouseMoved(MouseEvent e) {
    final EntityView ev = parent.getEntityAtPosition(e.getPoint());

    if (ev != null && ev.getClass() == ClassView.class) {
      if (classMouseHover != null)
        parent.getScene().repaint(classMouseHover.getBounds());

      classMouseHover = (ClassView) ev;
      parent.getScene().repaint(classMouseHover.getBounds());

      parent.getScene().setCursor(new Cursor(Cursor.HAND_CURSOR));
    } else if (classMouseHover != null) {
      parent.getScene().repaint(classMouseHover.getBounds());
      classMouseHover = null;
    }

    parent.getScene().setCursor(getCursor());
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    final EntityView ev = parent.getEntityAtPosition(e.getPoint());

    if (ev != null && ev.getClass() == ClassView.class) {
      if (!classSelected.remove(ev))

        classSelected.add((ClassView) ev);

      parent.getScene().repaint(ev.getBounds());
    }

    buttons[0].setEnabled(Multi.canCreate(getClassEntity(classSelected)));
  }

  @Override
  public boolean isAtPosition(Point position) {
    return false;
  }

  @Override
  public void paintComponent(Graphics2D g2) {
    Rectangle bounds = Utility.scaleRect(parent.getScene().getVisibleRect(),
                                         parent.getInversedScale());

    subArea = new Area(new Rectangle(bounds));

    if (classMouseHover != null)
      addClassClipped(classMouseHover.getBounds(), subArea);

    for (ClassView cv : classSelected) {
      Rectangle cvBounds = cv.getBounds();
      addClassClipped(cv.getBounds(), subArea);
      g2.setColor(Color.RED.darker());
      g2.setStroke(new BasicStroke(2.6f));
      g2.drawRect(cvBounds.x, cvBounds.y, cvBounds.width + 1,
                  cvBounds.height + 1);
    }

    g2.setColor(new Color(150, 150, 150, 120));
    g2.setClip(subArea);
    g2.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

    // Find another way for avoid clipping with buttons.
    parent.getScrollPane().getViewport().repaint();
  }

  @Override
  public void repaint() {
    parent.getScene().repaint();
  }

  @Override
  public void setBounds(Rectangle bounds) { }

}
