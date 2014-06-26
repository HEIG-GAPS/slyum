
package swing.slyumCustomizedComponents;

import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.ListModel;
import swing.Slyum;

/**
 *
 * @author David Miserez <david.miserez@heig-vd.ch>
 * @param <E> TODO
 */
public class SList<E> extends JList<E> {

  private SScrollPane scrollPane;
  
  public SList(ListModel<E> dataModel) {
    super(dataModel);
    initialize();
  }

  public SList(E[] listData) {
    super(listData);
    initialize();
  }

  public SList(Vector<? extends E> listData) {
    super(listData);
    initialize();
  }

  public SList() {
    initialize();
  }
  
  private void initialize() {
    setBorder(null);
    scrollPane = new SScrollPane(this);
    scrollPane.setBorder(
        BorderFactory.createLineBorder(Slyum.DEFAULT_BORDER_COLOR));
  }

  public SScrollPane getScrollPane() {
    return scrollPane;
  }  
}
