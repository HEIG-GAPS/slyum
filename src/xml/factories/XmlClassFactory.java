package xml.factories;

import xml.XmlFactory;
import classDiagram.ClassDiagram;

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
