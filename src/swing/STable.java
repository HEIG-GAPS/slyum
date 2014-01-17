package swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.border.LineBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableModel;
import javax.swing.text.JTextComponent;
import utility.EditableCellFocusAction;

public class STable extends JTable {

  private int lastSelectedRow = -1;
  
  // Hack to avoid unselection of editor when changing focus.
  private boolean isAlreadyStopedEditin = false;

  public STable(TableModel dm) {
    super(dm);
    final FocusAdapter fa = new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        if (!isAlreadyStopedEditin)
          STable.this.getCellEditor().stopCellEditing();
        isAlreadyStopedEditin = false;
      }
    };
    
    new EditableCellFocusAction(this, KeyStroke.getKeyStroke("TAB"));
    setDefaultEditor(String.class, new DefaultCellEditor(new JTextField()){

      @Override
      public boolean stopCellEditing() {
        ((JComponent)getComponent()).removeFocusListener(fa);
        isAlreadyStopedEditin = true;
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
        component.setBorder(new LineBorder(Color.black));
        component.addFocusListener(fa);
        return super.getTableCellEditorComponent(
            table, value, isSelected, row, column);
      }
    });
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

  private class DefaultCellEditorImpl extends DefaultCellEditor {

    public DefaultCellEditorImpl(JCheckBox checkBox) {
      super(checkBox);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      return super.getTableCellEditorComponent(table, value, isSelected, row, column); //To change body of generated methods, choose Tools | Templates.
    }
  }
}
