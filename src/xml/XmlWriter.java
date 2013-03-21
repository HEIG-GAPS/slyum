package xml;

import java.util.LinkedList;
import java.util.Set;

import org.reflections.Reflections;

import classDiagram.ClassDiagram;
import classDiagram.components.ClassEntity;

public class XmlWriter {
    
    private static XmlWriter instance;
    
    public static XmlWriter getInstance() {
        if (instance == null)
            instance = new XmlWriter();
        return instance;
    }
    
    private LinkedList<XmlFactory> xmlFactories = new LinkedList<>();
    
    public boolean addXmlFactory(XmlFactory factory) {
        return xmlFactories.add(factory);
    }
    
    public String getXml() {
        System.out.println(xmlFactories.size());
        String xml = "";
        for (XmlFactory factory : xmlFactories) {
            xml += factory.createXml(new ClassDiagram("Muck"));
        }
        return xml;
    }
    
    public static void main(String... args) {
        Reflections reflections = new Reflections("classDiagram");

        Set<Class<? extends ClassEntity>> allClasses = 
            reflections.getSubTypesOf(ClassEntity.class);
        
        System.out.println(allClasses.size());
        for (Class<?> c : allClasses)
            System.out.println(c);
    }

}
