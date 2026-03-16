package graphic.entity;

import classDiagram.IDiagramComponent;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.EnumEntity;
import classDiagram.components.EnumValue;
import graphic.GraphicView;
import graphic.textbox.TextBox;
import graphic.textbox.TextBoxEnumValue;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;

public class EnumView extends EntityView {

  public enum TypeEnumDisplay {
    DEFAULT, VISIBLE, HIDE
  }

  public final static String ACTION_ADD_ENUM_VALUE = "actionAddEnumValue";
  public final static String ACTION_ENUM_VALUES_DEFAULT = "actionEnumValuesDefault";
  public final static String ACTION_ENUM_VALUES_VISIBLE = "actionEnumValuesVisible";
  public final static String ACTION_ENUM_VALUES_HIDE = "actionEnumValuesHide";

  private List<TextBoxEnumValue> viewValues = new LinkedList<>();
  private TypeEnumDisplay typeEnumDisplay = TypeEnumDisplay.DEFAULT;
  private ToggleGroup btnGrpEnumValuesVisible;
  private RadioMenuItem radBtnDefault, radBtnVisible, radBtnHide;

  public EnumView(GraphicView parent, EnumEntity component) {
    super(parent, component);
  }

  @Override
  protected void initializeMenuItemsAddElements(ContextMenu popupmenu) {
    popupmenu.getItems().add(makeMenuItem("Add enum value", ACTION_ADD_ENUM_VALUE,
                               "add-enum-value"));
    popupmenu.getItems().add(new SeparatorMenuItem());
  }

  @Override
  protected void initializeMenuItemsPropertiesElements(ContextMenu popupMenu) {
    // No properties elements.
  }

  @Override
  protected void initializeMenuViews(ContextMenu popupMenu) {
    Menu subMenu = new Menu("Values view");
    Image img = PersonalizedIcon.createImageIcon("eye.png");
    if (img != null) subMenu.setGraphic(new ImageView(img));
    btnGrpEnumValuesVisible = new ToggleGroup();
    subMenu.getItems().add(radBtnDefault = makeRadioButtonMenuItem("Default",
                                                        ACTION_ENUM_VALUES_DEFAULT, btnGrpEnumValuesVisible));
    subMenu.getItems().add(radBtnVisible = makeRadioButtonMenuItem("Visible",
                                                        ACTION_ENUM_VALUES_VISIBLE, btnGrpEnumValuesVisible));
    subMenu.getItems().add(radBtnHide = makeRadioButtonMenuItem("Hide",
                                                     ACTION_ENUM_VALUES_HIDE, btnGrpEnumValuesVisible));
    radBtnDefault.setSelected(true);
    popupMenu.getItems().add(subMenu);
    popupMenu.getItems().add(new SeparatorMenuItem());
  }

  @Override
  protected int paintTextBoxes(GraphicsContext gc, Rectangle bounds,
                               int textboxHeight, int offset) {
    if (isEnumValuesVisible()) {
      offset += 10;
      gc.setLineWidth(BORDER_WIDTH);
      gc.setStroke(DEFAULT_BORDER_COLOR_FX);
      gc.strokeLine(bounds.x, offset, bounds.x + bounds.width, offset);

      // draw values (enum)
      for (TextBoxEnumValue tb : viewValues) {
        tb.setBounds(new Rectangle(bounds.x + 8, offset + 2, bounds.width - 15,
                                   textboxHeight + 2));
        // TODO: tb.paintComponent(gc) - TextBox not yet migrated to JavaFX

        offset += textboxHeight;
      }
    }
    return offset;
  }

  @Override
  public int computeHeight(int classNameHeight, int stereotypeHeight,
                           int elementsHeight) {
    int height = super.computeHeight(classNameHeight, stereotypeHeight,
                                     elementsHeight);

    if (isEnumValuesVisible())
      height += 10 + elementsHeight * viewValues.size();
    return height;
  }

  @Override
  protected void innerRegenerate() {
    viewValues.clear();
    for (EnumValue value : ((EnumEntity) component).getEnumValues())
      addEnumValue(value, false);
  }

  @Override
  public boolean removeTextBox(TextBox tb) {

    if (((EnumEntity) component).removeEnumValue(((EnumValue) tb
        .getAssociatedComponent()))) {
      component.notifyObservers();
      updateHeight();
      return true;
    }

    return false;
  }

  @Override
  public List<TextBox> getAllTextBox() {
    List<TextBox> tbs = super.getAllTextBox();
    tbs.addAll(viewValues);
    return tbs;
  }

  @Override
  public void maybeShowPopup(MouseEvent e, ContextMenu popupMenu) {
    if (e.getButton() == MouseButton.SECONDARY && e.getEventType() == MouseEvent.MOUSE_RELEASED) {
      updateMenuItemView();

      // If context menu is requested on a TextBox, customize popup menu.
      if (pressedTextBox != null) {

        menuItemMoveUp.setDisable(viewValues.indexOf(pressedTextBox) == 0);
        menuItemMoveDown.setDisable(!(viewValues.size() == 0 || viewValues
                                                                   .indexOf(pressedTextBox) != viewValues.size() - 1));
      } else {
        menuItemMoveUp.setDisable(true);
        menuItemMoveDown.setDisable(true);
      }
    }
    super.maybeShowPopup(e, popupMenu);
  }

