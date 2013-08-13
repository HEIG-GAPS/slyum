package change;

import classDiagram.components.EnumValue;

public class BufferEnumValue implements Changeable {

  private EnumValue current, copy;

  public BufferEnumValue(EnumValue enumValue) {
    current = enumValue;
    try {
      copy = current.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void restore() {
    current.setValue(copy.getValue());
    current.notifyObservers();
  }

}
