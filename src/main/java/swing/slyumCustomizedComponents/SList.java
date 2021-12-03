package swing.slyumCustomizedComponents;

import swing.Slyum;

import javax.swing.*;
import java.util.Vector;

/**
 * @param <E> TODO
 *
 * @author David Miserez
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
