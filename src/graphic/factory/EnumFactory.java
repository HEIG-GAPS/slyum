package graphic.factory;

import classDiagram.components.EnumEntity;
import graphic.GraphicComponent;
import graphic.GraphicView;
import graphic.entity.EntityView;
import graphic.entity.EnumView;
import swing.SPanelDiagramComponent;
public class EnumFactory extends EntityFactory {

  public EnumFactory(GraphicView parent) {
    super(parent);

    GraphicView.setButtonFactory(SPanelDiagramComponent.getInstance()
            .getBtnEnum());
  }

  @Override
  public GraphicComponent create() {
    EnumEntity ee = new EnumEntity("Enum");
    final EntityView e = new EnumView(parent, ee);

    parent.addEntity(e);
    classDiagram.addEnumEntity(ee);
    initializeBounds(e);
    
    return e;
  }

}
