package swing;

import swing.slyumCustomizedComponents.SButton;
import swing.slyumCustomizedComponents.SSeparator;
import swing.slyumCustomizedComponents.SToolBar;
import swing.slyumCustomizedComponents.SToolBarButton;
import java.awt.Component;

public class SPanelDiagramComponent 
    extends SToolBar {

  public enum Mode {
    CURSOR(getInstance().btnCursorMode), GRIP(getInstance().btnGripMode);

    private SButton btnMode;

    private Mode(SButton btn) {
      btnMode = btn;
    }

    public SButton getBtnMode() {
      return btnMode;
    }
  }


  private SButton btnCursorMode, btnGripMode;

  private Mode currentMode;
  private static SPanelDiagramComponent instance;

  public static SPanelDiagramComponent getInstance() {
    if (instance == null) instance = new SPanelDiagramComponent();
    return instance;
  }

  private SPanelDiagramComponent() {

    add(btnCursorMode = new SToolBarButton(SlyumAction.ACTION_MODE_CURSOR));
    add(btnGripMode = new SToolBarButton(SlyumAction.ACTION_ADD_GRIPS));
    add(new SSeparator());

    add(new SToolBarButton(SlyumAction.ACTION_ADD_CLASS));
    add(new SToolBarButton(SlyumAction.ACTION_ADD_INTERFACE));
    add(new SToolBarButton(SlyumAction.ACTION_ADD_ENUM));
    add(new SToolBarButton(SlyumAction.ACTION_ADD_ASSOCIATION_CLASS));
    add(new SSeparator());

    add(new SToolBarButton(SlyumAction.ACTION_ADD_INHERITANCE));
    add(new SToolBarButton(SlyumAction.ACTION_ADD_DEPENDENCY));
    add(new SToolBarButton(SlyumAction.ACTION_ADD_INNER_CLASS));
    add(new SSeparator());

    add(new SToolBarButton(SlyumAction.ACTION_ADD_ASSOCIATION));
    add(new SToolBarButton(SlyumAction.ACTION_ADD_AGGREGATION));
    add(new SToolBarButton(SlyumAction.ACTION_ADD_COMPOSITION));
    add(new SToolBarButton(SlyumAction.ACTION_ADD_MULTI_ASSOCIATION));
    add(new SSeparator());

    add(new SToolBarButton(SlyumAction.ACTION_ADD_NOTE));
    add(new SToolBarButton(SlyumAction.ACTION_LINK_NOTE));
  }

  public void setButtonModeStyle(SButton button) {
    // Réinitialise le style de tous les boutons.
    synchronized (getTreeLock()) {
      for (Component c : getComponents())
        if (c instanceof SButton) ((SButton) c).resetBackground();
    }

    // Attribut le nouveau bouton définissant le mode.
    button.setBackground(Slyum.THEME_COLOR.brighter());
    button.setContentAreaFilled(true);
  }

  public Mode getMode() {
    return currentMode;
  }

  public void setMode(Mode newMode) {
    currentMode = newMode;
    PropertyLoader.getInstance().getProperties()
            .put(PropertyLoader.MODE_CURSOR, currentMode.toString());
    PropertyLoader.getInstance().push();
    PanelClassDiagram.getInstance().getCurrentGraphicView()
            .deleteCurrentFactory();
    setButtonModeStyle(newMode.getBtnMode());
  }

  public void applyMode() {
    setButtonModeStyle(currentMode.getBtnMode());
  }
}
