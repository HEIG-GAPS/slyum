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
    setLeafIcon(PersonalizedIcon.createSwingImageIcon("boxClose.png"));
    setClosedIcon(PersonalizedIcon.createSwingImageIcon("boxClose.png"));
    setOpenIcon(PersonalizedIcon.createSwingImageIcon("boxOpen.png"));
  }

  @Override
  public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded,
      boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel, expanded, leaf, row, hasFocus);

    if (row == 0) { // root
      setIcon(PersonalizedIcon.createSwingImageIcon("diagramIcon.png"));
    } else if (row == 1) {
      if (expanded)
        setIcon(PersonalizedIcon.createSwingImageIcon("boxOpenView.png"));
      else
        setIcon(PersonalizedIcon.createSwingImageIcon("boxCloseView.png"));
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
