package xml.factories;

import classDiagram.ClassDiagram;
import xml.XmlFactory;

public class XmlClassFactory extends XmlFactory {

    @Override
    public Class<?> getCreatedClass() {
        return ClassDiagram.class;
    }

    @Override
    protected String _createXml(Object model) {
        ClassDiagram o = (ClassDiagram)model;
        return o.getName();
    }
}
