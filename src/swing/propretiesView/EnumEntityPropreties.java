package swing.propretiesView;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.EnumEntity;
import classDiagram.components.EnumValue;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Observable;
import java.util.Observer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;
import swing.Slyum;
import swing.slyumCustomizedComponents.FlatPanel;
import swing.slyumCustomizedComponents.SButton;
import swing.slyumCustomizedComponents.STable;
import swing.slyumCustomizedComponents.TextFieldWithPrompt;
import utility.PersonalizedIcon;

public class EnumEntityPropreties extends GlobalPropreties {

  // Singleton
  private static EnumEntityPropreties instance;

  /**
   * Get the unique instance of this class.
   * 
   * @return the unique instance of SimpleEntityPropreties
   */
  public static EnumEntityPropreties getInstance() {
    if (instance == null) instance = new EnumEntityPropreties();

    return instance;
  } // --------

  private JTextField txtFieldName;
  private STable tableEnumValues;
  private JButton btnUp, btnDown, btnDelete;
  Observer rowObserver = new Observer() {

    @Override
    public void update(Observable o, Object object) {
      if (object instanceof UpdateMessage)
        switch ((UpdateMessage) object) {
          case SELECT:
            int rowCount = tableEnumValues.getRowCount();
            int selectedRow = tableEnumValues.getSelectedRow();
            tableEnumValues.selectRow(o);
            btnDelete.setEnabled(true);
            btnUp.setEnabled(selectedRow > 0);
            btnDown.setEnabled(selectedRow < rowCount - 1);
            tableEnumValues.scrollToCell(selectedRow, 0);
            break;
          case UNSELECT:
            btnDelete.setEnabled(false);
            break;
          default:
            break;
        }
      else
        ((AbstractTableModel) tableEnumValues.getModel())
                .fireTableDataChanged();
    }
  };

  public EnumEntityPropreties() {
    initializeComponents();
  }

  @Override
  public void updateComponentInformations(UpdateMessage msg) {
    if (currentObject == null) return;

    if (tableEnumValues.getCellEditor() != null)
      tableEnumValues.getCellEditor().stopCellEditing();
    
    EnumEntity enumEntity = (EnumEntity) currentObject;
    
    if (!txtFieldName.getText().equals(enumEntity.getName()))
      txtFieldName.setText(enumEntity.getName());
    enumEntity.addObserver(this);

    // Mise à jour des champs de la table
    AbstractTableModel model = (AbstractTableModel) tableEnumValues.getModel();
    model.fireTableStructureChanged();

    for (int i = 0; i < model.getRowCount(); i++)
      ((EnumValue) model.getValueAt(i, 0)).addObserver(rowObserver);

    // Désactivation des composants.
    btnDelete.setEnabled(false);
    btnDown.setEnabled(false);
    btnUp.setEnabled(false);
  }

