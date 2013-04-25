package xml.factories;

import org.w3c.dom.Element;

import xml.Factory;
import xml.XmlWriter;
import classDiagram.components.Entity;

public class XmlEntityFactory extends Factory<Element> {

    @Override
    public Class<?> getCreatedClass() {
        return Entity.class;
    }

    @Override
    protected Element _create(Object model) {
        Entity e = (Entity)model;
        Element element = XmlWriter.document().createElement("entity");
        element.setAttribute("id", String.valueOf(e.getId()));
        element.setAttribute("name", e.getName());
        element.setAttribute("visibility", e.getVisibility().toString());
        element.setAttribute("entityType", e.getEntityType());
        element.setAttribute("isAbstract", String.valueOf(e.isAbstract()));
        return element;
        
        /*
        if (attributes.size() == 0 && methods.size() == 0 && getLastBalise(depth).isEmpty())
            return xml + "/>";

        xml += ">\n";

        for (final Attribute attribute : attributes)
            xml += attribute.toXML(depth + 1) + "\n";

        for (final Method operation : methods)
            xml += operation.toXML(depth + 1) + "\n";

        xml += getLastBalise(depth + 1);

        return xml + tab + "</entity>";*/
        
    }
}
