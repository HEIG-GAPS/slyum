package ui;

import java.util.Properties;

/**
 * JavaFX migration stub for PropertyLoader.
 * Delegates to {@link swing.PropertyLoader} during the migration period
 * until the full swing package is replaced.
 *
 * @author migration
 */
public class PropertyLoader {

  public static PropertyLoader getInstance() {
    return InstanceHolder.INSTANCE;
  }

  private static final class InstanceHolder {
    static final PropertyLoader INSTANCE = new PropertyLoader();
  }

  private PropertyLoader() {}

  public Properties getProperties() {
    return swing.PropertyLoader.getInstance().getProperties();
  }

  public void push() {
    swing.PropertyLoader.getInstance().push();
  }

}
