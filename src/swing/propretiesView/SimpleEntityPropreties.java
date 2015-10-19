package swing.propretiesView;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Attribute;
import classDiagram.components.ClassEntity;
import classDiagram.components.ConstructorMethod;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;
import classDiagram.components.PrimitiveType;
import classDiagram.components.SimpleEntity;
import classDiagram.components.Type;
import classDiagram.components.Variable;
import classDiagram.components.Visibility;
import classDiagram.verifyName.TypeName;
import graphic.entity.ClassView;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import swing.MultiViewManager;
import swing.slyumCustomizedComponents.FlatPanel;
import swing.slyumCustomizedComponents.SButton;
import swing.slyumCustomizedComponents.STable;
import swing.Slyum;
import swing.slyumCustomizedComponents.SCheckBox;
import swing.slyumCustomizedComponents.TextFieldWithPrompt;
import utility.MultiBorderLayout;
import utility.PersonalizedIcon;
import utility.Utility;

/**
 * Show the propreties of an UML SimpleEntity with Swing components. All inner
 * classes are used for create customized JTable.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class SimpleEntityPropreties extends GlobalPropreties {

  private class AttributeTableModel extends AbstractTableModel implements Observer, TableModelListener, MouseListener {
    private final String[] columnNames = { "Attribute", "Type", "Visibility",
            "Constant", "Static" };

    private final LinkedList<Object[]> data = new LinkedList<>();

    private final HashMap<Attribute, Integer> mapIndex = new HashMap<>();

    public void addAttribute(Attribute attribute) {
      data.add(new Object[] { attribute.getName(),
              attribute.getType().getName(),
              attribute.getVisibility().getName(), attribute.isConstant(),
              attribute.isStatic() });

      attribute.addObserver(this);
      mapIndex.put(attribute, data.size() - 1);

      fireTableRowsInserted(0, data.size());
    }

    public void clearAll() {
      data.clear();
      mapIndex.clear();
      fireTableDataChanged();
    }

    @Override
    public Class<? extends Object> getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
      return columnNames[col];
    }

    @SuppressWarnings("unchecked")
    public HashMap<Attribute, Integer> getMapIndex() {
      return (HashMap<Attribute, Integer>) mapIndex.clone();
    }

    @Override
    public int getRowCount() {
      return data.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
      return data.get(row)[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      return !(currentObject.getClass() == InterfaceEntity.class && col == 4);
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
      if (currentObject == null || !(currentObject instanceof SimpleEntity))
        return;

      // Get the selected attribute
      final int index = attributesTable.getSelectionModel()
              .getLeadSelectionIndex();
      final Attribute attribute = Utility.getKeysByValue(mapIndex, index)
              .iterator().next();

      // Unselect all attributes
      for (final Attribute a : ((SimpleEntity) currentObject).getAttributes()) {
        if (a.equals(attribute)) continue;

        a.select();
        a.notifyObservers(UpdateMessage.UNSELECT);
      }

      // Select the selected attribute
      attribute.select();
      attribute.notifyObservers(UpdateMessage.SELECT);
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    public void setAttribute(Attribute attribute, int index) {
      data.set(index, new Object[] { attribute.getName(),
              attribute.getType().getName(),
              attribute.getVisibility().getName(), attribute.isConstant(),
              attribute.isStatic() });

      fireTableRowsUpdated(index, index);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
      try {
        data.get(row)[col] = value;
        fireTableCellUpdated(row, col);
      } catch (Exception e) {

      }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
      final int row = e.getFirstRow();
      final int column = e.getColumn();

      if (column == -1) return;

      final TableModel model = (TableModel) e.getSource();
      final Object data = model.getValueAt(row, column);
      final Attribute attribute = Utility.getKeysByValue(mapIndex, row)
              .iterator().next();

      switch (column) {
        case 0: // nom

          if (attribute.setName((String) data))
            setValueAt(attribute.getName(), row, column);

          break;

        case 1: // type
          String s = (String) data;

          if (!TypeName.getInstance().verifyName(s))
            setValueAt(attribute.getType().getName(), row, column);
          else
            attribute.setType(new Type(s));

          break;

        case 2: // visibility
          attribute.setVisibility(Visibility.valueOf(((String) data)
                   .toUpperCase()));
          break;

        case 3: // constant
          attribute.setConstant((Boolean) data);
          break;

        case 4: // static
          attribute.setStatic((Boolean) data);
          break;
      }

      attribute.notifyObservers();
      attribute.getType().notifyObservers();

      attributesTable.addRowSelectionInterval(row, row);
    }

    @Override
    public void update(Observable observable, Object o) {
      final Attribute attribute = (Attribute) observable;
      try {
        final int index = mapIndex.get(attribute);

        if (index == -1) return;

        if (o != null && o instanceof UpdateMessage)
          switch ((UpdateMessage) o) {
            case SELECT:
              btnRemoveAttribute.setEnabled(true);
              btnUpAttribute.setEnabled(index > 0);
              btnDownAttribute.setEnabled(index < mapIndex.size() - 1);
              showInProperties();
              attributesTable.addRowSelectionInterval(index, index);
              attributesTable.scrollRectToVisible(attributesTable.getCellRect(
                      attributesTable.getSelectedRow(),
                      attributesTable.getSelectedColumn(), true));
              break;
            case UNSELECT:
              attributesTable.removeRowSelectionInterval(index, index);
              break;
            default:
              break;
          }

        setAttribute(attribute, index);
      } catch (final Exception e) {

      }
    }
  }

  private class MethodTableModel 
      extends AbstractTableModel 
      implements Observer, TableModelListener, MouseListener {

    private final String[] columnNames = { "Method", "Type", "Visibility",
            "Abstract", "Static" };

    private final LinkedList<Object[]> data = new LinkedList<>();

    private final HashMap<Method, Integer> mapIndex = new HashMap<>();

    public void addMethod(Method method) {
      data.add(new Object[] { method.getName(),
              method.getReturnType().getName(),
              method.getVisibility().getName(), method.isAbstract(),
              method.isStatic() });

      method.addObserver(this);
      method.addObserver((ParametersTableModel) parametersTable.getModel());
      mapIndex.put(method, data.size() - 1);

      fireTableRowsInserted(0, data.size());
    }

    public void clearAll() {
      data.clear();
      mapIndex.clear();
      fireTableDataChanged();
    }

    @Override
    public Class<? extends Object> getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
      return columnNames[col];
    }

    @SuppressWarnings({ "unchecked" })
    public HashMap<Method, Integer> getMapIndex() {
      return (HashMap<Method, Integer>) mapIndex.clone();
    }

    @Override
    public int getRowCount() {
      return data.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
      return data.get(row)[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      Boolean isInterfaceEntityClass = currentObject.getClass()
          .equals(InterfaceEntity.class);
      Boolean isConstructorClass = Utility.getKeysByValue(mapIndex, row)
                                          .iterator().next().getClass()
                                          .equals(ConstructorMethod.class);
      
      Boolean retourn = !(((isInterfaceEntityClass || isConstructorClass) && col == 3) ||
               isConstructorClass && col == 4 ||  
               isConstructorClass && col == 1);
      return retourn;
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
      if (currentObject == null || !(currentObject instanceof SimpleEntity))
        return;

      // Get the selected method
      final int index = methodsTable.getSelectionModel()
              .getLeadSelectionIndex();
      final Method method = Utility.getKeysByValue(mapIndex, index).iterator()
              .next();

      // Unselect all methods
      for (final Method m : ((SimpleEntity) currentObject).getMethods()) {
        if (m.equals(method)) continue;

        m.select();
        m.notifyObservers(UpdateMessage.UNSELECT);
      }

      // Select the selected method
      method.select();
      method.notifyObservers(UpdateMessage.SELECT);
    }

    @Override
    public void mouseReleased(MouseEvent e) {}

    public void setMethod(Method method, int index) {
      data.set(index,
              new Object[] { method.getName(),
                      method.getReturnType().getName(),
                      method.getVisibility().getName(), method.isAbstract(),
                      method.isStatic() });

      fireTableRowsUpdated(index, index);
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
      try {
        data.get(row)[col] = value;
        fireTableCellUpdated(row, col);
      } catch (Exception e) {

      }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
      final int row = e.getFirstRow();
      final int column = e.getColumn();

      if (column == -1) return;

      final TableModel model = (TableModel) e.getSource();
      final Object data = model.getValueAt(row, column);
      final Method method = Utility.getKeysByValue(mapIndex, row).iterator()
              .next();

      switch (column) {
        case 0: // nom
          if (method.setName((String) data))
            setValueAt(method.getName(), row, column);

          break;

        case 1: // type
          String s = (String) data;
          if (TypeName.getInstance().verifyName(s))
            method.setReturnType(new Type(s));
          else
            setValueAt(method.getReturnType().getName(), row, column);

          break;

        case 2: // visibility
          method.setVisibility(Visibility.valueOf(((String) data).toUpperCase()));
          break;

        case 3: // abstract
          method.setAbstract((Boolean) data);
          break;

        case 4: // static
          method.setStatic((Boolean) data);
          break;
      }

      method.notifyObservers(UpdateMessage.SELECT);
      method.getReturnType().notifyObservers();

      methodsTable.addRowSelectionInterval(row, row);
    }

    @Override
    public void update(Observable observable, Object o) {
      try {
        final int index = mapIndex.get(observable);

        if (index == -1) return;

        if (o != null && o instanceof UpdateMessage)
          switch ((UpdateMessage) o) {
            case SELECT:
              btnRemoveMethod.setEnabled(true);
              btnUpMethod.setEnabled(index > 0);
              btnDownMethod.setEnabled(index < mapIndex.size() - 1);
              showInProperties();
              methodsTable.addRowSelectionInterval(index, index);
              methodsTable.scrollRectToVisible(methodsTable.getCellRect(
                      methodsTable.getSelectedRow(),
                      methodsTable.getSelectedColumn(), true));
              break;
            case UNSELECT:
              methodsTable.removeRowSelectionInterval(index, index);
              break;
            default:
              break;
          }

        setMethod((Method) observable, index);
      } catch (final Exception e) {

      }
    }

  }

  private class ParametersTableModel 
      extends AbstractTableModel 
      implements Observer, TableModelListener, ActionListener, MouseListener {
    private static final long serialVersionUID = 8577198492892934888L;

    private final String[] columnNames = { "Parameter", "Type" };

    private Method currentMethod;

    private Variable currentParameter;

    private final LinkedList<Object[]> data = new LinkedList<>();

    @Override
    public void actionPerformed(ActionEvent e) {
      addParameters(currentMethod);
    }

    public void clearAll() {
      if (currentMethod != null)
        for (Variable v : currentMethod.getParameters())
          v.deleteObserver(this);

      data.clear();
      fireTableDataChanged();
    }

    @Override
    public Class<? extends Object> getColumnClass(int c) {
      return getValueAt(0, c).getClass();
    }

    @Override
    public int getColumnCount() {
      return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
      return columnNames[col];
    }

    public Variable getCurrentParameter() {
      return currentParameter;
    }
    
    public Method getCurrentMethod() {
      return currentMethod;
    }

    @Override
    public int getRowCount() {
      return data.size();
    }

    @Override
    public Object getValueAt(int row, int col) {
      return data.get(row)[col];
    }

    @Override
    public boolean isCellEditable(int row, int col) {
      return true;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {}

    @Override
    public void mouseEntered(MouseEvent arg0) {}

    @Override
    public void mouseExited(MouseEvent arg0) {}

    @Override
    public void mousePressed(MouseEvent arg0) {
      if (currentMethod == null) return;

      // Get the selected parameter
      final int index = parametersTable.getSelectionModel()
              .getLeadSelectionIndex();
      final Variable variable = currentMethod.getParameters().get(index);

      setCurrentParameter(variable);
    }

    @Override
    public void mouseReleased(MouseEvent arg0) {}

    private void parameterSelected() {
      btnRemoveParameters.setEnabled(currentParameter != null);
      btnLeftParameters.setEnabled(currentMethod.getParameters().indexOf(
              currentParameter) > 0);
      btnRightParameters.setEnabled(currentMethod.getParameters().indexOf(
              currentParameter) < currentMethod.getParameters().size() - 1);
    }

    public void removeCurrentParameter() {
      final Variable parameter = getCurrentParameter();
      int index = currentMethod.getParameters().indexOf(parameter);

      if (parameter == null) return;

      currentMethod.removeParameters(parameter);
      parameter.notifyObservers();
      currentMethod.select();
      currentMethod.notifyObservers();
      currentMethod.notifyObservers(UpdateMessage.SELECT);

      {
        currentMethod.select();
        currentMethod.notifyObservers(UpdateMessage.SELECT);

        final int size = currentMethod.getParameters().size();
        if (size == index) index--;

        if (size == 0) return;

        parametersTable.addRowSelectionInterval(index, index);
        ((ParametersTableModel) parametersTable.getModel())
                .setCurrentParameter(currentMethod.getParameters().get(index));
      }
    }

    public void setCurrentParameter(Variable parameter) {
      currentParameter = parameter;
      parameterSelected();
    }

    public void setParameter(Method method) {
      if (method == null) return;

      clearAll();
      for (final Variable v : method.getParameters()) {
        v.addObserver(this);
        data.add(new Object[] { v.getName(), v.getType().getName() });
      }

      fireTableRowsInserted(0, data.size());
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
      data.get(row)[col] = value;

      fireTableCellUpdated(row, col);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
      if (currentMethod == null) return;

      final int row = e.getFirstRow();
      final int column = e.getColumn();

      if (column == -1) return;

      final TableModel model = (TableModel) e.getSource();
      final Object data = model.getValueAt(row, column);

      switch (column) {
        case 0: // Parameter
          currentMethod.getParameters().get(row).setName((String) data);
          break;

        case 1: // type
          try {
            currentMethod.getParameters().get(row)
                    .setType(new Type((String) data));
          } catch (Exception ex) {}
          break;
      }

      currentMethod.getParameters().get(row).notifyObservers();
      currentMethod.notifyObservers();
      currentMethod.getParameters().get(row).getType().notifyObservers();
      currentMethod.select();
      currentMethod.notifyObservers(UpdateMessage.SELECT);

      final Variable parameter = currentMethod.getParameters().get(row);

      parametersTable.addRowSelectionInterval(row, row);
      ((ParametersTableModel) parametersTable.getModel())
              .setCurrentParameter(parameter);
    }

    @Override
    public void update(Observable observable, Object o) {
      if (o != null && o instanceof UpdateMessage) {
        switch ((UpdateMessage) o) {
          case SELECT:
            showInProperties();
            clearAll();
            currentMethod = (Method) observable;
            setParameter(currentMethod);
            panelParameters.setVisible(true);
            btnAddParameters.setEnabled(true);
            final boolean hasParameters = currentMethod.getParameters().size() > 0;
            scrollPaneParameters.setVisible(hasParameters);
            imgNoParameter.setVisible(!hasParameters);
            imgMethodSelected.setVisible(false);
            break;
          case UNSELECT:
            clearAll();
            btnAddParameters.setEnabled(false);
            scrollPaneParameters.setVisible(false);
            imgMethodSelected.setVisible(true);
            imgNoParameter.setVisible(false);
            btnRemoveParameters.setEnabled(false);
            btnLeftParameters.setEnabled(false);
            btnRightParameters.setEnabled(false);
            break;
          default:
            break;
        }
      } else
        setParameter(currentMethod);
    }
  }

  private static SimpleEntityPropreties instance = new SimpleEntityPropreties();

  /**
   * Get the unique instance of this class.
   * 
   * @return the unique instance of SimpleEntityPropreties
   */
  public static SimpleEntityPropreties getInstance() {
    return instance;
  }

  private STable attributesTable, methodsTable, parametersTable;
  private final JButton btnAddParameters, btnRemoveMethod, btnRemoveAttribute,
          btnUpAttribute, btnDownAttribute, btnUpMethod, btnDownMethod,
          btnRemoveParameters, btnRightParameters, btnLeftParameters,
          btnAddMethodForInterface;
  private JCheckBox checkBoxAbstract = new SCheckBox("Abstract");
  private JComboBox<String> comboBox = Utility.getVisibilityComboBox();
  private JLabel imgMethodSelected, imgNoParameter;
  private JPanel panelParameters, panelAddMethodForClass;
  private JScrollPane scrollPaneParameters;
  private JTextField textName = new TextFieldWithPrompt("", "Enter the entity's name");
  
  protected SimpleEntityPropreties() {
    
    // Buttons for attributes.
    btnUpAttribute = new SButton(
            PersonalizedIcon.createImageIcon(
                Slyum.ICON_PATH + "arrow-up-24.png"), "Up");
    btnDownAttribute = new SButton(
            PersonalizedIcon.createImageIcon(
                Slyum.ICON_PATH + "arrow-down-24.png"), "Down");
    btnRemoveAttribute = new SButton(
            PersonalizedIcon.createImageIcon(
                Slyum.ICON_PATH + "minus.png"), "Remove");
    
    // Buttons fo methods.
    btnUpMethod = new SButton(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "arrow-up-24.png"), "Up");
    btnDownMethod = new SButton(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "arrow-down-24.png"), "Down");
    btnRemoveMethod = new SButton(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "minus.png"), "Remove");
    
    // Buttons for parameters. 
    btnAddParameters = new SButton(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "plus.png"), "Add");
    btnLeftParameters = new SButton(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "arrow-left-24.png"), "Left");
    btnRightParameters = new SButton(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "arrow-right-24.png"), "Rigth");
    btnRemoveParameters = new SButton(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "minus.png"), "Remove");
    
    // Others components
    imgMethodSelected = new JLabel(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "select_method.png"));
    imgMethodSelected.setAlignmentX(CENTER_ALIGNMENT);
    
    imgNoParameter = new JLabel(
        PersonalizedIcon.createImageIcon(
            Slyum.ICON_PATH + "empty_parameter.png"));
    imgNoParameter.setAlignmentX(CENTER_ALIGNMENT);
    imgNoParameter.setVisible(false);

    // Tables
    attributesTable = new STable(new AttributeTableModel(), () -> addAttribute(false));
    attributesTable.setEmptyText("No attribute");
    attributesTable.setPreferredScrollableViewportSize(new Dimension(200, 0));

    attributesTable.getModel().addTableModelListener(
            (AttributeTableModel) attributesTable.getModel());

    attributesTable.addMouseListener((AttributeTableModel) attributesTable
            .getModel());

    methodsTable = new STable(new MethodTableModel(), () -> addMethod(false));
    methodsTable.setEmptyText("No method");
    methodsTable.setPreferredScrollableViewportSize(new Dimension(200, 0));
    methodsTable.getModel().addTableModelListener(
            (MethodTableModel) methodsTable.getModel());
    methodsTable.addMouseListener((MethodTableModel) methodsTable.getModel());

    parametersTable = new STable(
        new ParametersTableModel(), 
        () -> addParameters(((ParametersTableModel)parametersTable.getModel()).getCurrentMethod()));
    parametersTable.setPreferredScrollableViewportSize(new Dimension(70, 0));
    parametersTable.getModel().addTableModelListener(
        (ParametersTableModel) parametersTable.getModel());
    parametersTable.addMouseListener((ParametersTableModel) parametersTable
            .getModel());

    TableColumn visibilityColumn = attributesTable.getColumnModel()
            .getColumn(2);
    visibilityColumn.setCellEditor(new DefaultCellEditor(Utility
            .getVisibilityComboBox()));

    visibilityColumn = methodsTable.getColumnModel().getColumn(2);
    visibilityColumn.setCellEditor(new DefaultCellEditor(Utility
            .getVisibilityComboBox()));

    TableColumn column = null;
    for (int i = 0; i < attributesTable.getColumnCount(); i++) {
      column = attributesTable.getColumnModel().getColumn(i);

      if (i < 2)
        column.setPreferredWidth(70);
      else if (i == 2)
        column.setPreferredWidth(20);
      else
        column.setPreferredWidth(10);
    }
    for (int i = 0; i < methodsTable.getColumnCount(); i++) {
      column = methodsTable.getColumnModel().getColumn(i);

      if (i < 2)
        column.setPreferredWidth(70);
      else if (i == 2)
        column.setPreferredWidth(20);
      else
        column.setPreferredWidth(10);
    }

    JPanel p = new FlatPanel();
    p.setAlignmentY(TOP_ALIGNMENT);
    p.setMaximumSize(new Dimension(0, Integer.MAX_VALUE));
    {
      final GridBagLayout gbl_panel = new GridBagLayout();
      gbl_panel.columnWidths = new int[] { 0, 0 };
      gbl_panel.rowHeights = new int[] { 0, 0 };
      gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
      gbl_panel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
      p.setLayout(gbl_panel);
    }

    {
      final GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
      gbc_btnNewButton.anchor = GridBagConstraints.NORTH;
      gbc_btnNewButton.gridx = 0;
      gbc_btnNewButton.gridy = 0;
      p.add(createSimpleEntityPropreties(), gbc_btnNewButton);
    }

    add(p);

    p = new FlatPanel();
    p.setAlignmentY(TOP_ALIGNMENT);
    p.setLayout(new BorderLayout());
    JPanel panel = createWhitePanel();
    panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    panel.add(attributesTable.getScrollPane());

    JPanel panelButton = new JPanel();
    panelButton.setOpaque(false);
    panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.PAGE_AXIS));

    {
      final JButton button = new SButton(
              PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "plus.png"),
              "Add");
      button.setAlignmentX(CENTER_ALIGNMENT);
      button.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
          addAttribute(true);
        }
      });

      panelButton.add(button);
    }

    {
      btnUpAttribute.setAlignmentX(CENTER_ALIGNMENT);
      btnUpAttribute.setEnabled(false);
      btnUpAttribute.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
          // Get the selected attribute
          final int index = attributesTable.getSelectionModel()
                  .getLeadSelectionIndex();
          final Attribute attribute = Utility
                  .getKeysByValue(
                          ((AttributeTableModel) attributesTable.getModel())
                                  .getMapIndex(), index).iterator().next();

          ((SimpleEntity) currentObject).moveAttributePosition(attribute, -1);
          ((SimpleEntity) currentObject).notifyObservers();
          attribute.select();
          attribute.notifyObservers(UpdateMessage.SELECT);
        }
      });

      panelButton.add(btnUpAttribute);
    }
    {
      btnDownAttribute.setAlignmentX(CENTER_ALIGNMENT);
      btnDownAttribute.setEnabled(false);
      btnDownAttribute.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
          // Get the selected attribute
          final int index = attributesTable.getSelectionModel()
                  .getLeadSelectionIndex();
          final Attribute attribute = Utility
                  .getKeysByValue(
                          ((AttributeTableModel) attributesTable.getModel())
                                  .getMapIndex(), index).iterator().next();

          ((SimpleEntity) currentObject).moveAttributePosition(attribute, 1);
          ((SimpleEntity) currentObject).notifyObservers();
          attribute.select();
          attribute.notifyObservers(UpdateMessage.SELECT);
        }
      });

      panelButton.add(btnDownAttribute);
    }

    {
      btnRemoveAttribute.setAlignmentX(CENTER_ALIGNMENT);
      btnRemoveAttribute.setEnabled(false);
      btnRemoveAttribute.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
          // Get the selected attribute
          final int index = attributesTable.getSelectionModel()
                  .getLeadSelectionIndex();
          Attribute attribute = Utility
                  .getKeysByValue(
                          ((AttributeTableModel) attributesTable.getModel())
                                  .getMapIndex(), index).iterator().next();

          ((SimpleEntity) currentObject).removeAttribute(attribute);
          ((SimpleEntity) currentObject).notifyObservers();

          for (int i = 0; i <= 1; i++) {
            try {
              attribute = Utility
                      .getKeysByValue(
                              ((AttributeTableModel) attributesTable.getModel())
                                      .getMapIndex(), index - i).iterator()
                      .next();
            } catch (final NoSuchElementException e) {
              continue;
            }

            attribute.select();
            attribute.notifyObservers(UpdateMessage.SELECT);
            break;
          }
        }
      });

      panelButton.add(btnRemoveAttribute);

    }

    p.add(panel, BorderLayout.CENTER);
    p.add(panelButton, BorderLayout.EAST);
    add(p);

    p = new FlatPanel();
    p.setAlignmentY(TOP_ALIGNMENT);
    p.setLayout(new BorderLayout());
    panel = createWhitePanel();
    panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
    panel.add(methodsTable.getScrollPane());

    panelButton = new JPanel();
    panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.PAGE_AXIS));
    panelButton.setOpaque(false);
    
    {
      
      // Compute the total width of components.
      final int BORDER_VERTICAL = 10;
      int buttonWidth = 0;

      // Panel contains buttons add method / constructor for classes.
      panelAddMethodForClass = new JPanel(
          new FlowLayout(FlowLayout.CENTER, 0, 0));
      panelAddMethodForClass.setBackground(null);
      panelAddMethodForClass.setOpaque(false);
      panelAddMethodForClass.setBorder(
          BorderFactory.createEmptyBorder(BORDER_VERTICAL, 0, BORDER_VERTICAL, 0));
      
      // Button add method.
      JButton button = new SButton(
          PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "plus16.png"), 
          "Add method");
      button.setAlignmentX(CENTER_ALIGNMENT);
      button.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent evt) {
          addMethod(true);
        }
      });
      buttonWidth += button.getIcon().getIconWidth();
      panelAddMethodForClass.add(button);
      
      // Button add constructor.
      button = new SButton(
          PersonalizedIcon.createImageIcon(
              Slyum.ICON_PATH + "add-constructor16.png"), 
          "Add constructor");
      button.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          ClassView classView = 
              (ClassView)MultiViewManager.getSelectedGraphicView()
                                         .searchAssociedComponent(currentObject);
          classView.addConstructor();
        }
      });
      buttonWidth += button.getIcon().getIconWidth();
      panelAddMethodForClass.add(button);
      
      panelAddMethodForClass.setMaximumSize(new Dimension(
          buttonWidth, button.getIcon().getIconHeight() + BORDER_VERTICAL*2));
      panelButton.add(panelAddMethodForClass);
      
      // Button add methods for interfaces.
      btnAddMethodForInterface = new SButton(
          PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "plus.png"), 
          "Add method");
      btnAddMethodForInterface.setAlignmentX(CENTER_ALIGNMENT);
      btnAddMethodForInterface.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent evt) {
          addMethod(true);
        }
      });
      btnAddMethodForInterface.setVisible(false);
      panelButton.add(btnAddMethodForInterface);
    }
    {
      btnUpMethod.setAlignmentX(CENTER_ALIGNMENT);
      btnUpMethod.setEnabled(false);
      btnUpMethod.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent evt) {

          // Get the selected method
          final int index = methodsTable.getSelectionModel()
                  .getLeadSelectionIndex();
          final Method method = Utility
                  .getKeysByValue(
                          ((MethodTableModel) methodsTable.getModel())
                                  .getMapIndex(), index).iterator().next();

          ((SimpleEntity) currentObject).moveMethodPosition(method, -1);
          ((SimpleEntity) currentObject).notifyObservers();
          method.select();
          method.notifyObservers(UpdateMessage.SELECT);
        }
      });

      panelButton.add(btnUpMethod);
    }
    {
      btnDownMethod.setAlignmentX(CENTER_ALIGNMENT);
      btnDownMethod.setEnabled(false);
      btnDownMethod.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent evt) {

          // Get the selected method
          final int index = methodsTable.getSelectionModel()
                  .getLeadSelectionIndex();
          final Method method = Utility
                  .getKeysByValue(
                          ((MethodTableModel) methodsTable.getModel())
                                  .getMapIndex(), index).iterator().next();

          ((SimpleEntity) currentObject).moveMethodPosition(method, 1);
          ((SimpleEntity) currentObject).notifyObservers();
          method.select();
          method.notifyObservers(UpdateMessage.SELECT);
        }
      });

      panelButton.add(btnDownMethod);
    }

    {
      btnRemoveMethod.setAlignmentX(CENTER_ALIGNMENT);
      btnRemoveMethod.setEnabled(false);
      btnRemoveMethod.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent arg0) {
          // Get the selected method
          final int index = methodsTable.getSelectionModel()
                  .getLeadSelectionIndex();
          Method method = Utility
                  .getKeysByValue(
                          ((MethodTableModel) methodsTable.getModel())
                                  .getMapIndex(), index).iterator().next();

          ((SimpleEntity) currentObject).removeMethod(method);
          ((SimpleEntity) currentObject).notifyObservers();

          for (int i = 0; i <= 1; i++) {
            try {
              method = Utility
                      .getKeysByValue(
                              ((MethodTableModel) methodsTable.getModel())
                                      .getMapIndex(), index - i).iterator()
                      .next();
            } catch (final NoSuchElementException e) {
              continue;
            }

            method.select();
            method.notifyObservers(UpdateMessage.SELECT);
            break;
          }
        }
      });

      panelButton.add(btnRemoveMethod);
    }

    p.add(panel, BorderLayout.CENTER);
    p.add(panelButton, BorderLayout.EAST);
    add(p);

    panel = panelParameters = new FlatPanel();
    panel.setLayout(new MultiBorderLayout());
    panel.setAlignmentY(TOP_ALIGNMENT);
    scrollPaneParameters = parametersTable.getScrollPane();
    scrollPaneParameters.setVisible(false);
    panel.setMaximumSize(new Dimension(100, Short.MAX_VALUE));
    panel.setPreferredSize(new Dimension(200, 0));
    panel.add(scrollPaneParameters, BorderLayout.CENTER);

    final JPanel btnPanel = new JPanel();

    btnAddParameters.setAlignmentX(CENTER_ALIGNMENT);
    btnAddParameters.setEnabled(false);
    btnAddParameters.addActionListener((ParametersTableModel) parametersTable.getModel());
    btnPanel.add(btnAddParameters);

    btnLeftParameters.setAlignmentX(CENTER_ALIGNMENT);
    btnLeftParameters.setEnabled(false);
    btnLeftParameters.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // Get the selected parameter
        int index = methodsTable.getSelectionModel().getLeadSelectionIndex();
        final Method method = Utility
                .getKeysByValue(
                        ((MethodTableModel) methodsTable.getModel())
                                .getMapIndex(), index).iterator().next();

        final Variable parameter = method.getParameters().get(
                parametersTable.getSelectionModel().getLeadSelectionIndex());

        index = parametersTable.getSelectionModel().getLeadSelectionIndex();
        method.moveParameterPosition(parameter, -1);
        method.notifyObservers();

        method.select();
        method.notifyObservers(UpdateMessage.SELECT);

        index--;
        parametersTable.addRowSelectionInterval(index, index);
        ((ParametersTableModel) parametersTable.getModel())
                .setCurrentParameter(parameter);
      }
    });
    btnPanel.add(btnLeftParameters);

    btnRightParameters.setAlignmentX(CENTER_ALIGNMENT);
    btnRightParameters.setEnabled(false);
    btnRightParameters.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // Get the selected parameter
        int index = methodsTable.getSelectionModel().getLeadSelectionIndex();
        final Method method = Utility
                .getKeysByValue(
                        ((MethodTableModel) methodsTable.getModel())
                                .getMapIndex(), index).iterator().next();

        index = parametersTable.getSelectionModel().getLeadSelectionIndex();
        final Variable parameter = method.getParameters().get(index);

        method.moveParameterPosition(parameter, 1);
        method.notifyObservers();

        method.select();
        method.notifyObservers(UpdateMessage.SELECT);

        index++;
        parametersTable.addRowSelectionInterval(index, index);
        ((ParametersTableModel) parametersTable.getModel())
                .setCurrentParameter(parameter);
      }
    });
    btnPanel.add(btnRightParameters);

    btnRemoveParameters.setAlignmentX(CENTER_ALIGNMENT);
    btnRemoveParameters.setEnabled(false);
    btnRemoveParameters.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ((ParametersTableModel) parametersTable.getModel())
                .removeCurrentParameter();
      }
    });
    btnPanel.add(btnRemoveParameters);
    btnPanel.setBackground(null);
    btnPanel.setPreferredSize(new Dimension(190, 30));
    panel.add(imgMethodSelected, BorderLayout.CENTER);
    panel.add(imgNoParameter, BorderLayout.CENTER);
    panel.add(btnPanel, BorderLayout.SOUTH);
    add(panel);
  }

  public JPanel createSimpleEntityPropreties() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setOpaque(false);
    panel.setAlignmentY(TOP_ALIGNMENT);

    // Entity's name
    textName.setAlignmentX(LEFT_ALIGNMENT);
    textName.setPreferredSize(new Dimension(230, 25));
    textName.addKeyListener(new KeyAdapter() {

      @Override
      public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '\n') {
          final SimpleEntity SimpleEntity = (SimpleEntity) currentObject;

          if (!SimpleEntity.setName(textName.getText()))
            textName.setText(SimpleEntity.getName());
          else
            SimpleEntity.notifyObservers();
        }
      }
    });
    panel.add(textName);
    panel.add(Box.createVerticalStrut(5));
    
    // Visibility combobox
    comboBox.setAlignmentX(LEFT_ALIGNMENT);
    comboBox.setPreferredSize(new Dimension(230, 25));
    comboBox.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        final Visibility newVisibility = Visibility.valueOf(comboBox
                .getSelectedItem().toString().toUpperCase());

        if (newVisibility != Visibility.valueOf(((SimpleEntity) currentObject)
                .getVisibility().getName().toUpperCase())) {
          ((SimpleEntity) currentObject).setVisibility(newVisibility);
          ((SimpleEntity) currentObject).notifyObservers();
        }
      }
    });
    panel.add(comboBox);
    panel.add(Box.createVerticalStrut(5));
    
    // Checkbox is abstract
    checkBoxAbstract.setAlignmentX(LEFT_ALIGNMENT);
    checkBoxAbstract.setOpaque(false);
    checkBoxAbstract.setBorder(null);
    checkBoxAbstract.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        ((SimpleEntity) currentObject).setAbstract(checkBoxAbstract
                .isSelected());
        ((SimpleEntity) currentObject).notifyObservers();
      }
    });
    panel.add(checkBoxAbstract);

    return panel;
  }

  public JLabel createTitleLabel(String text) {
    final JLabel label = new JLabel(text);
    label.setFont(label.getFont().deriveFont(20.0f));
    label.setAlignmentX(CENTER_ALIGNMENT);

    return label;
  }

  public JPanel createWhitePanel() {
    final JPanel panel = new JPanel();
    panel.setBackground(SystemColor.control);
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    panel.setAlignmentY(TOP_ALIGNMENT);
    panel.setOpaque(false);
    return panel;
  }

  @Override
  public void updateComponentInformations(UpdateMessage msg) {
    if (currentObject == null) return;

    stopEditingTables();
    final SimpleEntity SimpleEntity = (SimpleEntity) currentObject;
    final AttributeTableModel modelAttributes = 
        (AttributeTableModel)attributesTable.getModel();
    final MethodTableModel modelMethods = 
        (MethodTableModel)methodsTable.getModel();

    final LinkedList<Attribute> attributes = SimpleEntity.getAttributes();
    final LinkedList<Method> methods = SimpleEntity.getMethods();

    if (msg != null && msg.equals(UpdateMessage.UNSELECT))
      if (!SimpleEntity.getName().equals(textName.getText()))
        if (!SimpleEntity.setName(textName.getText()))
          textName.setText(SimpleEntity.getName());
        else
          SimpleEntity.notifyObservers();

    textName.setText(SimpleEntity.getName());
    checkBoxAbstract.setSelected(SimpleEntity.isAbstract());
    checkBoxAbstract
            .setEnabled(currentObject.getClass() != InterfaceEntity.class);
    comboBox.setSelectedItem(SimpleEntity.getVisibility().getName());

    modelAttributes.clearAll();
    modelMethods.clearAll();

    for (Attribute attribute : attributes)
      modelAttributes.addAttribute(attribute);

    for (Method method : methods)
      modelMethods.addMethod(method);

    btnRemoveMethod.setEnabled(false);
    btnRemoveAttribute.setEnabled(false);
    btnUpAttribute.setEnabled(false);
    btnDownAttribute.setEnabled(false);
    btnUpMethod.setEnabled(false);
    btnDownMethod.setEnabled(false);
    btnRemoveParameters.setEnabled(false);
    btnRightParameters.setEnabled(false);
    btnLeftParameters.setEnabled(false);
    
    if (currentObject instanceof ClassEntity) {
      panelAddMethodForClass.setVisible(true);
      btnAddMethodForInterface.setVisible(false);
    } else {
      btnAddMethodForInterface.setVisible(true);
      panelAddMethodForClass.setVisible(false);
    }

    validate();

    if (msg == UpdateMessage.ADD_METHOD
            || msg == UpdateMessage.ADD_METHOD_NO_EDIT)
      methodsTable.scrollRectToVisible(methodsTable.getCellRect(
              methodsTable.getRowCount(), methodsTable.getColumnCount(), true));

    if (msg == UpdateMessage.ADD_ATTRIBUTE
            || msg == UpdateMessage.ADD_ATTRIBUTE_NO_EDIT)
      attributesTable.scrollRectToVisible(attributesTable.getCellRect(
              attributesTable.getRowCount(), attributesTable.getColumnCount(),
              true));

  }
  
  private void addAttribute(boolean editRequest) {
    SimpleEntity entity = (SimpleEntity)currentObject;
    
    entity.addAttribute(new Attribute("attribute", PrimitiveType.VOID_TYPE));
    
    if (editRequest)
      entity.notifyObservers(UpdateMessage.ADD_ATTRIBUTE);
    else
      entity.notifyObservers(UpdateMessage.ADD_ATTRIBUTE_NO_EDIT);
  }
  
  private void addParameters(Method method) { 
    if (method == null)
      return;   
    method.addParameter(new Variable("p", new Type(PrimitiveType.INTEGER_TYPE.getName())));
    method.notifyObservers();
    method.select();
    method.notifyObservers(UpdateMessage.SELECT);
  }
  
  private void addMethod(boolean editRequest) {
    SimpleEntity simpleEntity = (SimpleEntity)currentObject;
    
    simpleEntity.addMethod(new Method("method", PrimitiveType.VOID_TYPE, Visibility.PUBLIC, simpleEntity));
    
    if (editRequest)
      simpleEntity.notifyObservers(UpdateMessage.ADD_METHOD);
    else
      simpleEntity.notifyObservers(UpdateMessage.ADD_METHOD_NO_EDIT);
  }

  private void stopEditingTables() {
    TableCellEditor a = attributesTable.getCellEditor(), m = methodsTable
            .getCellEditor(), p = parametersTable.getCellEditor();

    if (a != null) a.stopCellEditing();

    if (m != null) m.stopCellEditing();

    if (p != null) p.stopCellEditing();
  }

  @Override
  public Component add(Component comp) {
    Component c = super.add(comp);
    super.add(Box.createHorizontalStrut(5));
    return c;
  }
}
