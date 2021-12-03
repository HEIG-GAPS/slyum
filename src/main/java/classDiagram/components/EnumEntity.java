package classDiagram.components;

import change.BufferCreationEnumValue;
import change.Change;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.XMLParser.EntityType;

import java.util.LinkedList;
import java.util.List;

public class EnumEntity extends Entity {

  public static final String STEREOTYPE_ENUM = "enum";

  private List<EnumValue> values = new LinkedList<>();

  public EnumEntity(String name) {
    super(name);
    initializeComponents();
  }

  public EnumEntity(String name, int id) {
    super(name, id);
    initializeComponents();
  }

  public EnumEntity(Entity e) {
    super(e);
    initializeComponents();
  }

  public void initializeComponents() {
    setStereotype(STEREOTYPE_ENUM);
  }

  public boolean addEnumValue(EnumValue value) {
    if (!values.contains(value)) {
      if (values.add(value)) {
        setChanged();
        int index = values.indexOf(value);
        Change.push(new BufferCreationEnumValue(this, value, false, index));
        Change.push(new BufferCreationEnumValue(this, value, true, index));
        return true;
      }
    }
    return false;
  }

  public void createEnumValue() {
    EnumValue value = new EnumValue("VALUE");

    if (addEnumValue(value)) notifyObservers(UpdateMessage.ADD_ENUM);
  }

  public boolean removeEnumValue(EnumValue value) {
    int index = values.indexOf(value);
    boolean success = values.remove(value);
    if (success) {
      setChanged();
      Change.push(new BufferCreationEnumValue(this, value, true, index));
      Change.push(new BufferCreationEnumValue(this, value, false, index));
    }
    return success;
  }

  public void moveEnumPosition(EnumValue value, int offset) {
    moveComponentPosition(values, value, offset);
  }

  /**
   * Return enum values. Not a copy.
   *
   * @return enum values (not copied).
   */
  public List<EnumValue> getEnumValues() {
    return values;
  }

  @Override
  protected String getEntityType() {
    return EntityType.ENUM.toString();
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element enumEntity = super.getXmlElement(doc);
    for (EnumValue value : values)
      enumEntity.appendChild(value.getXmlElement(doc));
    return enumEntity;
  }

  @Override
  public EnumEntity clone() throws CloneNotSupportedException {
    EnumEntity entity = (EnumEntity) super.clone();

    for (EnumValue value : values)
      entity.addEnumValue(new EnumValue(value.getValue()));

    return entity;
  }

}
