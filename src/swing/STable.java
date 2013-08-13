package swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;

import utility.EditableCellFocusAction;

public class STable extends JTable {

  private int lastSelectedRow = -1;

  public STable(TableModel dm) {
    super(dm);
    new EditableCellFocusAction(this, KeyStroke.getKeyStroke("TAB"));
  }

  @Override
  public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
          boolean extend) {
    super.changeSelection(rowIndex, columnIndex, toggle, extend);

    if (editCellAt(rowIndex, columnIndex)) {
      Component editor = getEditorComponent();
      editor.requestFocusInWindow();
      if (editor instanceof JTextComponent)
        ((JTextComponent) editor).selectAll();
    }
  }

  public void selectRow(Object value) {
    for (int i = 0; i < getModel().getRowCount(); i++)
      if (getModel().getValueAt(i, 0) == value) addRowSelectionInterval(i, i);
  }

  public Object getSelectedRowValue() {
    return getModel().getValueAt(lastSelectedRow, 0);
  }

  @Override
  public void addRowSelectionInterval(int index0, int index1) {
    super.addRowSelectionInterval(index0, index1);
    lastSelectedRow = index0;
  }

  public void scrollToCell(int row, int column) {
    scrollRectToVisible(getCellRect(row, column, true));
  }

  public void scrollToLastCell() {
    scrollToCell(getRowCount() - 1, getColumnCount() - 1);
  }
}
