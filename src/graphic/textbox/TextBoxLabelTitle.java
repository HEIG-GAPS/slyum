package graphic.textbox;

import graphic.GraphicView;
import graphic.relations.LineView;
import graphic.relations.RelationGrip;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.Observable;

import javax.swing.SwingUtilities;

/**
 * A TextBox is a graphic component from Slyum containing a String. The
 * particularity of a TextBox is it text can be changed by double-clinking on
 * it.
 * 
 * A TextBoxLabel add the possibility to be moved by user in dragging mouse on
 * it. The TextBoxLabel's position is relative to it's anchor. When the anchor
 * moves, this label moves too to keep the dimension of the anchor always the
 * same.
 * 
 * A TextBox label have an associated graphic component and draw a line between
 * the label and the component when mouse hover the label. Points of the line is
 * compute in this way:
 * 
 * first point : it's the middle bounds of the label second point : it's compute
 * by calling the computeAnchor() abstract method.
 * 
 * A TextBoxLabelTitle is associated with a line component. The computeAnchor()
 * method find the nearest segment of the line and compute its middle for the
 * second point.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class TextBoxLabelTitle extends TextBoxLabel {
  private final ILabelTitle label;
  protected LineView relationView;

  /**
   * Create a new TextBoxLabelTitle associated with a line.
   * 
   * @param parent
   *          the graphic view
   * @param label
   *          a component implementing the ILabelTitle interface
   * @param relationView
   *          the line associated with the TextBox
   */
  public TextBoxLabelTitle(GraphicView parent, ILabelTitle label,
          LineView relationView) {
    super(parent, label.getLabel());

    if (relationView == null)
      throw new IllegalArgumentException("relationView is null");

    this.relationView = relationView;
    this.label = label;
    this.label.addObserver(this);
    relationView.addObserver(this);

    reinitializeLocation();
  }

  @Override
  public void reinitializeLocation() {

    deplacement = new Point(); // (0, 0)

    // Permet d'attendre que la taille de la textbox soit définie.
    SwingUtilities.invokeLater(new Runnable() {

      @Override
      public void run() {
        deplacement.x += TextBox.MARGE;
        deplacement.y += TextBox.MARGE;
        computeLabelPosition();
      }
    });
  }

  @Override
  protected Point computeAnchor() {
    final LinkedList<RelationGrip> points = relationView.getPoints();
    final int seg = relationView.getNearestSegment(new Point(bounds.x
            + bounds.width / 2, bounds.y + bounds.height / 2));

    final Point grip1 = points.get(seg).getAnchor();
    final Point grip2 = points.get(seg + 1).getAnchor();

    final int posX = (grip2.x - grip1.x) / 2 + grip1.x;
    final int posY = (grip2.y - grip1.y) / 2 + grip1.y;

    return new Point(posX, posY);
  }

  @Override
  public void computeDeplacement(Point point) {
    setBounds(new Rectangle(point.x, point.y, bounds.width, bounds.height));
    Point pos = computeAnchor();
    deplacement = new Point(point.x - pos.x, point.y - pos.y);
  }

  /**
   * Get the LineView associed with this TextBoxLabelTitle.
   * 
   * @return the LineView associed with this TextBoxLabelTitle
   */
  public LineView getLineView() {
    return relationView;
  }

  @Override
  public String getText() {
    return label.getLabel();
  }

  @Override
  public void gMouseClicked(MouseEvent e) {
    super.gMouseClicked(e);

    if (!GraphicView.isAddToSelection(e)) {
      parent.unselectAll();
      relationView.setSelected(true);
    } else {
      relationView.setSelected(!relationView.isSelected());
    }
  }

  @Override
  public void gMousePressed(MouseEvent e) {
    super.gMousePressed(e);
    maybeShowPopup(e, relationView);
  }

  @Override
  public void gMouseReleased(MouseEvent e) {
    super.gMouseReleased(e);
    maybeShowPopup(e, relationView);
  }

  @Override
  public void setText(String text) {
    super.setText(text);
    label.setLabel(text);
    label.notifyObservers();
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    super.update(arg0, arg1);
    super.setText(getText());
  }

}
