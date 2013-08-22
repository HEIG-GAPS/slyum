package swing.hierarchicalView;

import change.Change;
import classDiagram.ClassDiagram;
import classDiagram.IComponentsObserver;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.EnumEntity;
import classDiagram.components.InterfaceEntity;
import classDiagram.relationships.Aggregation;
import classDiagram.relationships.Association;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Composition;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Inheritance;
import classDiagram.relationships.InnerClass;
import classDiagram.relationships.Multi;
import graphic.entity.EntityView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import swing.PanelClassDiagram;
import swing.Slyum;
import utility.PersonalizedIcon;

/**
 * This class is a hierarchical view of the class diagram. It represents class
 * diagram like a tree with all component include in. JTree Swing component is
 * used to. It implements IComponentsObserver to see changes in class diagram.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class HierarchicalView 
    extends JPanel 
    implements IComponentsObserver, TreeSelectionListener, Observer {
  private final DefaultMutableTreeNode entitiesNode, associationsNode,
          inheritancesNode, dependenciesNode;
  private final JTree tree;
  private final DefaultTreeModel treeModel;
  private JTextField txtFieldClassDiagramName;  

  /**
   * Create a new hierarchical view of the specified class diagram. The new view
   * is empty, if class diagram had already components, you must add them
   * manually.
   * 
   * @param classDiagram
   *          the class diagram for constructing the hierarchical view.
   */
  public HierarchicalView(ClassDiagram classDiagram) {
    setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
    setBackground(null);
    setForeground(Color.GRAY);
    
    txtFieldClassDiagramName = new JTextField() {
      @Override
      protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        if(getText().isEmpty()){
          Graphics2D g2 = (Graphics2D)g.create();
          utility.Utility.setRenderQuality(g2);
          g2.setColor(Color.gray);
          g2.drawString("Enter the diagram's name", 11, 23);
          g2.dispose();
        }
      }
    };
    txtFieldClassDiagramName.addFocusListener(new FocusListener() {

      @Override
      public void focusGained(FocusEvent e) {
        repaint();
      }

      @Override
      public void focusLost(FocusEvent e) {
        repaint();
      }
    });
    txtFieldClassDiagramName.addKeyListener(new KeyAdapter() {
      
      @Override
      public void keyReleased(KeyEvent e) {
        PanelClassDiagram.setCurrentDiagramName(
            txtFieldClassDiagramName.getText());
      }
    });
    txtFieldClassDiagramName.setFont(Slyum.DEFAULT_FONT.deriveFont(15f));
    txtFieldClassDiagramName.setBorder(
        BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, Slyum.THEME_COLOR),
            BorderFactory.createEmptyBorder(5, 10, 5, 5)));
    txtFieldClassDiagramName.setMaximumSize(
        new Dimension(Short.MAX_VALUE, 500));
    txtFieldClassDiagramName.setVisible(!Slyum.isViewTitleOnExport());
    add(txtFieldClassDiagramName);

    final DefaultMutableTreeNode root = new DefaultMutableTreeNode(
            classDiagram.getName());

    entitiesNode = new DefaultMutableTreeNode("Entities");
    root.add(entitiesNode);

    associationsNode = new DefaultMutableTreeNode("Relations");
    root.add(associationsNode);

    inheritancesNode = new DefaultMutableTreeNode("Inheritances");
    root.add(inheritancesNode);

    dependenciesNode = new DefaultMutableTreeNode("Dependencies");
    root.add(dependenciesNode);

    treeModel = new DefaultTreeModel(root) {
      @Override
      public void removeNodeFromParent(MutableTreeNode node) {
        ((IClassDiagramNode) node).remove();
        super.removeNodeFromParent(node);
      }
    };
    tree = new JTree(treeModel);
    tree.addTreeSelectionListener(this);
    tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    tree.setCellRenderer(new TreeRenderer());
    tree.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JScrollPane scrollPane = new JScrollPane();
    scrollPane.setViewportView(tree);
    scrollPane.setBorder(null);
    add(scrollPane);
    classDiagram.addComponentsObserver(this);
    setMinimumSize(new Dimension(150, 200));
  }

  @Override
  public void addAggregation(Aggregation component) {
    addAssociation(component, "resources/icon/aggregation.png");
  }

  /**
   * Add the specified association in associations node.
   * 
   * @param component
   *          the new association
   * @param imgPath
   *          the icon representing the association in JTree
   */
  public void addAssociation(Association component, String imgPath) {
    addNode(new NodeAssociation(component, treeModel,
            PersonalizedIcon.createImageIcon(imgPath), tree), associationsNode);
  }

  @Override
  public void addAssociationClass(AssociationClass component) {
    addNode(new NodeSimpleEntity(component, treeModel, tree,
            PersonalizedIcon
                    .createImageIcon(Slyum.ICON_PATH + "classAssoc.png")),
            entitiesNode);
  }

  @Override
  public void addBinary(Binary component) {
    addAssociation(component, "resources/icon/association.png");
  }

  @Override
  public void addClassEntity(ClassEntity component) {
    addNode(new NodeSimpleEntity(component, treeModel, tree,
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "class.png")),
            entitiesNode);
  }

  @Override
  public void addInterfaceEntity(InterfaceEntity component) {
    addNode(new NodeSimpleEntity(component, treeModel, tree, PersonalizedIcon
            .createImageIcon(Slyum.ICON_PATH + "interface.png")),
            entitiesNode);
  }

  @Override
  public void addEnumEntity(EnumEntity component) {
    addNode(new NodeEnumEntity(component, treeModel, tree,
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "enum.png")),
            entitiesNode);
  }

  @Override
  public void addComposition(Composition component) {
    addAssociation(component, "resources/icon/composition.png");
  }

  @Override
  public void addDependency(Dependency component) {
    addNode(new NodeDepedency(component, treeModel, tree), dependenciesNode);
  }

  @Override
  public void addInheritance(Inheritance component) {
    addNode(new NodeInheritance(component, treeModel, tree), inheritancesNode);
  }

  @Override
  public void addInnerClass(InnerClass component) {
    addNode(new NodeInnerClass(component, treeModel, tree), inheritancesNode);

  }

  @Override
  public void addMulti(Multi component) {
    addAssociation(component, "resources/icon/multi.png");
  }

  /**
   * Add a new node in the specified parent node.
   * 
   * @param leaf
   *          the new node to add
   * @param parent
   *          the parent of the new node
   */
  public void addNode(DefaultMutableTreeNode leaf, DefaultMutableTreeNode parent) {
    parent.insert(leaf, 0);
    treeModel.reload(parent);
  }

  @Override
  public void changeZOrder(Entity entity, int index) {
    LinkedList<EntityView> evs = PanelClassDiagram.getInstance()
            .getCurrentGraphicView().getSelectedEntities();

    final NodeEntity ne = (NodeEntity) searchAssociedNodeIn(entity,
            entitiesNode);

    entitiesNode.remove(ne);

    entitiesNode.insert(ne, entitiesNode.getChildCount() - index);

    treeModel.reload(entitiesNode);

    for (EntityView ev : evs)

      ev.setSelected(true);
  }

  @Override
  public void removeComponent(IDiagramComponent component) {
    final IClassDiagramNode associedNode = searchAssociedNode(component);

    if (associedNode != null) {
      treeModel.removeNodeFromParent((DefaultMutableTreeNode) associedNode);
      component.deleteObserver((Observer) associedNode);
    }
  }

  /**
   * Search in the entire structure of JTree the node associated with the given
   * UML object. Return null if no associated object are found.
   * 
   * @param o
   *          the object associated with a node
   * @return the node associated with the object; or null if no node are found
   */
  public IClassDiagramNode searchAssociedNode(Object o) {
    IClassDiagramNode result = searchAssociedNodeIn(o, entitiesNode);

    if (result == null) result = searchAssociedNodeIn(o, associationsNode);

    if (result == null) result = searchAssociedNodeIn(o, inheritancesNode);

    if (result == null) result = searchAssociedNodeIn(o, dependenciesNode);

    return result;
  }

  /**
   * Return the node associated with the given UML object. Return null if no
   * associated object are found.
   * 
   * @param o
   *          the object associated with a node
   * @param root
   *          the root node for the JTree
   * @return the node associated with the object; or null if no node are found
   */
  public static IClassDiagramNode searchAssociedNodeIn(Object o, TreeNode root) {
    IClassDiagramNode child = null;

    for (int i = 0; i < root.getChildCount(); i++) {
      child = (IClassDiagramNode) root.getChildAt(i);

      if (child.getAssociedComponent().equals(o)) return child;

      if (!root.getChildAt(i).isLeaf())
        searchAssociedNodeIn(o, root.getChildAt(i));
    }

    return null;
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) {
    final LinkedList<TreePath> paths = new LinkedList<>();
    final TreePath[] treePaths = e.getPaths();

    // sort unselect first, select next
    for (int i = 0; i < treePaths.length; i++)
      if (!e.isAddedPath(treePaths[i])) paths.add(treePaths[i]);

    for (final TreePath treePath2 : treePaths)
      if (e.isAddedPath(treePath2)) paths.add(treePath2);

    for (final TreePath treePath : paths) {
      final Object o = treePath.getLastPathComponent();

      if (!(o instanceof IClassDiagramNode)) // is an associed component
        // node ?
        continue;

      final IDiagramComponent component = ((IClassDiagramNode) o)
              .getAssociedComponent();
      component.select();
      tree.scrollPathToVisible(treePath);

      if (e.isAddedPath(treePath))
        component.notifyObservers(UpdateMessage.SELECT);

      else
        component.notifyObservers(UpdateMessage.UNSELECT);
    }
  }
  
  public void setDiagramName(String name) {
    if (txtFieldClassDiagramName.getText().equals(name))
      return;
    txtFieldClassDiagramName.setText(name);
  }
  
  public void setVisibleClassDiagramName(boolean visible) {
    txtFieldClassDiagramName.setVisible(visible);
    doLayout();
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof ClassDiagram)
      setDiagramName(((ClassDiagram)o).getName());
  }
}
