package swing.hierarchicalView;

import classDiagram.IDiagramComponent;
import graphic.GraphicComponent;
import swing.MultiViewManager;
import swing.Slyum;
import utility.PersonalizedIcon;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

public class TreeRenderer extends DefaultTreeCellRenderer {
  public TreeRenderer() {
    setLeafIcon(PersonalizedIcon.createImageIcon("boxClose.png"));
    setClosedIcon(PersonalizedIcon.createImageIcon("boxClose.png"));
    setOpenIcon(PersonalizedIcon.createImageIcon("boxOpen.png"));
  }

  @Override
  public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel, expanded, leaf, row, hasFocus);

    if (row == 0) { // root
      setIcon(PersonalizedIcon.createImageIcon("diagramIcon.png"));
    } else if (row == 1) {
      if (expanded)
        setIcon(PersonalizedIcon.createImageIcon("boxOpenView.png"));
      else
        setIcon(PersonalizedIcon.createImageIcon("boxCloseView.png"));
    } else if (value instanceof ICustomizedIconNode) {
      setIcon(((ICustomizedIconNode) value).getCustomizedIcon());
    }

    if (value instanceof IClassDiagramNode) {

      IDiagramComponent component = ((IClassDiagramNode) value).getAssociedComponent();

      if (value instanceof AbstractNode && // Only main component must be treated.
          component != null) {
        if (GraphicComponent.countGraphicComponentsAssociedWith(component) == 0)
          setForeground(Color.RED);
        else if (MultiViewManager.getSelectedGraphicView() != null &&
                 MultiViewManager.getSelectedGraphicView().searchAssociedComponent(component) == null)
          setForeground(Color.GRAY);
      }
    }

    return this;
  }

}
