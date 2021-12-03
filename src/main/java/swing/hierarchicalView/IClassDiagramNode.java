package swing.hierarchicalView;

import classDiagram.IDiagramComponent;

/**
 * This interface is implemented by node (in JTree) that are associated with a UML component.
 *
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public interface IClassDiagramNode {
  /**
   * Get the associated UML object with this node.
   *
   * @return the associated UML object with this node
   */
  public IDiagramComponent getAssociedComponent();

  public void remove();

}
