package graphic.entity;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JRadioButtonMenuItem;

public class EntityTypeViewMenu extends JMenu {
  
  /*-------------------------Enum type----------------------------------------*/
  public enum EntityTypeView {
    ALL("all"),
    NOTHING("nothing"),
    METHODS("Only methods"),
    ATTRIBUTES("Only attributes");
    
    String label;
    EntityTypeView(String label) {
        this.label = label;
      }
    
    public String getLabel() {
      return label;
    }
  };
  /*---------------------End Enum type----------------------------------------*/
  
  /* ---------------------Radio button item-----------------------------------*/
  private class RadioButton extends JRadioButtonMenuItem {
    private EntityTypeView typeView;

    public RadioButton(EntityTypeView typeView, ButtonGroup group) {
      super(typeView.getLabel());
      init(typeView, group);
    }

    public void init(EntityTypeView typeView, ButtonGroup group) {
      this.typeView = typeView;
      setActionCommand(typeView.toString());
      group.add(this);
    }

    public EntityTypeView getTypeView() {
      return typeView;
    }
  }
  /* -----------------End Radio button item-----------------------------------*/

  public EntityTypeViewMenu() {
    init();
  }

  public EntityTypeViewMenu(String text) {
    super(text);
    init();
  }

  public EntityTypeViewMenu(Action action) {
    super(action);
    init();
  }

  public EntityTypeViewMenu(String text,
      boolean checked) {
    super(text, checked);
    init();
  }
  
  public void init() {
    ButtonGroup group = new ButtonGroup();
    
    // Création du menu
    for (EntityTypeView typeView : 
          EntityTypeView.values()) 
      add(new RadioButton(typeView, group));
  }
  
  public void setSelected(EntityTypeView typeView) {
    searchRadioButton(typeView).setSelected(true);
  }
  
  public EntityTypeView getSelected() {
    for (Component c : getComponents())
      if (c instanceof RadioButton && ((RadioButton)c).isSelected())
        return ((RadioButton)c).getTypeView();
    
    return null;
  }
  
  private RadioButton searchRadioButton(EntityTypeView typeView) {
    for (Component c : getComponents())
      if (c instanceof RadioButton && 
          ((RadioButton)c).getTypeView().equals(typeView))
        return (RadioButton)c;
    
    return null;
  }

}
