package swing;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public interface XmlElement {
  public Element getXmlElement(Document doc);

  public String getXmlTagName();

}
