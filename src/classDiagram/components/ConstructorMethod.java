
package classDiagram.components;

import javax.swing.ImageIcon;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import swing.Slyum;
import utility.PersonalizedIcon;

/**
 * Represents an UML method's constructor.
 * @author David Miserez
 */
public class ConstructorMethod extends Method {

  public ConstructorMethod(String name, Visibility visibility, SimpleEntity entity) {
    super(name, new Type("no_type"), visibility, entity);
    getReturnType().setVisible(false);
  }

  public ConstructorMethod(ConstructorMethod method, SimpleEntity newEntity) {
    super(method, newEntity);
  }

  public ConstructorMethod(ConstructorMethod method) {
    super(method);
  }

  @Override
  public ConstructorMethod createCopy(SimpleEntity newEntity) {
    return new ConstructorMethod(this, newEntity);
  }
  
  @Override
  public boolean isStatic() {
    return false;
  }

  @Override
  public boolean isAbstract() {
    return false;
  }

  @Override
  public boolean setReturnType(Type returnType) {
    if (getReturnType() == null)
      return super.setReturnType(returnType);
    return false;
  }

  @Override
  public String appendReturnType() {
    return "";
  }

  @Override
  public String getXmlTagName() {
    return "method";
  }

  @Override
  public Element getXmlElement(Document doc) {
    Element el = super.getXmlElement(doc);
    el.setAttribute("is-constructor", "true");
    return el;
  }
  
  @Override
  public ImageIcon getImageIcon() {
    return PersonalizedIcon.createImageIcon(
        Slyum.ICON_PATH + "constructor.png");
  }
}
