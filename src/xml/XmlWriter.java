package xml;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import swing.PanelClassDiagram;

public class XmlWriter {
    
    /* Champs static */
    private static XmlWriter instance;
    
    public static XmlWriter getInstance() {
        if (instance == null)
            instance = new XmlWriter();
        return instance;
    }
    /* ----------------------------------------------------------- */
    private LinkedList<XmlFactory> xmlFactories = new LinkedList<>();
    
    public XmlWriter() {
        loadXmlFactories();
    }
    
    private void loadXmlFactories() {
        Reflections reflections = new Reflections("xml.factories");
        Set<Class<? extends XmlFactory>> allClasses = 
            reflections.getSubTypesOf(XmlFactory.class);
        for (Class<?> c : allClasses)
            try {
                Object o = Class.forName(c.getName()).newInstance();
                assert o instanceof XmlFactory;
                addXmlFactory((XmlFactory)(o));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
    }
    
    
    public boolean addXmlFactory(XmlFactory factory) {
        return xmlFactories.add(factory);
    }
    
    public String getXml() { 
        String xml = "";
        for (XmlFactory factory : xmlFactories) {
            List<?> objects = PanelClassDiagram.getInstance().getClassDiagram().
                                getComponentsByType(factory.getCreatedClass());
            for (Object o : objects)
                xml += factory.createXml(o);
        }
        return xml;
    }
    
    public static void main(String... args) {
    }

}
