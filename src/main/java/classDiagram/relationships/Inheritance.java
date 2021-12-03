package classDiagram.relationships;

import classDiagram.ClassDiagram;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.SimpleEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.OverridesAndImplementationsDialog;
import utility.SMessageDialog;

import java.util.Observable;

/**
 * Represent a inheritance in UML structure. This inheritance, depends on the entites participating in inheritance, will
 * be a generalization or a relalization.
 *
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Inheritance extends Observable implements Relation, IParentChild {

  public static boolean validate(Entity child, Entity parent) {
    /* Cette méthode de validation est a revoir. Désactivation en attendant.
     * boolean valide = true; valide &= child != parent; for (Entity e :
     * child.getAllChilds()) valide &= !parent.equals(e); if (!valide)
     * SMessageDialog.showErrorMessage(
     * "Error in hierarchical class structure.\n" +
     * "Impossible to create inheritance association."); return valide; */

    // Test qu'une classe ne peut être parente d'une interface.
    boolean test = child instanceof InterfaceEntity &&
                   parent instanceof InterfaceEntity ||
                   child instanceof ClassEntity;
    return test;
  }

  protected final int id;
  protected SimpleEntity child, parent;

  /**
   * Create a new inheritance with the given entities child and parent.
   *
   * @param child the child entity
   * @param parent the parent entity
   */
  public Inheritance(SimpleEntity child, SimpleEntity parent) {
    init(child, parent);

    id = ClassDiagram.getNextId();
  }

  /**
   * Create a new inheritance with the given entities child and parent. Don't generate a new id and use this given in
   * parameter.
   *
   * @param child the child entity
   * @param parent the parent entity
   * @param id the unique ID of the component.
   */
  public Inheritance(SimpleEntity child, SimpleEntity parent, int id) {
    init(child, parent);

    this.id = id;
  }

  /**
   * Get the child for this inheritance.
   *
   * @return the child for this inheritance
   */
  @Override
  public SimpleEntity getChild() {
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
  public SimpleEntity getParent() {
    return parent;
  }

  /**
   * Call by construtor for init parameters.
   *
   * @param child the child given in constructor
   * @param parent the parent given in constructor
   */
  private void init(SimpleEntity child, SimpleEntity parent) {
    if (child.getClass() == InterfaceEntity.class
        && parent.getClass() == ClassEntity.class)
      throw new IllegalArgumentException("interface cannot implements class");

    this.child = child;
    this.child.addParent(this);

    this.parent = parent;
    this.parent.addChild(this);
  }

  @Override
  public void select() {
    setChanged();
  }

  /**
   * Set the child for this inheritance.
   *
   * @param child the new child for this inheritance
   */
  @Override
  public void setChild(Entity child) {
    if (!(child instanceof SimpleEntity))
      throw new IllegalArgumentException(
          "Child must be an instance of SimpleEntity.");

    this.child.removeParent(this);
    this.child = (SimpleEntity) child;
    this.child.addParent(this);
    setChanged();
  }

  /**
   * Set the parent for this inheritance.
   *
   * @param parent the new parent for this inheritance
   */
  @Override
  public void setParent(Entity parent) {

    if (!(parent instanceof SimpleEntity))
      throw new IllegalArgumentException(
          "Parent must be an instance of SimpleEntity.");

    this.parent.removeChild(this);
    this.parent = (SimpleEntity) parent;
    this.parent.addChild(this);
    setChanged();
  }

  public void showOverridesAndImplementations() {
    boolean thereAbstractMethod = false;
    OverridesAndImplementationsDialog oai =
        new OverridesAndImplementationsDialog(parent, child);

    // Get the checked methods and copy them to the child.
    if (oai.isAccepted())
      for (OverridesAndImplementationsDialog.CheckableItem m :
          oai.getCheckableItems()) {
        if (m.isSelected()) {
          child.addMethod(m.getMethod().createCopy(child));
          thereAbstractMethod |= m.getMethod().isAbstract();
        } else {
          child.removeMethod(m.getMethod());
        }
      }
    if (thereAbstractMethod && !child.isAbstract())
      showDeAbstractMessage();
    child.notifyObservers();
  }

  private void showDeAbstractMessage() {
    SMessageDialog.showInformationMessage(
        "Child class is not abstract.\n" +
        "Abstract methods have been de-abstracted.");
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
    setChild((SimpleEntity) entity);
  }

  @Override
  public void setTarget(Entity entity) {
    setParent((SimpleEntity) entity);
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

    child.setTextContent(String.valueOf(this.child.getId()));
    parent.setTextContent(String.valueOf(this.parent.getId()));
    inheritance.appendChild(child);
    inheritance.appendChild(parent);

    return inheritance;
  }

}
