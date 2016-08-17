package graphic.entity;

import change.BufferDeepCreation;
import change.Change;
import classDiagram.components.AssociationClass;
import graphic.GraphicView;
import graphic.relations.AssociationClasseLine;
import graphic.relations.BinaryView;
import java.awt.Point;
import java.awt.Rectangle;
import swing.PanelClassDiagram;

/**
 * Represent the view of an association class in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public class AssociationClassView extends ClassView {
  private final BinaryView binaryView;
  private AssociationClasseLine acl;

  /**
   * Create a new view of the specified association class on an existing binary
   * association.
   * 
   * @param parent
   *          the graphic view
   * @param component
   *          the association class
   * @param binaryView
   *          the association view associed with the association class
   * @param bounds
   *          the default bounds
   */
  public AssociationClassView(GraphicView parent, AssociationClass component,
          BinaryView binaryView, Rectangle bounds) {
    super(parent, component);

    setBounds(bounds);

    this.binaryView = binaryView;

    if (binaryView != null) {
      final Point first = binaryView.getFirstPoint().getAnchor(), last = binaryView
              .getLastPoint().getAnchor();
      final Point posTarget = new Point(first.x + (last.x - first.x) / 2, first.x
              + (last.y - first.y) / 2);

      parent.addLineView(acl = new AssociationClasseLine(parent, this,
              binaryView, new Point(bounds.x + bounds.width / 2, bounds.y
                      + bounds.height / 2), posTarget, true));
    }
  }

  /**
   * Create a new view of an association class with the specified source and
   * target. A new binary association will be creater.
   * 
   * @param parent
   *          the graphic view
   * @param component
   *          the association class
   * @param source
   *          the source class
   * @param target
   *          the target class
   * @param posSource
   *          the point for compute association view source
   * @param posTarget
   *          the point for compute association view target
   * @param bounds
   *          the default bounds
   */
  public AssociationClassView(GraphicView parent, AssociationClass component,
          ClassView source, ClassView target, Point posSource, Point posTarget,
          Rectangle bounds) {
    super(parent, component);

    setBounds(bounds);

    binaryView = new BinaryView(parent, source, target,
            component.getAssociation(), posSource, posTarget, true);

    parent.addLineView(binaryView);
    parent.addLineView(acl = new AssociationClasseLine(parent, this,
            binaryView, new Point(bounds.x + bounds.width / 2, bounds.y
                    + bounds.height / 2), new Point(posSource.x
                    + (posTarget.x - posSource.x) / 2, posSource.y
                    + (posTarget.y + posSource.y) / 2), true));
    
    Change.push(new BufferDeepCreation(false, binaryView.getAssociedComponent()));
    Change.push(new BufferDeepCreation(true, binaryView.getAssociedComponent()));
  }

  @Override
  public void restore() {
    super.restore();
    acl.restore();
  }

  @Override
  protected void restoreEntity() {
    if (parent.getClassDiagram().searchComponentById(getAssociedComponent().getId()) == null)
      parent.getClassDiagram().addAssociationClass(
              (AssociationClass) getAssociedComponent());
  }

  public AssociationClasseLine getAcl() {
    return acl;
  }

  public BinaryView getBinaryView() {
    return binaryView;
  }

  @Override
  public void delete() {
    
    if (PanelClassDiagram.getInstance().isXmlImportation()) {
      super.delete();
    } else {
      boolean isBlocked = Change.isBlocked();
      Change.setBlocked(true);
      super.delete();
      parent.getClassDiagram().removeComponent(getAssociedComponent());
      Change.setBlocked(isBlocked);
    }
  }
}
