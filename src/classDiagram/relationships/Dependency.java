package classDiagram.relationships;

import classDiagram.ClassDiagram;
import classDiagram.components.Entity;
import graphic.textbox.ILabelTitle;
import java.util.Observable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Represent a dependency in UML structure.
 * 
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Dependency extends Observable implements Relation, ILabelTitle {
  protected final int id;

  private String label = "use";

  Entity source, target;

  /**
   * Create a new dependency with the given entities source and target. The
   * source will be the entity depending on the target.
   * 
   * @param source
   *          the entity source - depending on target
   * @param target
   *          the entity target - the source depends
   */
  public Dependency(Entity source, Entity target) {
    init(source, target);

    id = ClassDiagram.getNextId();
  }

  /**
   * Create a new dependency with the given entities source and target. The
   * source will be the entity depending on the target. Don't generate a new id
   * and use this given in parameter.
   * 
   * @param source
   *          the entity source - depending on target
   * @param target
   *          the entity target - the source depends
   */
  public Dependency(Entity source, Entity target, int id) {
    init(source, target);

    this.id = id;
  }

  @Override
  public int getId() {
    return id;
  }

  @Override
  public String getLabel() {
    return label;
  }

  /**
   * Get the source for this dependency.
   * 
   * @return the source for this dependency
   */
  public Entity getSource() {
    return source;
  }

  /**
   * Get the target for this dependency.
   * 
   * @return the target for this dependency
   */
  public Entity getTarget() {
    return target;
  }

  /**
   * Call by construtor for init parameters.
   * 
   * @param source
   *          the source given in constructor
   * @param target
   *          the target given in constructor
   */
  private void init(Entity source, Entity target) {
    if (source == null || target == null)
      throw new IllegalArgumentException("source or/and target is null");

    this.source = source;
    this.target = target;
  }

  @Override
  public void select() {
    setChanged();
  }

  @Override
  public void setLabel(String text) {
    if (text == null) text = "";

    label = text;

    setChanged();
  }

  /**
   * Set the source for this dependency.
   * 
   * @param source
   *          the new source for this dependency
   */
  public void setSource(Entity source) {
    this.source = source;

    setChanged();
  }

  /**
   * Set the target for this dependency.
   * 
   * @param target
   *          the new target for this dependency
   */
  public void setTarget(Entity target) {
    this.target = target;

    setChanged();
  }

  @Override
  public String toString() {
    return getLabel();
  }

  @Override
  public String getXmlTagName() {
    return "dependency";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element dependency = doc.createElement(getXmlTagName()), source = doc
            .createElement("source"), target = doc.createElement("target");

    dependency.setAttribute("id", String.valueOf(id));
    dependency.setAttribute("label", getLabel());

    source.setTextContent(String.valueOf(this.source.getId()));
    target.setTextContent(String.valueOf(this.target.getId()));
    dependency.appendChild(source);
    dependency.appendChild(target);

    return dependency;
  }
}
