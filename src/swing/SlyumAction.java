package swing;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import utility.OSValidator;
import utility.PersonalizedIcon;
import utility.SMessageDialog;

/**
 * This class store all actions used in Slyum. 
 * All actions listener in Slyum MUST go through this class. For maintening
 * reasons, constructor is private so all action have to be created in this
 * file.
 * To create a new action in Slyum, follow these steps:
 *  - Add a static value SlyumAction representing the new action.
 *  - Add the action corresponding to the actionCommand.
 * @author David Miserez
 */
public abstract class SlyumAction extends AbstractAction {
  
  private static String DEFAULT_ICON_EXTENSION = ".png";
  
  /* ------------------------ STATIC -----------------------------------------*/
  
  // Menu File -----------------------------------------------------------------
  public static final SlyumAction ACTION_NEW_PROJECT = 
      new SlyumAction(
          "New Project", "new", KeyEvent.VK_N, 
          KeyStroke.getKeyStroke(KeyEvent.VK_N, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().newProject();
    }
  };
  
  public static final SlyumAction ACTION_OPEN_PROJECT = 
      new SlyumAction(
          "Open Project...", "open", KeyEvent.VK_O, 
          KeyStroke.getKeyStroke(KeyEvent.VK_O, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().openFromXML();
    }
  };
  
  public static final SlyumAction ACTION_SAVE_PROJECT = 
      new SlyumAction(
          "Save", "save", KeyEvent.VK_S, 
          KeyStroke.getKeyStroke(KeyEvent.VK_S, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().saveToXML(false);
    }
  };
  
  public static final SlyumAction ACTION_SAVE_AS_PROJECT = 
      new SlyumAction(
          "Save as...", "save-as", KeyEvent.VK_A, 
          KeyStroke.getKeyStroke(KeyEvent.VK_S, Slyum.MENU_SHORTCUT_KEY_MASK | 
                                                InputEvent.SHIFT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().saveToXML(true);
    }
  };
  
  public static final SlyumAction ACTION_EXPORT_AS_IMAGE = 
      new SlyumAction(
          "Export as image...", "export", KeyEvent.VK_E, 
          KeyStroke.getKeyStroke(KeyEvent.VK_E, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().exportAsImage();
    }
  };
  
  public static final SlyumAction ACTION_COPY_TO_CLIPBOARD = 
      new SlyumAction(
          "Copy selection to clipboard", "klipper", KeyEvent.VK_C, 
          KeyStroke.getKeyStroke(KeyEvent.VK_C, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().getCurrentGraphicView()
                                     .copyDiagramToClipboard();
    }
  };
  
  public static final SlyumAction ACTION_LOCATE_IN = 
      new SlyumAction(
          "Locate in" + (OSValidator.IS_MAC ? "Finder" : "Explorer"), "explore",
          KeyEvent.VK_L, 
          KeyStroke.getKeyStroke(KeyEvent.VK_L, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
          Desktop.getDesktop().open(
              PanelClassDiagram.getFileOpen().getParentFile());
        } catch (IOException exception) {
          SMessageDialog.showErrorMessage("No open file!");
        }
    }
    { setEnabled(false); }};
  
  public static final SlyumAction ACTION_PRINT = 
      new SlyumAction(
          "Print...", "print", KeyEvent.VK_P, 
          KeyStroke.getKeyStroke(KeyEvent.VK_P, Slyum.MENU_SHORTCUT_KEY_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      PanelClassDiagram.getInstance().initPrinting();
    }
  };
  
  public static final SlyumAction ACTION_PAGE_SETUP = 
      new SlyumAction(
          "Page Setup...", "page-setup", KeyEvent.VK_G) {
    @Override
    public void actionPerformed(ActionEvent e) {
        SlyumPrinterJob.pageDialog(
                PanelClassDiagram.getInstance().getCurrentGraphicView());
    }
  };
  
  public static final SlyumAction ACTION_PROPERTIES = 
      new SlyumAction(
          "Properties...", "properties", KeyEvent.VK_R, 
          KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
        openProperties();
    }
  };













  
  // Menu Diagram
  public static final SlyumAction ACTION_MODE_CURSOR = 
      new SlyumAction(
          "Default cursor", "pointer-arrow", KeyEvent.VK_A, 
          KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.ALT_DOWN_MASK)) {
    @Override
    public void actionPerformed(ActionEvent e) {
      SPanelDiagramComponent.getInstance()
                            .setMode(SPanelDiagramComponent.Mode.CURSOR);
    }
  };
  
  /* ------------------------ END STATIC -------------------------------------*/
  
  private SlyumAction() {
  }

  private SlyumAction(String name) {
    super(name);
    initialize();
  }

  private SlyumAction(String name, String iconName) {
    super(name, 
          PersonalizedIcon.createImageIcon(iconName + DEFAULT_ICON_EXTENSION));
    initialize();
  }

  private SlyumAction(
      String name, String iconName, int mnemonic) {
    super(name, 
          PersonalizedIcon.createImageIcon(
              Slyum.ICON_PATH + iconName + DEFAULT_ICON_EXTENSION));
    putValue(MNEMONIC_KEY, mnemonic);
    initialize();
  }

  private SlyumAction(
      String name, String iconName, int mnemonic, KeyStroke accelerator) {
    super(name, 
          PersonalizedIcon.createImageIcon(
              Slyum.ICON_PATH + iconName + DEFAULT_ICON_EXTENSION));
    putValue(MNEMONIC_KEY, mnemonic);
    putValue(ACCELERATOR_KEY, accelerator);
    initialize();
  }
  
  private void initialize() {
    putValue(SHORT_DESCRIPTION, getValue(NAME) + 
             " (" + getValue(ACCELERATOR_KEY) + ")");
  }
}
