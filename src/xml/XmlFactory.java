package xml;

/**
 * Factory for creating xml views of models.
 * Factories extended are automatically loaded by XmlWriter.
 * No need to manually add new factory.
 * @author David Miserez
 */
public abstract class XmlFactory {
    
    public String createXml(Object model) {
        if (!isModelCompatible(model))
            throw new IllegalArgumentException(
                    "The model passed by argument is " +
                    "incompatible with this factory");
        return _createXml(model);
    }
    public abstract Class<?> getCreatedClass();
    protected abstract String _createXml(Object model);
    
    private boolean isModelCompatible(Object model) {
        return getCreatedClass().equals(model.getClass());
    }
}
