package swing.hierarchicalView;

import javax.swing.*;

/**
 * This class is implemented by node in JTree that have a customized icon.
 *
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public interface ICustomizedIconNode {
  /**
   * Return the icon representing the node. Use by the JTree to know wich icon attributae to a node.
   *
   * @return the icon node
   */
  public ImageIcon getCustomizedIcon();

}
