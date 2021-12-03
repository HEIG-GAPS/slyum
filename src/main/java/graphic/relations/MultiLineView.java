package graphic.relations;

import classDiagram.relationships.Multi;
import classDiagram.relationships.Role;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.textbox.TextBoxRole;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import utility.Utility;

import java.awt.*;

/**
 * The LineView class represent a collection of lines making a link between two GraphicComponent. When it creates, the
 * LineView have one single line between the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a segment between each grips. Grips are
 * movable and a LineView have two special grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * <p>
 * A MultiLineView is a LineView associated with a Multi-association view.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class MultiLineView extends LineView {
  /**
   * Create a new MultiLineView associated with a MultiView.
   *
   * @param graphicView the graphic view
   * @param source the multi view
   * @param target A entity participating at the multi-association
   * @param role the role of the association
   * @param posSource the position for put the first MagneticGrip
   * @param posTarget the position for put the last MagneticGrip
   * @param checkRecursivity check if the relation is on itself
   */
  public MultiLineView(GraphicView graphicView, MultiView source,
                       EntityView target, Role role, Point posSource, Point posTarget,
                       boolean checkRecursivity) {
    super(graphicView, source, target, posSource, posTarget, checkRecursivity);

    final TextBoxRole tb = new TextBoxRole(parent, role, getLastPoint());
    tbRoles.add(tb);
    parent.addOthersComponents(tb);
  }

  @Override
  public void delete() {
    MultiView mv = (MultiView) getFirstPoint().getAssociedComponentView();

    if (getIsLightDelete())
      mv.lightDelete();

    super.delete();

    if (!getIsLightDelete())
      mv.connexionRemoved(this);

    if (parent.getLinesViewAssociedWith(mv).size() < 3)
      mv.delete();
  }

  @Override
  public void restore() {
    if (parent.searchAssociedComponent(((TextBoxRole) tbRoles.getFirst()).getRole()) != null)
      return;

    MultiView mv = (MultiView) getFirstPoint().getAssociedComponentView();
    Multi m = (Multi) mv.getAssociatedComponent();
    TextBoxRole tbr = (TextBoxRole) tbRoles.getFirst();

    super.restore();

    mv.addMultiLineView(this);

    if (!m.containsRole(tbr.getRole()))
      m.addRole(tbr.getRole(), false);
  }

  @Override
  public void addLineViewToParent() {

  }

  @Override
  public String getXmlTagName() {
    return "multiLineView";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element multiLineView = doc.createElement(getXmlTagName()), line = doc
        .createElement("line");

    multiLineView.setAttribute(
        "relationId",
        String.valueOf(getFirstPoint().getAssociedComponentView()
                                      .getAssociatedComponent().getId()));
    multiLineView.setAttribute("color", String.valueOf(getColor().getRGB()));

    for (RelationGrip grip : points) {
      Point pt = grip.getAnchor();
      line.appendChild(Utility.pointToXmlElement(pt, "point", doc));
    }
    multiLineView.appendChild(line);

    // Si l'association a des textbox de rôles.
    if (tbRoles.size() >= 1) {
      multiLineView.appendChild(Utility.boundsToXmlElement(doc, tbRoles.get(0)
                                                                       .getBounds(), "roleAssociation"));

      multiLineView.appendChild(Utility.boundsToXmlElement(doc,
                                                           ((TextBoxRole) tbRoles.get(0)).getTextBoxMultiplicity()
                                                                                         .getBounds(),
                                                           "multipliciteAssociation"));
    }

    return multiLineView;
  }

  @Override
  public boolean relationChanged(MagneticGrip gripSource,
                                 GraphicComponent tagret) {
    // Le changement pour les multi-association n'est pas implémenté.
    return false;
  }

}
