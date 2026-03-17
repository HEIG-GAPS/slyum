package graphic.entity;

import classDiagram.ClassDiagram.ViewEntity;
import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Attribute;
import classDiagram.components.ConstructorMethod;
import classDiagram.components.Method;
import classDiagram.components.Method.ParametersViewStyle;
import classDiagram.components.PrimitiveType;
import classDiagram.components.SimpleEntity;
import classDiagram.components.Visibility;
import graphic.GraphicView;
import graphic.textbox.TextBox;
import graphic.textbox.TextBoxAttribute;
import graphic.textbox.TextBoxMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.MultiViewManager;
import swing.PanelClassDiagram;
import swing.Slyum;
import utility.PersonalizedIcon;

import javafx.event.ActionEvent;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public abstract class SimpleEntityView extends EntityView {

  public static List<SimpleEntityView> getAll() {
    LinkedList<SimpleEntityView> simples = new LinkedList<>();

    for (GraphicView gv : MultiViewManager.getAllGraphicViews())
      for (EntityView view : gv.getEntitiesView())
        if (view instanceof SimpleEntityView)
          simples.add((SimpleEntityView) view);
    return simples;
  }

  public static List<SimpleEntityView> getSelectedSimpleEntityView(GraphicView parent) {
    List<SimpleEntityView> simples = new LinkedList<>();
    for (EntityView view : parent.getSelectedEntities())
      if (view instanceof SimpleEntityView)
        simples.add((SimpleEntityView) view);
    return simples;
  }

  // Attributs et méthodes
  protected LinkedList<TextBoxAttribute> attributesView = new LinkedList<>();
  protected boolean displayMethods = true;

  protected LinkedList<TextBoxMethod> methodsView = new LinkedList<>();

  private boolean displayAttributes = true;
  // Style de vue
  private boolean displayDefault = true;

  private ToggleGroup groupView, groupViewMethods;
  private MenuItem menuItemAbstract;
  private RadioMenuItem menuItemMethodsAll;
  private RadioMenuItem menuItemMethodsDefault;
  private RadioMenuItem menuItemMethodsName;
  private RadioMenuItem menuItemMethodsNothing;
  private RadioMenuItem menuItemMethodsType;
  private MenuItem menuItemStatic;
  private RadioMenuItem menuItemViewAll;
  private RadioMenuItem menuItemViewAttributes;
  private RadioMenuItem menuItemViewDefault;
  private RadioMenuItem menuItemViewMethods;
  private RadioMenuItem menuItemViewNothing;

  public SimpleEntityView(GraphicView parent, SimpleEntity component) {
    super(parent, component);
    initViewType();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);

    String cmd = (e.getSource() instanceof MenuItem) ? ((MenuItem)e.getSource()).getId() : "";
    if ("AddMethod".equals(cmd)) {
      addMethod();
    } else if ("AddAttribute".equals(cmd)) {
      addAttribute();
    } else if ("ViewDefault".equals(cmd)) {
      parent.setDefaultForSelectedEntities(true);
    } else if ("ViewAttribute".equals(cmd)) {
      parent.showAttributsForSelectedEntity(true);
      parent.showMethodsForSelectedEntity(false);
    } else if ("ViewMethods".equals(cmd)) {
      parent.showAttributsForSelectedEntity(false);
      parent.showMethodsForSelectedEntity(true);
    } else if ("ViewAll".equals(cmd)) {
      parent.showAttributsForSelectedEntity(true);
      parent.showMethodsForSelectedEntity(true);
    } else if ("ViewNothing".equals(cmd)) {
      parent.showAttributsForSelectedEntity(false);
      parent.showMethodsForSelectedEntity(false);
    } else if ("ViewMethodsDefault".equals(cmd))
      methodViewChangeClicked(ParametersViewStyle.DEFAULT);
    else if ("ViewTypeAndName".equals(cmd))
      methodViewChangeClicked(ParametersViewStyle.TYPE_AND_NAME);
    else if ("ViewType".equals(cmd))
      methodViewChangeClicked(ParametersViewStyle.TYPE);
    else if ("ViewName".equals(cmd))
      methodViewChangeClicked(ParametersViewStyle.NAME);
    else if ("ViewMethodNothing".equals(cmd))
      methodViewChangeClicked(ParametersViewStyle.NOTHING);
    else if ("Abstract".equals(cmd)) {
      IDiagramComponent component;
      if (pressedTextBox == null) {
        component = getAssociatedComponent();
        ((SimpleEntity) component).setAbstract(!((SimpleEntity) component)
            .isAbstract());
      } else {
        component = pressedTextBox.getAssociatedComponent();
        ((Method) component).setAbstract(!((Method) component).isAbstract());
      }
      component.notifyObservers();
    } else if ("Static".equals(cmd)) {
      IDiagramComponent component = pressedTextBox.getAssociatedComponent();
      if (component instanceof Attribute)
        ((Attribute) component).setStatic(!((Attribute) component).isStatic());
      else
        ((Method) component).setStatic(!((Method) component).isStatic());
      component.notifyObservers();
    } else if (Slyum.ACTION_TEXTBOX_UP.equals(cmd)
               || Slyum.ACTION_TEXTBOX_DOWN.equals(cmd)) {
      int offset = 1;
      if (Slyum.ACTION_TEXTBOX_UP.equals(cmd)) offset = -1;
      if (pressedTextBox.getClass() == TextBoxAttribute.class) {
        final Attribute attribute = (Attribute) ((TextBoxAttribute) pressedTextBox)
            .getAssociatedComponent();
        ((SimpleEntity) component).moveAttributePosition(attribute, offset);
      } else if (pressedTextBox.getClass() == TextBoxMethod.class) {
        final Method method = (Method) ((TextBoxMethod) pressedTextBox)
            .getAssociatedComponent();
        ((SimpleEntity) component).moveMethodPosition(method, offset);
      } else if (Slyum.ACTION_DUPLICATE.equals(cmd)) {
        if (pressedTextBox != null) {
          IDiagramComponent component = pressedTextBox.getAssociatedComponent();
          SimpleEntity entity = (SimpleEntity) getAssociatedComponent();
          if (component instanceof Attribute) {
            Attribute attribute = new Attribute((Attribute) component);
            LinkedList<Attribute> attributes = entity.getAttributes();
            entity.addAttribute(attribute);
            entity.notifyObservers(UpdateMessage.ADD_ATTRIBUTE_NO_EDIT);
            entity.moveAttributePosition(attribute,
                                         attributes.indexOf(component) - attributes.size() + 1);
            entity.notifyObservers();
          } else {
            Method method = new Method((Method) component);
            LinkedList<Method> methods = entity.getMethods();
            entity.addMethod(method);
            entity.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);
            entity.moveMethodPosition(method, methods.indexOf(component)
                                              - methods.size() + 1);
            entity.notifyObservers();
          }
        }
      }
      component.notifyObservers();
    }
  }

  /**
   * Create a new attribute with default type and name.
   */
  public void addAttribute() {
    final Attribute attribute = new Attribute("attribute",
                                              PrimitiveType.VOID_TYPE);
    prepareNewAttribute(attribute);

    ((SimpleEntity) component).addAttribute(attribute);
    component.notifyObservers(UpdateMessage.ADD_ATTRIBUTE);
  }

  /**
   * Create a new attribute view with the given attribute. If editing is a true, the new attribute view will be in
   * editing mode while it created.
   *
   * @param attribute the attribute UML
   * @param editing true if creating a new attribute view in editing mode; false otherwise
   */
  public void addAttribute(Attribute attribute, boolean editing) {
    final TextBoxAttribute newTextBox = new TextBoxAttribute(parent, attribute);
    attributesView.add(newTextBox);

    updateHeight();

    if (editing) newTextBox.editing();
  }

  /**
   * Create a new method with default type and name, without parameter.
   */
  public void addMethod() {
    final Method method = new Method("method", PrimitiveType.VOID_TYPE,
                                     Visibility.PUBLIC, ((SimpleEntity) component));
    prepareNewMethod(method);

    if (((SimpleEntity) component).addMethod(method))
      component.notifyObservers(UpdateMessage.ADD_METHOD);
  }

  /**
   * Create a new method view with the given method. If editing is a true, the new method view will be in editing mode
   * while it created.
   *
   * @param method the method UML
   * @param editing true if creating a new method view in editing mode; false otherwise
   */
  public void addMethod(Method method, boolean editing) {
    TextBoxMethod newTextBox = new TextBoxMethod(parent, method);

    // Add the new TextBox at the same position than the model's Method.
    methodsView.add(((SimpleEntity) component).getMethods().indexOf(method),
                    newTextBox);
    updateHeight();
    if (editing) newTextBox.editing();
  }

  @Override
  public SimpleEntityView clone() throws CloneNotSupportedException {

    SimpleEntityView view = (SimpleEntityView) super.clone();
    view.displayDefault = displayDefault;
    view.displayAttributes = displayAttributes;
    view.displayMethods = displayMethods;
    return view;
  }

  @Override
  public int computeHeight(int classNameHeight, int stereotypeHeight, int elementsHeight) {
    int height = super.computeHeight(classNameHeight, stereotypeHeight,
                                     elementsHeight);

    if (displayMethods) height += 10 + elementsHeight * methodsView.size();
    if (displayAttributes)
      height += 10 + elementsHeight * attributesView.size();
    return height;
  }

  @Override
  public List<TextBox> getAllTextBox() {
    List<TextBox> tb = super.getAllTextBox();
    tb.addAll(methodsView);
    tb.addAll(attributesView);
    return tb;
  }

  @Override
  public SimpleEntity getComponent() {
    return (SimpleEntity) super.getComponent();
  }

  /**
   * Set the display state for attributes.
   *
   * @param display the new display state for attributes.
   */
  public void setDisplayAttributes(boolean display) {
    displayAttributes = display;
    displayDefault = false;
    updateHeight();
  }

  public void setDisplayDefault(boolean display) {
    displayDefault = display;
    initViewType();
  }

  /**
   * Set the display state for methods.
   *
   * @param display the new display state for methods.
   */
  public void setDisplayMethods(boolean display) {
    displayMethods = display;
    displayDefault = false;
    updateHeight();
  }

  @Override
  public void setPictureMode(boolean enable) {
    super.setPictureMode(enable);
    for (TextBox t : methodsView)
      t.setPictureMode(enable);
    for (TextBox t : attributesView)
      t.setPictureMode(enable);
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element entityView = super.getXmlElement(doc);
    entityView.setAttribute("displayDefault", String.valueOf(displayDefault));
    entityView.setAttribute("displayAttributes",
                            String.valueOf(displayAttributes));
    entityView.setAttribute("displayMethods", String.valueOf(displayMethods));
    return entityView;
  }

  public final void initViewType() {
    if (displayDefault) {
      ViewEntity view =
          PanelClassDiagram.getInstance().getClassDiagram().getDefaultViewEntities();
      switch (view) {
        case ALL:
          displayAttributes = true;
          displayMethods = true;
          break;
        case NOTHING:
          displayAttributes = false;
          displayMethods = false;
          break;
        case ONLY_ATTRIBUTES:
          displayAttributes = true;
          displayMethods = false;
          break;
        case ONLY_METHODS:
          displayAttributes = false;
          displayMethods = true;
          break;
        default:
          displayAttributes = true;
          displayMethods = true;
          break;
      }
      updateHeight();
    }
  }

  /**
   * Return if attributes are displayed or not.
   *
   * @return true if attributes are displayed; false otherwise
   */
  public boolean isAttributeDisplayed() {
    return displayAttributes;
  }

  /**
   * Return if methods are displayed or not.
   *
   * @return true if methods are displayed; false otherwise
   */
  public boolean isMethodsDisplayed() {
    return displayMethods;
  }

  @Override
  public void maybeShowPopup(MouseEvent e, ContextMenu popupMenu) {
    if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
      updateMenuItemView();
      updateMenuItemMethodsView();

      menuItemAbstract.setDisable(true);

      // If context menu is requested on a TextBox, customize popup menu.
      if (pressedTextBox != null) {
        Boolean isConstructor = pressedTextBox.getAssociatedComponent().getClass()
                                              .equals(ConstructorMethod.class);
        menuItemStatic.setDisable(isConstructor);

        menuItemMoveUp.setDisable(attributesView.indexOf(pressedTextBox) == 0
                                  && methodsView.indexOf(pressedTextBox) == 0);
        menuItemMoveDown.setDisable(!(attributesView.size() == 0 || attributesView
                                                           .indexOf(pressedTextBox) != attributesView.size() - 1)
                        && (methodsView.size() == 0 || methodsView
                                                           .indexOf(pressedTextBox) != methodsView.size() - 1));
        if (pressedTextBox instanceof TextBoxMethod)
          menuItemAbstract.setDisable(isConstructor);

      } else {
        menuItemMoveUp.setDisable(!false);
        menuItemMoveDown.setDisable(true);
        menuItemStatic.setDisable(true);
        menuItemAbstract.setDisable(false);
      }
    }
    super.maybeShowPopup(e, popupMenu);
  }

  /**
   * Change the display style of parameters for all methods.
   *
   * @param newStyle the new display style
   */
  public void methodViewChange(ParametersViewStyle newStyle) {
    for (TextBoxMethod tbm : methodsView)
      ((Method) tbm.getAssociatedComponent()).setParametersViewStyle(newStyle);
  }

  /**
   * Remove the attribute associated with TextBoxAttribute from model (UML).
   *
   * @param tbAttribute the attribute to remove.
   *
   * @return true if the attribute has been removed; false otherwise
   */
  public boolean removeAttribute(TextBoxAttribute tbAttribute) {
    if (((SimpleEntity) component).removeAttribute((Attribute) tbAttribute
        .getAssociatedComponent())) {
      component.notifyObservers();
      updateHeight();
      return true;
    }

    return false;
  }

  /**
   * Remove the method associated with TextBoxMethod from model (UML)
   *
   * @param tbMethod the method to remove.
   *
   * @return true if component has been removed; false otherwise.
   */
  public boolean removeMethod(TextBoxMethod tbMethod) {
    if (((SimpleEntity) component).removeMethod((Method) tbMethod
        .getAssociatedComponent())) {
      component.notifyObservers();

      updateHeight();

      return true;
    }

    return false;
  }

  @Override
  public boolean removeTextBox(TextBox tb) {
    if (tb instanceof TextBoxAttribute)
      return removeAttribute((TextBoxAttribute) tb);
    else if (tb instanceof TextBoxMethod)
      return removeMethod((TextBoxMethod) tb);
    return false;
  }

  @Override
  public void update(Observable observable, Object object) {
    boolean enable = false;
    if (object != null && object.getClass() == UpdateMessage.class)
      switch ((UpdateMessage) object) {
        case ADD_ATTRIBUTE:
          enable = true;
        case ADD_ATTRIBUTE_NO_EDIT:
          addAttribute(((SimpleEntity) component).getLastAddedAttribute(),
                       enable);
          break;
        case ADD_METHOD:
          enable = true;
        case ADD_METHOD_NO_EDIT:
          addMethod(((SimpleEntity) component).getLastAddedMethod(), enable);
          break;
        default:
          super.update(observable, object);
          break;
      }
    else
      regenerateEntity();
  }

  @Override
  protected void initializeMenuItemsAddElements(ContextMenu popupmenu) {
    popupMenu.getItems().add(makeMenuItem("Add attribute", "AddAttribute", "attribute"));
    popupMenu.getItems().add(makeMenuItem("Add method", "AddMethod", "method"));
    popupMenu.getItems().add(new SeparatorMenuItem());
  }

  @Override
  protected void initializeMenuItemsPropertiesElements(ContextMenu popupMenu) {
    menuItemAbstract = makeMenuItem("Abstract", "Abstract", "abstract");
    popupMenu.getItems().add(menuItemAbstract);
    menuItemStatic = makeMenuItem("Static", "Static", "static");
    popupMenu.getItems().add(menuItemStatic);
    popupMenu.getItems().add(new SeparatorMenuItem());
  }

  @Override
  protected void initializeMenuViews(ContextMenu popupMenu) {
    Menu subMenu;
    subMenu = new Menu("View");
    groupView = new ToggleGroup();

    // Item Default
    menuItemViewDefault = makeRadioButtonMenuItem("Default", "ViewDefault",
                                                  groupView);
    menuItemViewDefault.setSelected(true);
    subMenu.getItems().add(menuItemViewDefault);

    // Item All
    menuItemViewAll = makeRadioButtonMenuItem("All", "ViewAll", groupView);
    subMenu.getItems().add(1, menuItemViewAll);

    // Item Only attributes
    menuItemViewAttributes = makeRadioButtonMenuItem("Only attributes", "ViewAttribute", groupView);
    subMenu.getItems().add(2, menuItemViewAttributes);

    // Item Only methods
    menuItemViewMethods = makeRadioButtonMenuItem("Only Methods", "ViewMethods", groupView);
    subMenu.getItems().add(3, menuItemViewMethods);

    // Item Nothing
    menuItemViewNothing = makeRadioButtonMenuItem("Nothing", "ViewNothing", groupView);
    subMenu.getItems().add(menuItemViewNothing);

    popupMenu.getItems().add(subMenu);

    // Menu VIEW METHODS
    subMenu = new Menu("Methods View");
    groupViewMethods = new ToggleGroup();

    menuItemMethodsDefault = makeRadioButtonMenuItem("Default",
                                                     "ViewMethodsDefault", groupViewMethods);
    menuItemMethodsDefault.setSelected(true);
    subMenu.getItems().add(menuItemMethodsDefault);

    menuItemMethodsAll = makeRadioButtonMenuItem("Type and Name", "ViewTypeAndName", groupViewMethods);
    subMenu.getItems().add(1, menuItemMethodsAll);

    menuItemMethodsType = makeRadioButtonMenuItem("Type", "ViewType", groupViewMethods);
    subMenu.getItems().add(2, menuItemMethodsType);

    menuItemMethodsName = makeRadioButtonMenuItem("Name", "ViewName", groupViewMethods);
    subMenu.getItems().add(3, menuItemMethodsName);

    menuItemMethodsNothing = makeRadioButtonMenuItem("Nothing", "ViewMethodNothing", groupViewMethods);
    subMenu.getItems().add(menuItemMethodsNothing);

    popupMenu.getItems().add(subMenu);
    popupMenu.getItems().add(new SeparatorMenuItem());
  }

  @Override
  protected void innerRegenerate() {
    methodsView.clear();
    attributesView.clear();
    for (Attribute a : ((SimpleEntity) component).getAttributes())
      addAttribute(a, false);
    for (Method m : ((SimpleEntity) component).getMethods())
      addMethod(m, false);
  }

  @Override
  protected int paintTextBoxes(GraphicsContext gc, Rectangle bounds, int textboxHeight, int offset) {
    if (displayAttributes) {
      offset += 10;
      gc.setLineWidth(BORDER_WIDTH);
      gc.setStroke(DEFAULT_BORDER_COLOR);
      gc.strokeLine(bounds.x, offset, bounds.x + bounds.width, offset);
      for (TextBoxAttribute tb : attributesView) {
        tb.setBounds(new Rectangle(bounds.x + 8, offset + 2, bounds.width - 15, textboxHeight + 2));
        tb.paintComponent(gc);
        offset += textboxHeight;
      }
    }
    if (displayMethods) {
      offset += 10;
      gc.setLineWidth(BORDER_WIDTH);
      gc.setStroke(DEFAULT_BORDER_COLOR);
      gc.strokeLine(bounds.x, offset, bounds.x + bounds.width, offset);
      for (final TextBoxMethod tb : methodsView) {
        tb.setBounds(new Rectangle(bounds.x + 8, offset + 2, bounds.width - 15, textboxHeight + 2));
        tb.paintComponent(gc);
        offset += textboxHeight;
      }
    }
    return offset;
  }

  /**
   * Method called before creating a new attribute, if modifications on attribute is necessary.
   *
   * @param attribute the attribute to prepare
   */
  protected abstract void prepareNewAttribute(Attribute attribute);

  /**
   * Method called before creating a new method, if modifications on method is necessary.
   *
   * @param method the method to prepare
   */
  protected abstract void prepareNewMethod(Method method);

  /**
   * Change the display style of parameters for the pressed TextBox if exists, or for all otherwise.
   *
   * @param newStyle the new display style
   */
  private void methodViewChangeClicked(ParametersViewStyle newStyle) {
    if (pressedTextBox instanceof TextBoxMethod)
      ((Method) pressedTextBox.getAssociatedComponent())
          .setParametersViewStyle(newStyle);
    else
      for (SimpleEntityView ev : getSelectedSimpleEntityView(parent))
        ev.methodViewChange(newStyle);
  }

  private void updateMenuItemMethodsView() {
    RadioMenuItem itemToSelect;
    ParametersViewStyle newView = null;

    if (pressedTextBox == null) {
      // Check si toutes les méthodes des entités sélectionnées ont la même vue.
      List<SimpleEntityView> selected = getSelectedSimpleEntityView(parent);
      List<TextBoxMethod> textbox = new LinkedList<>();
      for (SimpleEntityView view : selected)
        textbox.addAll(view.methodsView);

      for (int i = 0; i < textbox.size() - 1; i++) {
        Method current = (Method) textbox.get(i).getAssociatedComponent();
        Method next = (Method) textbox
            .get(i + 1).getAssociatedComponent();
        if (!current.getConcretParametersViewStyle().equals(
            next.getConcretParametersViewStyle())) {
          groupViewMethods.selectToggle(null);
          return;
        }
      }

      if (textbox.size() > 0)
        newView = ((Method) textbox.get(0).getAssociatedComponent())
            .getConcretParametersViewStyle();
    } else if (pressedTextBox instanceof TextBoxMethod) {
      newView = ((Method) pressedTextBox.getAssociatedComponent())
          .getConcretParametersViewStyle();
    }

    if (newView != null) {
      switch (newView) {
        case DEFAULT:
          itemToSelect = menuItemMethodsDefault;
          break;
        case NAME:
          itemToSelect = menuItemMethodsName;
          break;

        case NOTHING:
          itemToSelect = menuItemMethodsNothing;
          break;

        case TYPE:
          itemToSelect = menuItemMethodsType;
          break;

        case TYPE_AND_NAME:
          itemToSelect = menuItemMethodsAll;
          break;

        default:
          itemToSelect = menuItemMethodsAll;
          break;
      }

      itemToSelect.setSelected(true);
    }
  }

  private void updateMenuItemView() {
    RadioMenuItem menuItemToSelect;

    // Check si toutes les entités sélectionnées ont le même type de vue.
    List<SimpleEntityView> selected = getSelectedSimpleEntityView(parent);
    for (int i = 0; i < selected.size() - 1; i++) {
      SimpleEntityView view = selected.get(i), next = selected.get(i + 1);
      if (view.displayDefault != next.displayDefault
          || view.displayAttributes != next.displayAttributes
          || view.displayMethods != next.displayMethods) {
        groupView.selectToggle(null);
        return;
      }
    }

    if (displayDefault)
      menuItemToSelect = menuItemViewDefault;
    else if (displayAttributes && displayMethods)
      menuItemToSelect = menuItemViewAll;
    else if (displayAttributes)
      menuItemToSelect = menuItemViewAttributes;
    else if (displayMethods)
      menuItemToSelect = menuItemViewMethods;
    else
      menuItemToSelect = menuItemViewNothing;

    menuItemToSelect.setSelected(true);
  }

}
