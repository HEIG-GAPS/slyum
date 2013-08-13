package classDiagram.components;

import java.util.Observable;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import change.BufferEnumValue;
import change.Change;
import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent;

public class EnumValue extends Observable implements IDiagramComponent, Cloneable {

  protected final int id = ClassDiagram.getNextId();
  private String value;

  public EnumValue(String value) {
    if (value == null || value.isEmpty())
      throw new IllegalArgumentException(
              "Value for enum cannot be null or empty.");

    if (!value.matches(Attribute.REGEX_SEMANTIC_ATTRIBUTE))
      throw new IllegalArgumentException("Semantic name doesn't matche.");

    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    if (!value.matches(Attribute.REGEX_SEMANTIC_ATTRIBUTE))
      throw new IllegalArgumentException("Semantic name doesn't matche.");
    if (this.value.equals(value)) return;
    Change.push(new BufferEnumValue(this));
    this.value = value;
    Change.push(new BufferEnumValue(this));
    setChanged();
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element enumValue = doc.createElement(getXmlTagName());
    enumValue.setTextContent(getValue());
    return enumValue;
  }

  @Override
  public String getXmlTagName() {
    return "EnumValue";
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void select() {
    setChanged();
  }

  @Override
  public String toString() {
    return getValue();
  }

  @Override
  public EnumValue clone() throws CloneNotSupportedException {
    return new EnumValue(getValue());
  }

}