  public void addEnumValue(EnumValue value, final boolean editing) {
    final TextBoxEnumValue newTextBox = new TextBoxEnumValue(parent, value);
    viewValues.add(newTextBox);
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        updateHeight();
        if (editing) newTextBox.editing();
      }
    });
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    super.actionPerformed(e);

    if (ACTION_ADD_ENUM_VALUE.equals(((MenuItem)e.getSource()).getId())) {
      ((EnumEntity) component).createEnumValue();

      // Action Move up and down
    } else if (Slyum.ACTION_TEXTBOX_UP.equals(((MenuItem)e.getSource()).getId())
               || Slyum.ACTION_TEXTBOX_DOWN.equals(((MenuItem)e.getSource()).getId())) {

      int offset = 1;
      if (Slyum.ACTION_TEXTBOX_UP.equals(((MenuItem)e.getSource()).getId())) offset = -1;

      if (pressedTextBox.getClass() == TextBoxEnumValue.class) {
        EnumValue value = (EnumValue) ((TextBoxEnumValue) pressedTextBox)
            .getAssociatedComponent();
        ((EnumEntity) component).moveEnumPosition(value, offset);
      }

      // Action duplicate item
    } else if (Slyum.ACTION_DUPLICATE.equals(((MenuItem)e.getSource()).getId())) {
      if (pressedTextBox != null) {
        IDiagramComponent component = pressedTextBox.getAssociatedComponent();
        EnumEntity entity = (EnumEntity) getAssociatedComponent();
        if (component instanceof EnumValue) {
          EnumValue value = new EnumValue(((EnumValue) component).getValue());
          List<EnumValue> values = entity.getEnumValues();
          entity.addEnumValue(value);
          entity.notifyObservers(UpdateMessage.ADD_ENUM_NO_EDIT);
          entity.moveEnumPosition(value,
                                  values.indexOf(component) - values.size() + 1);
          entity.notifyObservers();
        }
      }
    } else if (ACTION_ENUM_VALUES_DEFAULT.equals(((MenuItem)e.getSource()).getId())) {
      changeViewForSelectedEnums(TypeEnumDisplay.DEFAULT);
    } else if (ACTION_ENUM_VALUES_VISIBLE.equals(((MenuItem)e.getSource()).getId())) {
      changeViewForSelectedEnums(TypeEnumDisplay.VISIBLE);
    } else if (ACTION_ENUM_VALUES_HIDE.equals(((MenuItem)e.getSource()).getId())) {
      changeViewForSelectedEnums(TypeEnumDisplay.HIDE);
    }
    component.notifyObservers();
  }

  public static void changeViewForSelectedEnums(TypeEnumDisplay view) {
    for (EnumView enumView : getSelected())
      enumView.setTypeEnumDisplay(view);
  }

  public boolean isEnumValuesVisible() {
    switch (typeEnumDisplay) {
      case HIDE:
        return false;
      case VISIBLE:
        return true;
      case DEFAULT:
      default:
        return PanelClassDiagram.getInstance().getClassDiagram().getDefaultViewEnum();
    }
  }

  public TypeEnumDisplay getTypeEnumDisplay() {
    return typeEnumDisplay;
  }

  public void setTypeEnumDisplay(TypeEnumDisplay typeEnumDisplay) {
    this.typeEnumDisplay = typeEnumDisplay;
    updateHeight();
  }

  public static List<EnumView> getAll() {
    return extractEnumViewFromList(
        MultiViewManager.getSelectedGraphicView().getEntitiesView());
  }

  public static List<EnumView> getSelected() {
    return extractEnumViewFromList(
        MultiViewManager.getSelectedGraphicView().getSelectedEntities());
  }

  private static List<EnumView> extractEnumViewFromList(List<EntityView> list) {
    LinkedList<EnumView> enums = new LinkedList<>();
    for (EntityView view : list)
      if (view instanceof EnumView) enums.add((EnumView) view);
    return enums;
  }

  @Override
  public void restore() {
    super.restore();
    parent.addEntity(this);
    restoreEntity();
    repaint();
  }

  protected void restoreEntity() {
    if (parent.getClassDiagram().searchComponentById(getAssociatedComponent().getId()) == null)
      parent.getClassDiagram().addEnumEntity((EnumEntity) getAssociatedComponent());
  }

  private void updateMenuItemView() {
    List<EnumView> enums = getSelected();
    RadioMenuItem itemToSelect;

    // Check si toutes les entités sélectionnées ont le même type de vue.
    for (int i = 0; i < enums.size() - 1; i++)
      if (!enums.get(i).getTypeEnumDisplay()
                .equals(enums.get(i + 1).getTypeEnumDisplay())) {
        btnGrpEnumValuesVisible.getToggles().forEach(t -> t.setSelected(false));
        return;
      }

    switch (getTypeEnumDisplay()) {
      case HIDE:
        itemToSelect = radBtnHide;
        break;
      case VISIBLE:
        itemToSelect = radBtnVisible;
        break;
      case DEFAULT:
      default:
        itemToSelect = radBtnDefault;
        break;
    }

    itemToSelect.setSelected(true);
  }

  @Override
  public void update(Observable arg0, Object arg1) {
    boolean enable = false;
    if (arg1 != null && arg1.getClass() == UpdateMessage.class)
      switch ((UpdateMessage) arg1) {
        case ADD_ENUM:
          enable = true;
        case ADD_ENUM_NO_EDIT:
          List<EnumValue> values = ((EnumEntity) component).getEnumValues();
          addEnumValue(values.get(values.size() - 1), enable);
          break;
        default:
          super.update(arg0, arg1);
          break;
      }
    else
      regenerateEntity();
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element entityView = super.getXmlElement(doc);
    entityView.setAttribute("enumValuesVisible",
                            String.valueOf(getTypeEnumDisplay()));
    return entityView;
  }

}
