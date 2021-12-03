package graphic.relations;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Association;
import classDiagram.relationships.Association.NavigateDirection;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.textbox.TextBoxLabelTitle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.Observable;

/**
 * The LineView class represent a collection of lines making a link between two GraphicComponent. When it creates, the
 * LineView have one single line between the two GraphicComponent. By clicking on the line, the user can personnalize
 * the LineView by adding new grips. When drawing, the LineView will draw a segment between each grips. Grips are
 * movable and a LineView have two special grips; MagneticGrip. These grips are associated with a GraphicComponent and
 * can't be placed elsewhere.
 * <p>
 * A RelationView have an associated UML component.
 * <p>
 * An AssociationView is associated with an association UML component.
 *
 * @author David Miserez
 * @version 1.0 - 25.07.2011
 */
public abstract class AssociationView extends RelationView {
  protected Association association;
  private ButtonGroup btnGrpNavigation;
  private JMenuItem navBidirectional, navFirstToSecond, navSecondToFirst;

  public AssociationView(GraphicView parent, EntityView source,
                         EntityView target, Association association, Point posSource,
                         Point posTarget, boolean checkRecursivity) {
    super(parent, source, target, association, posSource, posTarget,
          checkRecursivity);
    JMenu menuNavigation;
    TextBoxLabelTitle tb = new TextBoxLabelTitle(parent, association, this);

    this.association = association;
    tbRoles.add(tb);
    parent.addOthersComponents(tb);

    // Gestion du menu contextuel
    popupMenu.addSeparator();
    popupMenu.add(menuNavigation = new JMenu("Navigability"));
    btnGrpNavigation = new ButtonGroup();
    menuNavigation.add(navBidirectional = makeRadioButtonMenuItem("",
                                                                  NavigateDirection.BIDIRECTIONAL.toString(),
                                                                  btnGrpNavigation));
    menuNavigation.add(navFirstToSecond = makeRadioButtonMenuItem("",
                                                                  NavigateDirection.FIRST_TO_SECOND.toString(),
                                                                  btnGrpNavigation));
    menuNavigation.add(navSecondToFirst = makeRadioButtonMenuItem("",
                                                                  NavigateDirection.SECOND_TO_FIRST.toString(),
                                                                  btnGrpNavigation));

    setMenuItemText();
  }

  @Override
  public void actionPerformed(ActionEvent e) {

    if (e.getActionCommand().equals(NavigateDirection.BIDIRECTIONAL.toString()))
      association.setDirected(NavigateDirection.BIDIRECTIONAL);
    else if (e.getActionCommand().equals(
        NavigateDirection.FIRST_TO_SECOND.toString()))
      association.setDirected(NavigateDirection.FIRST_TO_SECOND);
    else if (e.getActionCommand().equals(
        NavigateDirection.SECOND_TO_FIRST.toString()))
      association.setDirected(NavigateDirection.SECOND_TO_FIRST);
    else
      super.actionPerformed(e);

    association.notifyObservers();
  }

  @Override
  public void maybeShowPopup(MouseEvent e, JPopupMenu popupMenu) {
    setMenuItemText();
    checkMenuItemSelected();
    super.maybeShowPopup(e, popupMenu);
  }

  private void setMenuItemText() {
    String sourceName = association.getSource().getName(), targetName = association
        .getTarget().getName();
    navBidirectional.setText("Bidirectional");
    navFirstToSecond.setText(String.format("%s -> %s", sourceName, targetName));
    navSecondToFirst.setText(String.format("%s -> %s", targetName, sourceName));
  }

  private void checkMenuItemSelected() {
    switch (association.getDirected()) {
      case FIRST_TO_SECOND:
        btnGrpNavigation.setSelected(navFirstToSecond.getModel(), true);
        break;
      case SECOND_TO_FIRST:
        btnGrpNavigation.setSelected(navSecondToFirst.getModel(), true);
        break;
      case BIDIRECTIONAL:
        btnGrpNavigation.setSelected(navBidirectional.getModel(), true);
        break;
      default:
        break;
    }
  }

  @Override
  public void update(Observable observable, Object o) {
    super.update(observable, o);
  }

  @Override
  public IDiagramComponent getAssociatedComponent() {
    return association;
  }

  @Override
  public void paintComponent(Graphics2D g2) {
    super.paintComponent(g2);
    paintNavigability(g2);
  }

  protected void paintNavigability(Graphics2D g2) {
    switch (association.getDirected()) {
      case FIRST_TO_SECOND:
        DependencyView.paintExtremity(g2, points.get(points.size() - 2)
                                                .getAnchor(), points.getLast().getAnchor());
        break;
      case SECOND_TO_FIRST:
        DependencyView.paintExtremity(g2, points.get(1).getAnchor(), points
            .getFirst().getAnchor());
        break;
      case BIDIRECTIONAL:
      default:
        break;
    }
  }

  @Override
  public void setSelected(boolean select) {
    if (isSelected() == select) return;

    super.setSelected(select);

    association.select();

    if (select)
      association.notifyObservers(UpdateMessage.SELECT);
    else
      association.notifyObservers(UpdateMessage.UNSELECT);
  }

}