  private void initializeComponents() {
    JPanel panelAttributes = new JPanel(), panelButtons = new JPanel(), panelMain = new FlatPanel();
    JButton btnAdd;

    panelAttributes.setLayout(new BoxLayout(panelAttributes,
            BoxLayout.PAGE_AXIS));
    panelAttributes.setMaximumSize(new Dimension(200, Short.MAX_VALUE));

    // Enum name
    txtFieldName = new TextFieldWithPrompt("", "Enter the enum's name");
    txtFieldName.setMaximumSize(new Dimension(Short.MAX_VALUE, 20));
    txtFieldName.addKeyListener(new KeyAdapter() {

      @Override
      public void keyReleased(KeyEvent evt) {
        updateEnumName(txtFieldName.getText());
      }
    });
    txtFieldName.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        txtFieldName.setSelectionStart(0);
      }
    });
    panelAttributes.add(txtFieldName);
    // -----

    // Enum values
    tableEnumValues = new STable(new AbstractTableModel() {

      @Override
      public Object getValueAt(int row, int col) {
        if (currentObject == null) return null;

        return ((EnumEntity) currentObject).getEnumValues().get(row);
      }

      @Override
      public int getRowCount() {
        if (currentObject == null) return 0;

        EnumEntity enumEntity = (EnumEntity) currentObject;
        return enumEntity.getEnumValues().size();
      }

      @Override
      public int getColumnCount() {
        return 1;
      }

      @Override
      public Class<?> getColumnClass(int c) {
        return getValueAt(0, c).getClass();
      }

      @Override
      public boolean isCellEditable(int row, int col) {
        return true;
      }

      @Override
      public void setValueAt(Object value, int row, int col) {
        if (currentObject == null) return;

        EnumEntity enumEntity = (EnumEntity) currentObject;
        EnumValue enumValue = enumEntity.getEnumValues().get(row);
        enumValue.setValue((String)value);
        enumValue.notifyObservers();
      }
    }) {
      @Override
      public void changeSelection(int rowIndex, int columnIndex,
              boolean toggle, boolean extend) {
        super.changeSelection(rowIndex, columnIndex, toggle, extend);

        EnumValue currentEnumValue = (EnumValue) getModel().getValueAt(
                rowIndex, columnIndex);
        currentEnumValue.select();
        currentEnumValue.notifyObservers(UpdateMessage.SELECT);
      }
    };
    tableEnumValues.setDefaultEditor(EnumValue.class, tableEnumValues.new CustomCellEditor());
    tableEnumValues.setTableHeader(null);
    tableEnumValues.setFillsViewportHeight(true);
    tableEnumValues.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tableEnumValues.setPreferredScrollableViewportSize(new Dimension(0, 0));

    panelAttributes.add(Box.createVerticalStrut(5));
    panelAttributes.add(tableEnumValues.getScrollPane());
    // -----

    // Buttons
    panelButtons.setBackground(null);
    panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.PAGE_AXIS));
    panelButtons.setMaximumSize(new Dimension(30, Short.MAX_VALUE));

    panelButtons.add(btnAdd = new SButton(PersonalizedIcon
            .createImageIcon(Slyum.ICON_PATH + "plus.png"), "Add"));
    btnAdd.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        if (currentObject == null) return;

        ((EnumEntity) currentObject).createEnumValue();
      }
    });

    panelButtons.add(btnUp = new SButton(PersonalizedIcon
            .createImageIcon(Slyum.ICON_PATH + "arrow-up-24.png"), "Up"));
    btnUp.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        if (currentObject == null) return;

        EnumEntity enumEntity = (EnumEntity) currentObject;
        EnumValue enumValue = (EnumValue) tableEnumValues.getSelectedRowValue();
        enumEntity.moveEnumPosition(enumValue, -1);
        enumEntity.notifyObservers();
        enumValue.select();
        enumValue.notifyObservers(UpdateMessage.SELECT);
      }
    });

    panelButtons.add(btnDown = new SButton(PersonalizedIcon
            .createImageIcon(Slyum.ICON_PATH + "arrow-down-24.png"), "Down"));
    btnDown.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        if (currentObject == null) return;

        EnumEntity enumEntity = (EnumEntity) currentObject;
        EnumValue enumValue = (EnumValue) tableEnumValues.getSelectedRowValue();
        enumEntity.moveEnumPosition(
                (EnumValue) tableEnumValues.getSelectedRowValue(), 1);
        enumEntity.notifyObservers();
        enumValue.select();
        enumValue.notifyObservers(UpdateMessage.SELECT);
      }
    });

    panelButtons.add(btnDelete = new SButton(PersonalizedIcon
            .createImageIcon(Slyum.ICON_PATH + "minus.png"), "Delete"));
    btnDelete.setEnabled(false);
    btnDelete.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        if (currentObject == null) return;

        EnumEntity enumEntity = (EnumEntity) currentObject;
        int selectedRow = tableEnumValues.getSelectedRow(), rowCount, rowToSelect;

        enumEntity.removeEnumValue((EnumValue) tableEnumValues.getModel()
                .getValueAt(selectedRow, 0));
        enumEntity.notifyObservers();

        // Recherche de l'enum devant être sélectionné après la suppression.
        rowCount = tableEnumValues.getRowCount();
        rowToSelect = selectedRow >= rowCount ? rowCount - 1 : selectedRow;

        if (rowCount > 0) {
          EnumValue enumValueToSelect = (EnumValue) tableEnumValues.getModel()
                  .getValueAt(rowToSelect, 0);
          enumValueToSelect.select();
          enumValueToSelect.notifyObservers(UpdateMessage.SELECT);
        }
      }
    });
    // -----

    panelMain.setLayout(new BoxLayout(panelMain, BoxLayout.LINE_AXIS));
    panelMain.setMaximumSize(new Dimension(250, Short.MAX_VALUE));
    panelMain.add(panelAttributes);
    panelMain.add(Box.createHorizontalStrut(5));
    panelMain.add(panelButtons);
    add(panelMain);
  }

  private void updateEnumName(String name) {
    if (currentObject == null) return;

    EnumEntity enumEntity = (EnumEntity) currentObject;
    enumEntity.setName(name);
    enumEntity.notifyObservers();
  }

  @Override
  public void update(Observable o, Object object) {
    super.update(o, object);
    if (object instanceof UpdateMessage) {
      switch ((UpdateMessage) object) {
        case ADD_ENUM_NO_EDIT:
        case ADD_ENUM:
          tableEnumValues.scrollToLastCell();
          break;
        default:
          break;
      }
    }
  }
}
