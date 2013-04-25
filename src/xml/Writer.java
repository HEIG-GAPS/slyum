package xml;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;

import swing.PanelClassDiagram;

@SuppressWarnings("rawtypes")
public abstract class Writer<T extends Factory, O> {
    
    protected LinkedList<T> factories = new LinkedList<>();
    
    public Writer(String factoriesPackage) {
        loadFactories(factoriesPackage);
    }

    @SuppressWarnings("unchecked")
    private void loadFactories(String factoriesPackage) {
        Reflections reflections = new Reflections(factoriesPackage);
        Set<Class<? extends Factory>> allClasses = 
            reflections.getSubTypesOf(Factory.class);
        for (Class<?> c : allClasses)
            try {
                Object o = Class.forName(c.getName()).newInstance();
                assert o instanceof Factory;
                addFactory((T)(o));
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
    }
    
    
    private boolean addFactory(T factory) {
        return factories.add(factory);
    }
    
    /**
     * Return an object built with all object associated with a factory.
     * @return a string built with all object associated with a factory.
     */
    public abstract O generate();
}
