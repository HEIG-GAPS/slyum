package swing;

import swing.slyumCustomizedComponents.SSeparator;
import swing.slyumCustomizedComponents.SToolBar;
import swing.slyumCustomizedComponents.SToolBarButton;


public class SPanelFileComponent extends SToolBar {
  
  private static SPanelFileComponent instance;
  public static SPanelFileComponent getInstance() {
    if (instance == null) instance = new SPanelFileComponent();
    return instance;
  }

  private SPanelFileComponent() {
    add(new SToolBarButton(SlyumAction.ACTION_NEW_PROJECT));
    add(new SToolBarButton(SlyumAction.ACTION_OPEN_PROJECT));
    add(new SToolBarButton(SlyumAction.ACTION_SAVE_PROJECT));
    add(new SSeparator());
    
    add(new SToolBarButton(SlyumAction.ACTION_EXPORT_AS_IMAGE));
    add(new SToolBarButton(SlyumAction.ACTION_COPY_TO_CLIPBOARD));
    add(new SToolBarButton(SlyumAction.ACTION_PRINT));
  }
}
