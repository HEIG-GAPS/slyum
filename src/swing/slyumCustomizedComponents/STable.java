package swing.slyumCustomizedComponents;

import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import swing.Slyum;
import utility.EditableCellFocusAction;

public class STable extends JTable {
  
  public class CustomCellEditor extends DefaultCellEditor {
  
  // Hack to avoid unselection of editor when changing focus.
  private boolean isAlreadyStopedEditing = false;
  private FocusAdapter fa;

    public CustomCellEditor() {
      super(new PopupTextField());
      fa = new FocusAdapter() {
        @Override
        public void focusLost(FocusEvent e) {
          if (!isAlreadyStopedEditing && STable.this.getCellEditor() != null)
            STable.this.getCellEditor().stopCellEditing();
          isAlreadyStopedEditing = false;
        }
      };
    }

    @Override
    public boolean stopCellEditing() {
      ((JComponent)getComponent()).removeFocusListener(fa);
      isAlreadyStopedEditing = true;
      return super.stopCellEditing();
    }

    @Override
    public void cancelCellEditing() {
      ((JComponent)getComponent()).removeFocusListener(fa);
      super.cancelCellEditing();
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, 
        boolean isSelected, int row, int column) {        
      JComponent component = (JComponent)getComponent();
      component.setBorder(BorderFactory.createLineBorder(Slyum.THEME_COLOR, 1));
      component.addFocusListener(fa);
      return super.getTableCellEditorComponent(
          table, value, isSelected, row, column);
    }    
  }

  private int lastSelectedRow = -1;

  public STable(TableModel dm) {
    super(dm);
    
    new EditableCellFocusAction(this, KeyStroke.getKeyStroke("TAB"));
    setDefaultEditor(String.class, new CustomCellEditor());
  }

  @Override
  public void changeSelection(int rowIndex, int columnIndex, boolean toggle,
          boolean extend) {
    super.changeSelection(rowIndex, columnIndex, toggle, extend);
    if (editCellAt(rowIndex, columnIndex)) {
      final Component editor = getEditorComponent();
      editor.requestFocusInWindow();
      if (editor instanceof JTextComponent) {
        JTextComponent textEditor = (JTextComponent)editor;
        textEditor.selectAll();
      }
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
