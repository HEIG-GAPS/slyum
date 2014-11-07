package classDiagram.relationships;

import classDiagram.ClassDiagram;
import classDiagram.components.Entity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Observable;

public class InnerClass extends Observable implements Relation, IParentChild {

  protected final int id;
  protected Entity child, parent;
  
  public InnerClass(Entity child, Entity parent) {
    init(child, parent);
    id = ClassDiagram.getNextId();
  }

  public InnerClass(Entity child, Entity parent, int id) {
    init(child, parent);
    this.id = id;
  }

  /**
   * Call by construtor for init parameters.
   * 
   * @param child
   *          the child given in constructor
   * @param parent
   *          the parent given in constructor
   */
  private void init(Entity child, Entity parent) {

    this.child = child;
    this.child.addParent(this);

    this.parent = parent;
    this.parent.addChild(this);
  }

  /**
   * Get the child for this inheritance.
   * 
   * @return the child for this inheritance
   */
  @Override
  public Entity getChild() {
    return child;
  }

  @Override
  public int getId() {
    return id;
  }

  /**
   * Get the parent for this inheritance.
   * 
   * @return the parent for this inheritance
   */
  @Override
  public Entity getParent() {
    return parent;
  }

  @Override
  public void select() {
    setChanged();
  }

  /**
   * Set the child for this inheritance.
   * 
   * @param child
   *          the new child for this inheritance
   */
  @Override
  public void setChild(Entity child) {    
    this.child.removeParent(this);
    this.child = child;
    this.child.addParent(this);
    setChanged();
  }

  /**
   * Set the parent for this inheritance.
   * 
   * @param parent
   *          the new parent for this inheritance
   */
  @Override
  public void setParent(Entity parent) {
    this.parent.removeChild(this);
    this.parent = parent;
    this.parent.addChild(this);
    setChanged();
  }

  @Override
  public Entity getSource() {
    return getChild();
  }

  @Override
  public Entity getTarget() {
    return getParent();
  }

  @Override
  public void setSource(Entity entity) {
    setChild(entity);
  }

  @Override
  public void setTarget(Entity entity) {
    setParent(entity);
  }

  @Override
  public String toString() {
    return getChild().getName() + " - " + getParent().getName();
  }

  @Override
  public String getXmlTagName() {
    return "inheritance";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element inheritance = doc.createElement(getXmlTagName()), child = doc
            .createElement("child"), parent = doc.createElement("parent");
    inheritance.setAttribute("id", String.valueOf(id));
    inheritance.setAttribute("innerClass", "true");

    child.setTextContent(String.valueOf(this.child.getId()));
    parent.setTextContent(String.valueOf(this.parent.getId()));
    inheritance.appendChild(child);
    inheritance.appendChild(parent);

    return inheritance;
  }
}
