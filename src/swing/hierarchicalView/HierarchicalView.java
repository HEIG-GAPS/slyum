package swing.hierarchicalView;

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
import graphic.GraphicView;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.datatransfer.Transferable;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import swing.MultiViewManager;
import swing.PanelClassDiagram;
import swing.slyumCustomizedComponents.SScrollPane;
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
    implements IComponentsObserver, TreeSelectionListener, Observer, 
               MouseListener, KeyListener {
  
  private final DefaultMutableTreeNode 
      viewsNode,
      entitiesNode, 
      associationsNode,
      inheritancesNode, 
      dependenciesNode;
  
  private final STree tree;
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
    setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0,
            Slyum.THEME_COLOR));
    
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

    viewsNode = new DefaultMutableTreeNode("Views");
    root.add(viewsNode);

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
        if (node instanceof IClassDiagramNode)
          ((IClassDiagramNode) node).remove();
        super.removeNodeFromParent(node);
      }
    };
    tree = new STree(treeModel);
    tree.setDragEnabled(true);
    tree.setTransferHandler(new TransferHandler(){

      @Override
      protected Transferable createTransferable(JComponent c) {
        Object o = tree.getSelectionPath().getLastPathComponent();
        if (o instanceof NodeEntity)
          return (Transferable)((NodeEntity)o).getAssociedComponent();
        return null;
      }

      @Override
      public int getSourceActions(JComponent c) {
        return COPY;
      }
    });
    tree.addTreeSelectionListener(this);
    tree.addMouseListener(this);
    tree.addKeyListener(this);
    
    tree.getSelectionModel().setSelectionMode(
            TreeSelectionModel.SINGLE_TREE_SELECTION);
    tree.setCellRenderer(new TreeRenderer());
    tree.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    JScrollPane scrollPane = new SScrollPane();
    scrollPane.setViewportView(tree);
    scrollPane.setBorder(null);
    add(scrollPane);
    classDiagram.addComponentsObserver(this);
    setMinimumSize(new Dimension(150, 200));
  }
  
  public void addView(GraphicView graphicView) {
    viewsNode.insert(
        new NodeView(graphicView, treeModel), 
        getLastIndex(viewsNode));
    treeModel.reload(viewsNode);
  }
  
  public void setSelectedView(GraphicView graphicView) {
    tree.setSelectionPath(
        new TreePath(searchNodeViewAssociedWith(graphicView).getPath()));
  }
  
  private int getLastIndex(DefaultMutableTreeNode node) {
    return node.getLeafCount() + (node.isLeaf() ? -1 : 0);
  }

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

  public void addAssociationClass(AssociationClass component) {
    addNode(new NodeSimpleEntity(component, treeModel, tree,
            PersonalizedIcon
                    .createImageIcon(Slyum.ICON_PATH + "classAssoc.png")),
            entitiesNode);
  }

  public void addBinary(Binary component) {
    addAssociation(component, "resources/icon/association.png");
  }

  public void addClassEntity(ClassEntity component) {
    addNode(new NodeSimpleEntity(component, treeModel, tree,
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "class.png")),
            entitiesNode);
  }

  public void addInterfaceEntity(InterfaceEntity component) {
    addNode(new NodeSimpleEntity(component, treeModel, tree, PersonalizedIcon
            .createImageIcon(Slyum.ICON_PATH + "interface.png")),
            entitiesNode);
  }

  public void addEnumEntity(EnumEntity component) {
    addNode(new NodeEnumEntity(component, treeModel, tree,
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "enum.png")),
            entitiesNode);
  }

  public void addComposition(Composition component) {
    addAssociation(component, "resources/icon/composition.png");
  }

  public void addDependency(Dependency component) {
    addNode(new NodeDepedency(component, treeModel, tree), dependenciesNode);
  }

  public void addInheritance(Inheritance component) {
    addNode(new NodeInheritance(component, treeModel, tree), inheritancesNode);
  }

  public void addInnerClass(InnerClass component) {
    addNode(new NodeInnerClass(component, treeModel, tree), inheritancesNode);

  }

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
    sortAlphabetically(parent, treeModel, tree);
  }
  
  public static void sortAlphabetically(
      DefaultMutableTreeNode parent, DefaultTreeModel treeModel, STree tree) {
    int count = parent.getChildCount();
    
    if (count < 2)
      return;
    
    // Sort childs.
    for (int i = 0; i < count; ++i) {
      DefaultMutableTreeNode child = (DefaultMutableTreeNode)parent.getChildAt(i);
      if (!child.isLeaf())
        sortAlphabetically(child, treeModel, tree);
    }
    
    quickSort(parent, 0, count-1);
    
    tree.stopFireEvent = true;
    treeModel.reload(parent);
    tree.stopFireEvent = false;
  }
  
  private static void quickSort(DefaultMutableTreeNode node, int low, int high) {
    int i = low, j = high;
    String pivot = ((DefaultMutableTreeNode)node.getChildAt(low + (high-low)/2))
                                                .getUserObject().toString();
    
    while (i <= j) {
      
      while (((DefaultMutableTreeNode)node.getChildAt(i)).getUserObject().toString().compareToIgnoreCase(pivot) < 0) ++i;
      while (((DefaultMutableTreeNode)node.getChildAt(j)).getUserObject().toString().compareToIgnoreCase(pivot) > 0) --j;
      
      // Exchange
      if (i <= j) {
        DefaultMutableTreeNode 
            nodeI = (DefaultMutableTreeNode)node.getChildAt(i), 
            nodeJ = (DefaultMutableTreeNode)node.getChildAt(j);
        
        node.insert(nodeI, j);
        node.insert(nodeJ, i);
        ++i;
        --j;
      }
    }
    
    // Recursivity
    if (low < j)
      quickSort(node, low, j);
    if (i < high)
      quickSort(node, i, high);
  }

  public void changeZOrder(Entity entity, int index) {
    
    /* Nothing to do since we sort the tree alphabetically.
    
    LinkedList<EntityView> evs = 
        MultiViewManager.getSelectedGraphicView().getSelectedEntities();

    final NodeEntity ne = (NodeEntity) searchAssociedNodeIn(entity,
            entitiesNode);

    entitiesNode.remove(ne);
    entitiesNode.insert(ne, entitiesNode.getChildCount() - index);
    treeModel.reload(entitiesNode);

    for (EntityView ev : evs)
      ev.setSelected(true);*/
  }

  public void removeComponent(IDiagramComponent component) {
    final IClassDiagramNode associedNode = searchAssociedNode(component);

    if (associedNode != null) {
      treeModel.removeNodeFromParent((DefaultMutableTreeNode) associedNode);
      component.deleteObserver((Observer) associedNode);
    }
  }
  
  public void removeView(GraphicView graphicView) {
    NodeView nodeView = searchNodeViewAssociedWith(graphicView);

    if (nodeView != null) {
      treeModel.removeNodeFromParent((DefaultMutableTreeNode) nodeView);
      graphicView.deleteObserver((Observer) nodeView);
    }
  }
  
  public void removeViews() {
    List<GraphicView> gvs = MultiViewManager.getAllGraphicViews();
    gvs.remove(MultiViewManager.getRootGraphicView());
    
    for (GraphicView gv : gvs)
      removeView(gv);
  }
  
  public NodeView searchNodeViewAssociedWith(GraphicView graphicView) {
    NodeView child;

    for (int i = 0; i < viewsNode.getChildCount(); i++) {
      child = (NodeView) viewsNode.getChildAt(i);

      if (child.getGraphicView() == graphicView)
        return child;
    }
    return null;
  }

  /**
   * Search in the entire structure of JTree the node associated with the given
   * UML object. Return null if no associated object are found.
   * 
   * @param o the object associated with a node
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
    IClassDiagramNode child;

    for (int i = 0; i < root.getChildCount(); i++) {
      child = (IClassDiagramNode) root.getChildAt(i);

      if (child.getAssociedComponent().equals(o)) 
        return child;

      if (!root.getChildAt(i).isLeaf())
        searchAssociedNodeIn(o, root.getChildAt(i));
    }

    return null;
  }

  @Override
  public void valueChanged(TreeSelectionEvent e) { 
    if (tree.stopFireEvent)
      return;
    
    final LinkedList<TreePath> paths = new LinkedList<>();
    final TreePath[] treePaths = e.getPaths();
    
    for (TreePath treePath : treePaths)
      if (!e.isAddedPath(treePath))
        paths.add(treePath);

    for (final TreePath treePath2 : treePaths)
      if (e.isAddedPath(treePath2)) 
        paths.add(treePath2);
    
    GraphicView selectedGraphicView = 
        MultiViewManager.getSelectedGraphicView();
    if (selectedGraphicView != null)
      selectedGraphicView.unselectAll();

    for (final TreePath treePath : paths) {
      final Object o = treePath.getLastPathComponent();

      if (!(o instanceof IClassDiagramNode)) // is an associed component node ?
        continue;

      final IDiagramComponent component = 
          ((IClassDiagramNode) o).getAssociedComponent();
      component.select();
      tree.scrollPathToVisible(treePath);

      if (e.isAddedPath(treePath))
        component.notifyObservers(UpdateMessage.SELECT);
      else
        component.notifyObservers(UpdateMessage.UNSELECT);
    }
  }
  
  public void setDiagramName(String name) {
    DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
    root.setUserObject(name);
    model.nodeChanged(root);
    
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
    if (o instanceof ClassDiagram) {
      String name = ((ClassDiagram)o).getName();
      setDiagramName(name);
      viewsNode.getFirstLeaf().setUserObject(
          name.isEmpty() ? GraphicView.ROOT_VIEW_DEFAULT_NAME : name);
    }
  }

  @Override
  public void notifyAggregationCreation(Aggregation component) {
    addAggregation(component);
  }

  @Override
  public void notifyAssociationClassCreation(AssociationClass component) {
    addAssociationClass(component);
  }

  @Override
  public void notifyBinaryCreation(Binary component) {
    addBinary(component);
  }

  @Override
  public void notifyClassEntityCreation(ClassEntity component) {
    addClassEntity(component);
  }

  @Override
  public void notifyCompositionCreation(Composition component) {
    addComposition(component);
  }

  @Override
  public void notifyDependencyCreation(Dependency component) {
    addDependency(component);
  }

  @Override
  public void notifyInheritanceCreation(Inheritance component) {
    addInheritance(component);
  }

  @Override
  public void notifyInnerClassCreation(InnerClass component) {
    addInnerClass(component);
  }

  @Override
  public void notifyInterfaceEntityCreation(InterfaceEntity component) {
    addInterfaceEntity(component);
  }

  @Override
  public void notifyEnumEntityCreation(EnumEntity component) {
    addEnumEntity(component);
  }

  @Override
  public void notifyMultiCreation(Multi component) {
    addMulti(component);
  }

  @Override
  public void notifyChangeZOrder(Entity entity, int index) {
    changeZOrder(entity, index);
  }

  @Override
  public void notifyRemoveComponent(IDiagramComponent component) {
    removeComponent(component);
  }

  @Override
  public void mouseClicked(MouseEvent e) {
   
  }
  
  public void maybeShowPopup(MouseEvent e, JPopupMenu popupMenu) {
        
    if (SwingUtilities.isRightMouseButton(e)) {
      popupMenu.show(e.getComponent(), (int) (e.getX()), (int) (e.getY()));
      TreePath path = tree.getPathForLocation(e.getX(), e.getY());
      if (path != null) 
        tree.setSelectionPath(path);
    }
  }

  @Override
  public void mousePressed(MouseEvent e) {
    int selRow = tree.getRowForLocation(e.getX(), e.getY());
    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
    if(selRow != -1) {
      Object lastComponent = selPath.getLastPathComponent();
      if (lastComponent instanceof NodeView) {
        NodeView nodeView = (NodeView)lastComponent;

        maybeShowPopup(e, nodeView.getPopupMenu());

        // Double click for open view
        if(e.getClickCount() == 2)
          MultiViewManager.openView(nodeView.getGraphicView());
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    
  }

  @Override
  public void mouseExited(MouseEvent e) {
    
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

  @Override
  public void keyPressed(KeyEvent e) {
    
  }

  @Override
  public void keyReleased(KeyEvent e) {
    TreePath selectionPath = tree.getSelectionPath();
    if (selectionPath != null) {
      Object selectedNode = selectionPath.getLastPathComponent();
      if (selectedNode != null && selectedNode instanceof NodeView) {
        NodeView selectedNodeView = (NodeView)selectedNode;
        if (e.getKeyCode() == KeyEvent.VK_DELETE &&
            selectedNodeView.getGraphicView() != MultiViewManager.getRootGraphicView())
          MultiViewManager.removeView(selectedNodeView.getGraphicView());
      }
    }
  }
  
  public static class STree extends JTree {
    private boolean stopFireEvent;

    public STree(TreeModel newModel) {
      super(newModel);
    }
    
    public void addSelectionPathNoFire(TreePath treePath) {
      stopFireEvent = true;
      addSelectionPath(treePath);
      stopFireEvent = false;
    }
    
    public void removeSelectionPathNoFire(TreePath treePath) {
      stopFireEvent = true;
      removeSelectionPath(treePath);
      stopFireEvent = false;
    }
  }
}
