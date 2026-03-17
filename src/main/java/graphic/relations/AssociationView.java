package graphic.relations;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Association;
import classDiagram.relationships.Association.NavigateDirection;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.textbox.TextBoxLabelTitle;

import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import java.awt.*;
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
  private ToggleGroup btnGrpNavigation;
  private RadioMenuItem navBidirectional, navFirstToSecond, navSecondToFirst;

  public AssociationView(GraphicView parent, EntityView source,
                         EntityView target, Association association, Point posSource,
                         Point posTarget, boolean checkRecursivity) {
    super(parent, source, target, association, posSource, posTarget,
          checkRecursivity);
    Menu menuNavigation;
    TextBoxLabelTitle tb = new TextBoxLabelTitle(parent, association, this);

    this.association = association;
    tbRoles.add(tb);
    parent.addOthersComponents(tb);

    // Gestion du menu contextuel
    popupMenu.addSeparator();
    menuNavigation = new Menu("Navigability");
    popupMenu.getItems().add(menuNavigation);
    btnGrpNavigation = new ToggleGroup();
    navBidirectional = makeRadioButtonMenuItem("", NavigateDirection.BIDIRECTIONAL.toString(), btnGrpNavigation);
    navFirstToSecond = makeRadioButtonMenuItem("", NavigateDirection.FIRST_TO_SECOND.toString(), btnGrpNavigation);
    navSecondToFirst = makeRadioButtonMenuItem("", NavigateDirection.SECOND_TO_FIRST.toString(), btnGrpNavigation);
    menuNavigation.getItems().add(navBidirectional);
    menuNavigation.getItems().add(navFirstToSecond);
    menuNavigation.getItems().add(navSecondToFirst);

    setMenuItemText();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    String cmd = (e.getSource() instanceof MenuItem) ? ((MenuItem)e.getSource()).getId() : "";
    if (cmd.equals(NavigateDirection.BIDIRECTIONAL.toString()))
      association.setDirected(NavigateDirection.BIDIRECTIONAL);
    else if (cmd.equals(NavigateDirection.FIRST_TO_SECOND.toString()))
      association.setDirected(NavigateDirection.FIRST_TO_SECOND);
    else if (cmd.equals(NavigateDirection.SECOND_TO_FIRST.toString()))
      association.setDirected(NavigateDirection.SECOND_TO_FIRST);
    else
      super.actionPerformed(e);

    association.notifyObservers();
  }

  @Override
  public void maybeShowPopup(javafx.scene.input.MouseEvent e, javafx.scene.control.ContextMenu popupMenu) {
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
        navFirstToSecond.setSelected(true);
        break;
      case SECOND_TO_FIRST:
        navSecondToFirst.setSelected(true);
        break;
      case BIDIRECTIONAL:
        navBidirectional.setSelected(true);
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
  public void paintComponent(GraphicsContext gc) {
    super.paintComponent(gc);
    paintNavigability(gc);
  }

  protected void paintNavigability(GraphicsContext gc) {
    switch (association.getDirected()) {
      case FIRST_TO_SECOND:
        DependencyView.paintExtremity(gc, points.get(points.size() - 2)
                                                .getAnchor(), points.getLast().getAnchor());
        break;
      case SECOND_TO_FIRST:
        DependencyView.paintExtremity(gc, points.get(1).getAnchor(), points
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
