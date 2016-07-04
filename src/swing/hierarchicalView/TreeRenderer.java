package swing.hierarchicalView;

import classDiagram.IDiagramComponent;
import graphic.GraphicComponent;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import swing.MultiViewManager;
import swing.Slyum;
import utility.PersonalizedIcon;

public class TreeRenderer extends DefaultTreeCellRenderer {
  public TreeRenderer() {
    setLeafIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
            + "boxClose.png"));
    setClosedIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
            + "boxClose.png"));
    setOpenIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
            + "boxOpen.png"));
  }

  @Override
  public Component getTreeCellRendererComponent(
      JTree tree, Object value, boolean sel, boolean expanded, 
      boolean leaf, int row, boolean hasFocus) {

    super.getTreeCellRendererComponent(
        tree, value, sel, expanded, leaf, row, hasFocus);

    if (row == 0) { // root
      setIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "diagramIcon.png"));
    } else if (row == 1) {
      if (expanded)
        setIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "boxOpenView.png"));
      else
        setIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "boxCloseView.png"));
    }
    else if (value instanceof ICustomizedIconNode) {
      setIcon(((ICustomizedIconNode) value).getCustomizedIcon());
    } 
    
    if (value instanceof IClassDiagramNode) {
      
      IDiagramComponent component = ((IClassDiagramNode)value).getAssociedComponent();
      
      if (value instanceof AbstractNode && // Only main component must be treated.
          component != null) {
        if (GraphicComponent.countGraphicComponentsAssociedWith(component) == 0)
          setForeground(Color.RED);
        else if (MultiViewManager.getSelectedGraphicView().searchAssociedComponent(component) == null)
          setForeground(Color.GRAY);
      }   
    }
    
    return this;
  }
}
