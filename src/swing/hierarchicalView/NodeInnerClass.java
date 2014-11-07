package swing.hierarchicalView;

import classDiagram.IDiagramComponent;
import classDiagram.relationships.Inheritance;
import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;

import swing.Slyum;
import utility.PersonalizedIcon;
import classDiagram.relationships.InnerClass;
import java.util.Observable;
import java.util.Observer;
import javax.swing.tree.TreePath;
import swing.PanelClassDiagram;
import swing.hierarchicalView.HierarchicalView.STree;

public class NodeInnerClass
    extends AbstractNode 
    implements ICustomizedIconNode, Observer, IClassDiagramNode {

  private static final long serialVersionUID = -4534430187776530177L;
  public static String generateName(InnerClass innerClass) {
    return innerClass.toString();
  }

  private InnerClass innerClass;
  
  public NodeInnerClass(InnerClass innerClass, DefaultTreeModel treeModel,
          STree tree) {
    super(innerClass, treeModel, tree);
    
    if (treeModel == null)
      throw new IllegalArgumentException("treeModel is null");

    if (tree == null) throw new IllegalArgumentException("tree is null");

    innerClass.addObserver(this);
    this.innerClass = innerClass;
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "innerClass.png");
  }

  @Override
  public void update(Observable observable, Object o) {
    
    if (o != null && o instanceof IDiagramComponent.UpdateMessage) {
      final TreePath path = new TreePath(getPath());

      switch ((IDiagramComponent.UpdateMessage) o) {
        case SELECT:
          if (!PanelClassDiagram.getInstance().isDisabledUpdate())
            tree.addSelectionPathNoFire(path);
          break;
        case UNSELECT:
          tree.removeSelectionPathNoFire(path);
          break;
        default:
          break;
      }
    } else {
      setUserObject(generateName(innerClass));
      treeModel.reload(this);
    }
  }

  @Override
  public IDiagramComponent getAssociedComponent() {
    return innerClass;
  }

  @Override
  public void remove() { }

}
