package change;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.EnumEntity;
import classDiagram.components.EnumValue;

public class BufferCreationEnumValue implements Changeable {

  private EnumEntity enumEntitySource;
  private EnumValue enumValue;
  private boolean creation;
  private int index;

  public BufferCreationEnumValue(EnumEntity enumEntitySource,
          EnumValue enumValue, boolean creation, int index) {
    this.enumEntitySource = enumEntitySource;
    this.enumValue = enumValue;
    this.creation = creation;
    this.index = index;
  }

  @Override
  public void restore() {
    if (creation) {
      enumEntitySource.addEnumValue(enumValue);
      enumEntitySource.notifyObservers(UpdateMessage.ADD_ENUM_NO_EDIT);
      enumEntitySource.moveEnumPosition(enumValue, index
              - enumEntitySource.getEnumValues().size() + 1);
      enumEntitySource.notifyObservers();
    } else {
      enumEntitySource.removeEnumValue(enumValue);
      enumEntitySource.notifyObservers();
    }
  }

}
