package graphic.relations;

import change.Change;
import classDiagram.IDiagramComponent;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.PanelClassDiagram;
import utility.Utility;

import java.awt.*;

/**
 * The LineView class represent a collection of lines making a link between two GraphicComponent. When it creates, the
 * LineView have one single line between the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a segment between each grips. Grips are
 * movable and a LineView have two special grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * <p>
 * An AssociationClasseLine is a line between an AssociationClassView and an AssociationView.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class AssociationClasseLine extends LineView {
  /**
   * Create a new AssociationClasseLine between source and target.
   *
   * @param graphicView the {@link GraphicView}.
   * @param source the entity source
   * @param target the association target
   * @param posSource the position for put the first MagneticGrip
   * @param posTarget the position for put the last MagneticGrip
   * @param checkRecursivity check if the relation is on itself
   */
  public AssociationClasseLine(final GraphicView graphicView, final EntityView source, final AssociationView target,
                               final Point posSource, final Point posTarget, final boolean checkRecursivity) {
    super(graphicView, source, target, posSource, posTarget, checkRecursivity);
    lineStroke = new BasicStroke(LINE_WIDTH, BasicStroke.CAP_BUTT,
                                 BasicStroke.JOIN_MITER, 10.0f, new float[] {6.5f}, 0.0f);
  }

  @Override
  public IDiagramComponent getAssociatedXmlElement() {
    return getFirstPoint().getAssociedComponentView().getAssociatedComponent();
  }

  @Override
  public boolean relationChanged(MagneticGrip gripSource,
                                 GraphicComponent target) {
    // Le changement de relation pour les classes d'association
    // n'est pas implémenté.
    return false;
  }

  @Override
  public String getXmlTagName() {
    return "relationView";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element relationView = doc.createElement(getXmlTagName()), line = doc
        .createElement("line");

    relationView.setAttribute("relationId",
                              String.valueOf(getAssociatedXmlElement().getId()));
    relationView.setAttribute("color", String.valueOf(getColor().getRGB()));

    for (RelationGrip grip : points) {
      Point pt = grip.getAnchor();
      pt.translate(1, 1);
      line.appendChild(Utility.pointToXmlElement(pt, "point", doc));
    }

    relationView.appendChild(line);
    return relationView;
  }

  @Override
  public void delete() {

    if (PanelClassDiagram.getInstance().isXmlImportation()) {
      super.delete();
    } else {

      boolean record = Change.isRecord();
      Change.record();

      super.delete();

      GraphicComponent classView = getFirstPoint().getAssociedComponentView();
      if (parent.containsComponent(classView))
        if (getIsLightDelete())
          classView.lightDelete();
        else
          classView.delete();

      if (!record)
        Change.stopRecord();
    }
  }

}
