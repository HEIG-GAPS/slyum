package swing;

import java.awt.Dimension;
import swing.slyumCustomizedComponents.SSlider;
import swing.slyumCustomizedComponents.SSeparator;
import swing.slyumCustomizedComponents.SToolBar;
import swing.slyumCustomizedComponents.SToolBarButton;

import graphic.GraphicView;

public class SPanelElement 
    extends SToolBar 
    implements IListenerComponentSelectionChanged {
 
  private static SPanelElement instance;
  public static SPanelElement getInstance() {
    if (instance == null) instance = new SPanelElement();
    return instance;
  }
  
  private SSlider sliderZoom;

  private SPanelElement() {
    add(new SToolBarButton(SlyumAction.ACTION_UNDO));
    add(new SToolBarButton(SlyumAction.ACTION_REDO));
    add(new SSeparator());
    
    add(new SToolBarButton(SlyumAction.ACTION_ASSOCIATE_NOTE));
    add(new SToolBarButton(SlyumAction.ACTION_COLOR));
    add(new SToolBarButton(SlyumAction.ACTION_DUPLICATE));
    add(new SToolBarButton(SlyumAction.ACTION_DELETE));
    add(new SSeparator());
    
    add(new SToolBarButton(SlyumAction.ACTION_ALIGN_TOP));
    add(new SToolBarButton(SlyumAction.ACTION_ALIGN_BOTTOM));
    add(new SToolBarButton(SlyumAction.ACTION_ALIGN_RIGHT));
    add(new SToolBarButton(SlyumAction.ACTION_ALIGN_LEFT));
    add(new SToolBarButton(SlyumAction.ACTION_ADJUST_SIZE));
    add(new SSeparator());
    
    sliderZoom = new SSlider(100, 50, 200) {
      @Override
      public void setValue(int value) {
        super.setValue(value);

        if (PanelClassDiagram.getInstance() != null)
          PanelClassDiagram.getInstance().getCurrentGraphicView().repaint();
      }
    };
    sliderZoom.setPreferredSize(new Dimension(100, 15));
    add(sliderZoom);
    add(new SSeparator());

    
    add(new SToolBarButton(SlyumAction.ACTION_MOVE_TOP));
    add(new SToolBarButton(SlyumAction.ACTION_MOVE_UP));
    add(new SToolBarButton(SlyumAction.ACTION_MOVE_DOWN));
    add(new SToolBarButton(SlyumAction.ACTION_MOVE_BOTTOM));

    SlyumAction.ACTION_ALIGN_TOP.setEnabled(false);
    SlyumAction.ACTION_ALIGN_BOTTOM.setEnabled(false);
    SlyumAction.ACTION_ALIGN_RIGHT.setEnabled(false);
    SlyumAction.ACTION_ALIGN_LEFT.setEnabled(false);
    SlyumAction.ACTION_ADJUST_SIZE.setEnabled(false);
    SlyumAction.ACTION_UNDO.setEnabled(false);
    SlyumAction.ACTION_REDO.setEnabled(false);
  }

  @Override
  public void componentSelectionChanged() {
    GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
    boolean enabled = gv.countSelectedComponents() > 0;
    SlyumAction.ACTION_DELETE.setEnabled(enabled);
    SlyumAction.ACTION_DUPLICATE.setEnabled(enabled);
    updateBtnState();
  }

  public void updateBtnState() {
    GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
    int nb = gv.countSelectedEntities();
    boolean enable = nb > 1;
    SlyumAction.ACTION_ALIGN_TOP.setEnabled(enable);
    SlyumAction.ACTION_ALIGN_BOTTOM.setEnabled(enable);
    SlyumAction.ACTION_ALIGN_RIGHT.setEnabled(enable);
    SlyumAction.ACTION_ALIGN_LEFT.setEnabled(enable);

    enable = nb > 0;
    SlyumAction.ACTION_ADJUST_SIZE.setEnabled(enable);

    enable = gv.countEntities() > 1 && gv.countSelectedEntities() > 0;
    SlyumAction.ACTION_MOVE_TOP.setEnabled(enable);
    SlyumAction.ACTION_MOVE_UP.setEnabled(enable);
    SlyumAction.ACTION_MOVE_DOWN.setEnabled(enable);
    SlyumAction.ACTION_MOVE_BOTTOM.setEnabled(enable);
  }

  public SSlider getSliderZoom() {
    return sliderZoom;
  }
}
