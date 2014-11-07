package swing.hierarchicalView;

import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import swing.Slyum;
import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Inheritance;
import swing.PanelClassDiagram;
import swing.hierarchicalView.HierarchicalView.STree;

/**
 * A JTree node associated with an inheritance.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class NodeInheritance 
    extends AbstractNode 
    implements ICustomizedIconNode, Observer, IClassDiagramNode

{
  /**
	 * 
	 */
  private static final long serialVersionUID = -6765906183481076172L;

  /**
   * Return the title that the node must show according to its inheritance.
   * 
   * @param inheritance
   *          the inheritance to get the title
   * @return the title generated from dependency
   */
  public static String generateName(Inheritance inheritance) {
    return inheritance.toString();
  }

  private final Inheritance inheritance;

  /**
   * Create a new node associated with an inheritance.
   * 
   * @param inheritance
   *          the inheritance associated
   * @param treeModel
   *          the model of the JTree
   * @param tree
   *          the JTree
   */
  public NodeInheritance(
      Inheritance inheritance, DefaultTreeModel treeModel, STree tree) {
    
    super(generateName(inheritance), treeModel, tree);

    if (treeModel == null)
      throw new IllegalArgumentException("treeModel is null");

    if (tree == null) throw new IllegalArgumentException("tree is null");

    this.inheritance = inheritance;
    inheritance.addObserver(this);
  }

  @Override
  public IDiagramComponent getAssociedComponent() {
    return inheritance;
  }

  @Override
  public ImageIcon getCustomizedIcon() {
    return PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "generalize.png");
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
      setUserObject(generateName(inheritance));
      treeModel.reload(this);
    }
  }

  @Override
  public void remove() {}
}
