package swing.hierarchicalView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import swing.Slyum;
import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Dependency;
import swing.PanelClassDiagram;
import swing.hierarchicalView.HierarchicalView.STree;

/**
 * A JTree node associated with a dependency UML.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class NodeDepedency 
    extends AbstractNode
    implements IClassDiagramNode, ICustomizedIconNode, Observer {

  /**
   * Return the title that the node must show according to its dependency.
   * 
   * @param dependency
   *          the dependency to get the title
   * @return the title generated from dependency
   */
  public static String generateName(Dependency dependency) {
    return dependency.toString();
  }

  private final Dependency dependency;

  /**
   * Create a new node associated with a dependency.
   * 
   * @param dependency
   *          the dependency associated
   * @param treeModel
   *          the model of the JTree
   * @param tree
   *          the JTree
   */
  public NodeDepedency(
      Dependency dependency, DefaultTreeModel treeModel, STree tree) {
    
    super(generateName(dependency), treeModel, tree);

    if (treeModel == null)
      throw new IllegalArgumentException("dependency is null");

    if (tree == null) throw new IllegalArgumentException("tree is null");

    this.tree = tree;

    this.dependency = dependency;
    this.treeModel = treeModel;

    dependency.addObserver(this);
  }

  @Override
  public IDiagramComponent getAssociedComponent() {
    return dependency;
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "dependency.png");
  }

  @Override
  public void update(Observable observable, Object o) {
    if (o != null && o instanceof UpdateMessage) {
      final TreePath path = new TreePath(getPath());

      switch ((UpdateMessage) o) {
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
      setUserObject(generateName(dependency));
      treeModel.reload(this);
    }
  }

  @Override
  public void remove() {}

}
