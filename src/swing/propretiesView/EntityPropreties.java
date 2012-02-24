package swing.propretiesView;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

import swing.SButton;
import swing.JPanelRounded;
import swing.Slyum;
import swing.propretiesView.GlobalPropreties;
import utility.PersonalizedIcon;
import utility.Utility;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Attribute;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.Method;
import classDiagram.components.PrimitiveType;
import classDiagram.components.Type;
import classDiagram.components.Variable;
import classDiagram.components.Visibility;

/**
 * Show the propreties of an UML entity with Swing components. All inner classes
 * are used for create customized JTable.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class EntityPropreties extends GlobalPropreties
{
	private class AttributeTableModel extends AbstractTableModel implements Observer, TableModelListener, MouseListener
	{
		private static final long serialVersionUID = 5735895585153401565L;

		private final String[] columnNames = { "Name", "Type", "Visibility", "Constant", "Static" };

		private final LinkedList<Object[]> data = new LinkedList<Object[]>();

		private final HashMap<Attribute, Integer> mapIndex = new HashMap<Attribute, Integer>();

		public void addAttribute(Attribute attribute)
		{
			data.add(new Object[] { attribute.getName(), attribute.getType().getName(), attribute.getVisibility().getName(), attribute.isConstant(), attribute.isStatic() });

			attribute.addObserver(this);
			mapIndex.put(attribute, data.size() - 1);

			fireTableRowsInserted(0, data.size());
		}

		public void clearAll()
		{
			data.clear();
			mapIndex.clear();
			fireTableDataChanged();
		}

		@Override
		public Class<? extends Object> getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public String getColumnName(int col)
		{
			return columnNames[col];
		}

		@SuppressWarnings("unchecked")
		public HashMap<Attribute, Integer> getMapIndex()
		{
			return (HashMap<Attribute, Integer>) mapIndex.clone();
		}

		@Override
		public int getRowCount()
		{
			return data.size();
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			return data.get(row)[col];
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			return !(currentObject.getClass() == InterfaceEntity.class && col == 4);
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (currentObject == null || !(currentObject instanceof Entity))
				return;

			// Get the selected attribute
			final int index = attributesTable.getSelectionModel().getLeadSelectionIndex();
			final Attribute attribute = Utility.getKeysByValue(mapIndex, index).iterator().next();

			// Unselect all attributes
			for (final Attribute a : ((Entity) currentObject).getAttributes())
			{
				if (a.equals(attribute))
					continue;

				a.select();
				a.notifyObservers(UpdateMessage.UNSELECT);
			}

			// Select the selected attribute
			attribute.select();
			attribute.notifyObservers(UpdateMessage.SELECT);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
		}

		public void setAttribute(Attribute attribute, int index)
		{
			data.set(index, new Object[] { attribute.getName(), attribute.getType().getName(), attribute.getVisibility().getName(), attribute.isConstant(), attribute.isStatic() });

			fireTableRowsUpdated(index, index);
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			data.get(row)[col] = value;

			fireTableCellUpdated(row, col);
		}

		@Override
		public void tableChanged(TableModelEvent e)
		{
			final int row = e.getFirstRow();
			final int column = e.getColumn();

			if (column == -1)
				return;

			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);
			final Attribute attribute = Utility.getKeysByValue(mapIndex, row).iterator().next();

			switch (column)
			{
				case 0: // nom
					if (!attribute.setName((String) data))
						setValueAt(attribute.getName(), row, column);

					break;

				case 1: // type
					String s = (String) data;
					
					if (!Type.checkSemantic(s))
						setValueAt(attribute.getType().getName(), row, column);
					else
						attribute.setType(new Type(s));

					break;

				case 2: // visibility
					attribute.setVisibility(Visibility.valueOf(((String) data).toUpperCase()));
					break;

				case 3: // constant
					attribute.setConstant((Boolean) data);
					break;

				case 4: // static
					attribute.setStatic((Boolean) data);
					break;
			}

			attribute.notifyObservers(UpdateMessage.SELECT);
			attribute.getType().notifyObservers();

			attributesTable.addRowSelectionInterval(row, row);
		}

		@Override
		public void update(Observable arg0, Object arg1)
		{
			final Attribute attribute = (Attribute) arg0;
			try
			{
				final int index = mapIndex.get(attribute);

				if (index == -1)
					return;

				if (arg1 != null && arg1 instanceof UpdateMessage)
					switch ((UpdateMessage) arg1)
					{
						case SELECT:
							btnRemoveAttribute.setEnabled(true);
							btnUpAttribute.setEnabled(index > 0);
							btnDownAttribute.setEnabled(index < mapIndex.size() - 1);
							showInProperties();
							attributesTable.addRowSelectionInterval(index, index);
							break;
						case UNSELECT:
							attributesTable.removeRowSelectionInterval(index, index);
							break;
					}

				setAttribute(attribute, index);
			} catch (final Exception e)
			{

			}
		}

	}

	private class MethodTableModel extends AbstractTableModel implements Observer, TableModelListener, MouseListener
	{
		private static final long serialVersionUID = -8935769363179120147L;

		private final String[] columnNames = { "Name", "Type", "Visibility", "Abstract", "Static" };

		private final LinkedList<Object[]> data = new LinkedList<Object[]>();

		private final HashMap<Method, Integer> mapIndex = new HashMap<Method, Integer>();

		public void addMethod(Method method)
		{
			data.add(new Object[] { method.getName(), method.getReturnType().getName(), method.getVisibility().getName(), method.isAbstract(), method.isStatic() });

			method.addObserver(this);
			method.addObserver((ParametersTableModel) parametersTable.getModel());
			mapIndex.put(method, data.size() - 1);

			fireTableRowsInserted(0, data.size());
		}

		public void clearAll()
		{
			data.clear();
			mapIndex.clear();
			fireTableDataChanged();
		}

		@Override
		public Class<? extends Object> getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public String getColumnName(int col)
		{
			return columnNames[col];
		}

		@SuppressWarnings( { "unchecked" })
		public HashMap<Method, Integer> getMapIndex()
		{
			return (HashMap<Method, Integer>) mapIndex.clone();
		}

		@Override
		public int getRowCount()
		{
			return data.size();
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			return data.get(row)[col];
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			return !(currentObject.getClass() == InterfaceEntity.class && col == 3);
		}

		@Override
		public void mouseClicked(MouseEvent e)
		{
		}

		@Override
		public void mouseEntered(MouseEvent e)
		{
		}

		@Override
		public void mouseExited(MouseEvent e)
		{
		}

		@Override
		public void mousePressed(MouseEvent e)
		{
			if (currentObject == null || !(currentObject instanceof Entity))
				return;

			// Get the selected method
			final int index = methodsTable.getSelectionModel().getLeadSelectionIndex();
			final Method method = Utility.getKeysByValue(mapIndex, index).iterator().next();

			// Unselect all methods
			for (final Method m : ((Entity) currentObject).getMethods())
			{
				if (m.equals(method))
					continue;

				m.select();
				m.notifyObservers(UpdateMessage.UNSELECT);
			}

			// Select the selected method
			method.select();
			method.notifyObservers(UpdateMessage.SELECT);
		}

		@Override
		public void mouseReleased(MouseEvent e)
		{
		}

		public void setMethod(Method method, int index)
		{
			data.set(index, new Object[] { method.getName(), method.getReturnType().getName(), method.getVisibility().getName(), method.isAbstract(), method.isStatic() });

			fireTableRowsUpdated(index, index);
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			data.get(row)[col] = value;

			fireTableCellUpdated(row, col);
		}

		@Override
		public void tableChanged(TableModelEvent e)
		{
			final int row = e.getFirstRow();
			final int column = e.getColumn();

			if (column == -1)
				return;

			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);
			final Method method = Utility.getKeysByValue(mapIndex, row).iterator().next();

			switch (column)
			{
				case 0: // nom
					if (!method.setName((String) data))
						setValueAt(method.getName(), row, column);

					break;

				case 1: // type
					String s = (String) data;
					if (!Type.checkSemantic(s))
						setValueAt(method.getReturnType().getName(), row, column);
					else
						method.setReturnType(new Type(s));

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
		public void update(Observable arg0, Object arg1)
		{
			try
			{
				final int index = mapIndex.get(arg0);

				if (index == -1)
					return;

				if (arg1 != null && arg1 instanceof UpdateMessage)
					switch ((UpdateMessage) arg1)
					{
						case SELECT:
							btnRemoveMethod.setEnabled(true);
							btnUpMethod.setEnabled(index > 0);
							btnDownMethod.setEnabled(index < mapIndex.size() - 1);
							showInProperties();
							methodsTable.addRowSelectionInterval(index, index);
							break;
						case UNSELECT:
							methodsTable.removeRowSelectionInterval(index, index);
							break;
					}

				setMethod((Method) arg0, index);
			} catch (final Exception e)
			{

			}
		}

	}

	private class ParametersTableModel extends AbstractTableModel implements Observer, TableModelListener, ActionListener, MouseListener
	{
		private static final long serialVersionUID = 8577198492892934888L;

		private final String[] columnNames = { "Parameter", "Type" };

		private Method currentMethod;

		private Variable currentParameter;

		private final LinkedList<Object[]> data = new LinkedList<Object[]>();

		@Override
		public void actionPerformed(ActionEvent e)
		{
			if (currentMethod == null)
				return;

			currentMethod.addParameter(new Variable("p", new Type(PrimitiveType.INTEGER_TYPE.getName())));
			currentMethod.notifyObservers();
			currentMethod.select();
			currentMethod.notifyObservers(UpdateMessage.SELECT);
		}

		public void clearAll()
		{
			if (currentMethod != null)
				for (Variable v : currentMethod.getParameters())
					v.deleteObserver(this);
			
			data.clear();
			fireTableDataChanged();
		}

		@Override
		public Class<? extends Object> getColumnClass(int c)
		{
			return getValueAt(0, c).getClass();
		}

		@Override
		public int getColumnCount()
		{
			return columnNames.length;
		}

		@Override
		public String getColumnName(int col)
		{
			return columnNames[col];
		}

		public Variable getCurrentParameter()
		{
			return currentParameter;
		}

		@Override
		public int getRowCount()
		{
			return data.size();
		}

		@Override
		public Object getValueAt(int row, int col)
		{
			return data.get(row)[col];
		}

		@Override
		public boolean isCellEditable(int row, int col)
		{
			return true;
		}

		@Override
		public void mouseClicked(MouseEvent arg0)
		{
		}

		@Override
		public void mouseEntered(MouseEvent arg0)
		{
		}

		@Override
		public void mouseExited(MouseEvent arg0)
		{
		}

		@Override
		public void mousePressed(MouseEvent arg0)
		{
			if (currentMethod == null)
				return;

			// Get the selected parameter
			final int index = parametersTable.getSelectionModel().getLeadSelectionIndex();
			final Variable variable = currentMethod.getParameters().get(index);

			setCurrentParameter(variable);
		}

		@Override
		public void mouseReleased(MouseEvent arg0)
		{
		}

		private void parameterSelected()
		{
			btnRemoveParameters.setEnabled(currentParameter != null);
			btnLeftParameters.setEnabled(currentMethod.getParameters().indexOf(currentParameter) > 0);
			btnRightParameters.setEnabled(currentMethod.getParameters().indexOf(currentParameter) < currentMethod.getParameters().size() - 1);
		}

		public void removeCurrentParameter()
		{
			final Variable parameter = getCurrentParameter();
			int index = currentMethod.getParameters().indexOf(parameter);

			if (parameter == null)
				return;

			currentMethod.removeParameters(parameter);
			parameter.notifyObservers();
			currentMethod.select();
			currentMethod.notifyObservers();
			currentMethod.notifyObservers(UpdateMessage.SELECT);

			{
				currentMethod.select();
				currentMethod.notifyObservers(UpdateMessage.SELECT);

				final int size = currentMethod.getParameters().size();
				if (size == index)
					index--;

				if (size == 0)
					return;

				parametersTable.addRowSelectionInterval(index, index);
				((ParametersTableModel) parametersTable.getModel()).setCurrentParameter(currentMethod.getParameters().get(index));
			}
		}

		public void setCurrentParameter(Variable parameter)
		{
			currentParameter = parameter;
			parameterSelected();
		}

		public void setParameter(Method method)
		{
			if (method == null)
				return;
			
			clearAll();
			for (final Variable v : method.getParameters())
			{
				v.addObserver(this);
				data.add(new Object[] { v.getName(), v.getType().getName() });
			}

			fireTableRowsInserted(0, data.size());
		}

		@Override
		public void setValueAt(Object value, int row, int col)
		{
			data.get(row)[col] = value;

			fireTableCellUpdated(row, col);
		}

		@Override
		public void tableChanged(TableModelEvent e)
		{
			if (currentMethod == null)
				return;

			final int row = e.getFirstRow();
			final int column = e.getColumn();

			if (column == -1)
				return;

			final TableModel model = (TableModel) e.getSource();
			final Object data = model.getValueAt(row, column);

			switch (column)
			{
				case 0: // Parameter
					currentMethod.getParameters().get(row).setName((String) data);
					break;

				case 1: // type
					currentMethod.getParameters().get(row).setType(new Type((String) data));
					break;
			}

			currentMethod.getParameters().get(row).notifyObservers();
			currentMethod.notifyObservers();
			currentMethod.getParameters().get(row).getType().notifyObservers();
			currentMethod.select();
			currentMethod.notifyObservers(UpdateMessage.SELECT);

			final Variable parameter = currentMethod.getParameters().get(row);

			parametersTable.addRowSelectionInterval(row, row);
			((ParametersTableModel) parametersTable.getModel()).setCurrentParameter(parameter);
		}

		@Override
		public void update(Observable arg0, Object arg1)
		{			
			if (arg1 != null && arg1 instanceof UpdateMessage)
			{
				switch ((UpdateMessage) arg1)
				{
					case SELECT:
						showInProperties();
						clearAll();
						currentMethod = (Method) arg0;
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
				}
			}
			else
				setParameter(currentMethod);
		}
	}

	private static EntityPropreties instance = new EntityPropreties();

	private static final long serialVersionUID = 7817631106855232540L;

	/**
	 * Get the unique instance of this class.
	 * 
	 * @return the unique instance of EntityPropreties
	 */
	public static EntityPropreties getInstance()
	{
		return instance;
	}

	/**
	 * Set the given size for preferredSize, maximumSize and minimumSize to the
	 * given component.
	 * 
	 * @param component
	 *            the component to resize
	 * @param size
	 *            the size
	 */
	public static void setAllSize(JComponent component, Dimension size)
	{
		component.setPreferredSize(size);
		component.setMaximumSize(size);
		component.setMinimumSize(size);
	}

	JTable attributesTable, methodsTable, parametersTable;
	
	private final JButton btnAddParameters,
			btnRemoveMethod,
			btnRemoveAttribute,
			btnUpAttribute,
			btnDownAttribute,
			btnUpMethod,
			btnDownMethod,
			btnRemoveParameters,
			btnRightParameters,
			btnLeftParameters;

	JCheckBox checkBoxAbstract = new JCheckBox("Abstract");

	JComboBox<String> comboBox = Utility.getVisibilityComboBox();

	private final JLabel imgNoAttribute, imgNoMethod, imgMethodSelected,
			imgNoParameter;

	JPanel panelParameters;

	private JScrollPane scrollPaneAttributes, scrollPaneMethods,
			scrollPaneParameters;

	JTextField textName = new JTextField();

	protected EntityPropreties()
	{
		String small = ".png";
		
		if (Slyum.getSmallIcons())
			small = "_small.png";
			
		btnAddParameters = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_plus_blue" + small), Color.BLUE, "Add");
		btnRemoveMethod = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_red_delete" + small), Color.RED, "Remove");
		btnRemoveAttribute = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_red_delete" + small), Color.RED, "Remove");
		btnUpAttribute = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_violet_up" + small), Color.MAGENTA, "Up");
		btnDownAttribute = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_violet_down" + small), Color.MAGENTA, "Down");
		btnUpMethod = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_violet_up" + small), Color.MAGENTA, "Up");
		btnDownMethod = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_violet_down" + small), Color.MAGENTA, "Down");
		btnRemoveParameters = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_red_delete" + small), Color.RED, "Remove");
		btnRightParameters = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_violet_right" + small), Color.MAGENTA, "Rigth");
		btnLeftParameters = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_violet_left" + small), Color.MAGENTA, "Left");
		
		imgNoAttribute = new JLabel(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "empty_attribute.png"));
		imgNoMethod = new JLabel(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "empty_method.png"));
		imgMethodSelected = new JLabel(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "select_method.png"));
		imgNoParameter = new JLabel(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "empty_parameter.png"));

		imgNoMethod.setAlignmentX(CENTER_ALIGNMENT);
		imgNoAttribute.setAlignmentX(CENTER_ALIGNMENT);
		imgMethodSelected.setAlignmentX(CENTER_ALIGNMENT);
		imgNoParameter.setAlignmentX(CENTER_ALIGNMENT);

		imgNoParameter.setVisible(false);

		setBackground(Color.WHITE);
		attributesTable = new JTable(new AttributeTableModel());
		attributesTable.setPreferredScrollableViewportSize(new Dimension(200, 0));

		attributesTable.getModel().addTableModelListener((AttributeTableModel) attributesTable.getModel());

		attributesTable.addMouseListener((AttributeTableModel) attributesTable.getModel());

		methodsTable = new JTable(new MethodTableModel());
		methodsTable.setPreferredScrollableViewportSize(new Dimension(200, 0));
		methodsTable.getModel().addTableModelListener((MethodTableModel) methodsTable.getModel());
		methodsTable.addMouseListener((MethodTableModel) methodsTable.getModel());

		parametersTable = new JTable(new ParametersTableModel());
		parametersTable.setPreferredScrollableViewportSize(new Dimension(70, 0));
		parametersTable.getModel().addTableModelListener((ParametersTableModel) parametersTable.getModel());
		parametersTable.addMouseListener((ParametersTableModel) parametersTable.getModel());

		TableColumn visibilityColumn = attributesTable.getColumnModel().getColumn(2);
		visibilityColumn.setCellEditor(new DefaultCellEditor(Utility.getVisibilityComboBox()));

		visibilityColumn = methodsTable.getColumnModel().getColumn(2);
		visibilityColumn.setCellEditor(new DefaultCellEditor(Utility.getVisibilityComboBox()));

		TableColumn column = null;
		for (int i = 0; i < attributesTable.getColumnCount(); i++)
		{
			column = attributesTable.getColumnModel().getColumn(i);

			if (i < 2)
				column.setPreferredWidth(70);

			else if (i == 2)
				column.setPreferredWidth(20);
			else
				column.setPreferredWidth(10);

		}

		for (int i = 0; i < methodsTable.getColumnCount(); i++)
		{
			column = methodsTable.getColumnModel().getColumn(i);

			if (i < 2)
				column.setPreferredWidth(70);

			else if (i == 2)
				column.setPreferredWidth(20);
			else
				column.setPreferredWidth(10);

		}

		JPanelRounded p = new JPanelRounded();
		p.setForeground(Color.GRAY);
		p.setBackground(new Color(240, 240, 240));
		p.setAlignmentY(TOP_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
		{
			final GridBagLayout gbl_panel = new GridBagLayout();
			gbl_panel.columnWidths = new int[] { 0, 0 };
			gbl_panel.rowHeights = new int[] { 0, 0 };
			gbl_panel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
			gbl_panel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
			p.setLayout(gbl_panel);
		}

		p.setMaximumSize(new Dimension(0, Integer.MAX_VALUE));

		{
			final GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
			gbc_btnNewButton.anchor = GridBagConstraints.NORTH;
			gbc_btnNewButton.gridx = 0;
			gbc_btnNewButton.gridy = 0;
			p.add(createEntityPropreties(), gbc_btnNewButton);
		}

		add(p);

		p = new JPanelRounded();
		p.setForeground(Color.GRAY);
		p.setBackground(new Color(240, 240, 240));
		p.setAlignmentY(TOP_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
		p.setLayout(new BorderLayout());
		JPanel panel = createWhitePanel();
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		JScrollPane scrollPane = scrollPaneAttributes = new JScrollPane(attributesTable);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(new LineBorder(Color.GRAY, 1, true));
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setVisible(false);
		panel.add(createTitleLabel("Attributes"));
		panel.add(scrollPane);
		panel.add(imgNoAttribute);

		JPanel panelButton = new JPanel();
		panelButton.setOpaque(false);
		panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.PAGE_AXIS));

		{
			final JButton button = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_plus_blue" + small), Color.BLUE, "Add");
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setAlignmentX(CENTER_ALIGNMENT);
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					((Entity) currentObject).addAttribute(new Attribute("attribute", PrimitiveType.VOID_TYPE));
					((Entity) currentObject).notifyObservers(UpdateMessage.ADD_ATTRIBUTE);
				}
			});

			panelButton.add(button);
		}

		{
			btnUpAttribute.setAlignmentX(CENTER_ALIGNMENT);
			btnUpAttribute.setEnabled(false);
			btnUpAttribute.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					// Get the selected attribute
					final int index = attributesTable.getSelectionModel().getLeadSelectionIndex();
					final Attribute attribute = Utility.getKeysByValue(((AttributeTableModel) attributesTable.getModel()).getMapIndex(), index).iterator().next();

					((Entity) currentObject).moveAttributePosition(attribute, -1);
					((Entity) currentObject).notifyObservers();
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
				public void actionPerformed(ActionEvent arg0)
				{
					// Get the selected attribute
					final int index = attributesTable.getSelectionModel().getLeadSelectionIndex();
					final Attribute attribute = Utility.getKeysByValue(((AttributeTableModel) attributesTable.getModel()).getMapIndex(), index).iterator().next();

					((Entity) currentObject).moveAttributePosition(attribute, 1);
					((Entity) currentObject).notifyObservers();
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
				public void actionPerformed(ActionEvent arg0)
				{
					// Get the selected attribute
					final int index = attributesTable.getSelectionModel().getLeadSelectionIndex();
					Attribute attribute = Utility.getKeysByValue(((AttributeTableModel) attributesTable.getModel()).getMapIndex(), index).iterator().next();

					((Entity) currentObject).removeAttribute(attribute);
					((Entity) currentObject).notifyObservers();

					for (int i = 0; i <= 1; i++)
					{
						try
						{
							attribute = Utility.getKeysByValue(((AttributeTableModel) attributesTable.getModel()).getMapIndex(), index - i).iterator().next();
						} catch (final NoSuchElementException e)
						{
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

		p = new JPanelRounded();
		p.setForeground(Color.GRAY);
		p.setBackground(new Color(240, 240, 240));
		p.setAlignmentY(TOP_ALIGNMENT);
		p.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
		p.setLayout(new BorderLayout());
		panel = createWhitePanel();
		panel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
		scrollPane = scrollPaneMethods = new JScrollPane(methodsTable);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(new LineBorder(Color.GRAY, 1, true));
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setVisible(false);
		panel.add(createTitleLabel("Methods"));
		panel.add(scrollPane);
		panel.add(imgNoMethod);

		panelButton = new JPanel();
		panelButton.setLayout(new BoxLayout(panelButton, BoxLayout.PAGE_AXIS));
		panelButton.setOpaque(false);
		{
			final JButton button = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "button_plus_blue" + small), Color.BLUE, "Add");
			button.setBorderPainted(false);
			button.setContentAreaFilled(false);
			button.setAlignmentX(CENTER_ALIGNMENT);
			button.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					final Entity entity = (Entity) currentObject;
					entity.addMethod(new Method("method", PrimitiveType.VOID_TYPE, Visibility.PUBLIC, entity));
					entity.notifyObservers(UpdateMessage.ADD_METHOD);
				}
			});

			panelButton.add(button);
		}

		{
			btnUpMethod.setAlignmentX(CENTER_ALIGNMENT);
			btnUpMethod.setEnabled(false);
			btnUpMethod.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0)
				{

					// Get the selected method
					final int index = methodsTable.getSelectionModel().getLeadSelectionIndex();
					final Method method = Utility.getKeysByValue(((MethodTableModel) methodsTable.getModel()).getMapIndex(), index).iterator().next();

					((Entity) currentObject).moveMethodPosition(method, -1);
					((Entity) currentObject).notifyObservers();
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
				public void actionPerformed(ActionEvent arg0)
				{

					// Get the selected method
					final int index = methodsTable.getSelectionModel().getLeadSelectionIndex();
					final Method method = Utility.getKeysByValue(((MethodTableModel) methodsTable.getModel()).getMapIndex(), index).iterator().next();

					((Entity) currentObject).moveMethodPosition(method, 1);
					((Entity) currentObject).notifyObservers();
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
				public void actionPerformed(ActionEvent arg0)
				{
					// Get the selected method
					final int index = methodsTable.getSelectionModel().getLeadSelectionIndex();
					Method method = Utility.getKeysByValue(((MethodTableModel) methodsTable.getModel()).getMapIndex(), index).iterator().next();

					((Entity) currentObject).removeMethod(method);
					((Entity) currentObject).notifyObservers();

					for (int i = 0; i <= 1; i++)
					{
						try
						{
							method = Utility.getKeysByValue(((MethodTableModel) methodsTable.getModel()).getMapIndex(), index - i).iterator().next();
						} catch (final NoSuchElementException e)
						{
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

		panel = panelParameters = new JPanelRounded();
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setForeground(Color.GRAY);
		panel.setBackground(new Color(240, 240, 240));
		panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 2, 5, 2), BorderFactory.createEtchedBorder()));
		panel.setAlignmentY(TOP_ALIGNMENT);
		panel.setBorder(BorderFactory.createEmptyBorder(9, 9, 9, 9));
		scrollPane = scrollPaneParameters = new JScrollPane(parametersTable);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.setBorder(new LineBorder(Color.GRAY, 1, true));
		scrollPane.setBackground(Color.WHITE);
		scrollPane.setVisible(false);
		panel.setMaximumSize(new Dimension(100, Short.MAX_VALUE));
		panel.setPreferredSize(new Dimension(200, 0));
		panel.add(createTitleLabel("Parameters"));
		panel.add(scrollPane);

		final JPanel btnPanel = new JPanel();
		btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.LINE_AXIS));

		btnAddParameters.setAlignmentX(CENTER_ALIGNMENT);
		btnAddParameters.setBorder(null);
		btnAddParameters.setEnabled(false);
		btnAddParameters.addActionListener((ParametersTableModel) parametersTable.getModel());
		btnPanel.add(btnAddParameters);

		btnLeftParameters.setAlignmentX(CENTER_ALIGNMENT);
		btnLeftParameters.setBorder(null);
		btnLeftParameters.setEnabled(false);
		btnLeftParameters.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Get the selected parameter
				int index = methodsTable.getSelectionModel().getLeadSelectionIndex();
				final Method method = Utility.getKeysByValue(((MethodTableModel) methodsTable.getModel()).getMapIndex(), index).iterator().next();

				final Variable parameter = method.getParameters().get(parametersTable.getSelectionModel().getLeadSelectionIndex());

				index = parametersTable.getSelectionModel().getLeadSelectionIndex();
				method.moveParameterPosition(parameter, -1);
				method.notifyObservers();

				method.select();
				method.notifyObservers(UpdateMessage.SELECT);

				index--;
				parametersTable.addRowSelectionInterval(index, index);
				((ParametersTableModel) parametersTable.getModel()).setCurrentParameter(parameter);
			}
		});
		btnPanel.add(btnLeftParameters);

		btnRightParameters.setAlignmentX(CENTER_ALIGNMENT);
		btnRightParameters.setBorder(null);
		btnRightParameters.setEnabled(false);
		btnRightParameters.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				// Get the selected parameter
				int index = methodsTable.getSelectionModel().getLeadSelectionIndex();
				final Method method = Utility.getKeysByValue(((MethodTableModel) methodsTable.getModel()).getMapIndex(), index).iterator().next();

				index = parametersTable.getSelectionModel().getLeadSelectionIndex();
				final Variable parameter = method.getParameters().get(index);

				method.moveParameterPosition(parameter, 1);
				method.notifyObservers();

				method.select();
				method.notifyObservers(UpdateMessage.SELECT);

				index++;
				parametersTable.addRowSelectionInterval(index, index);
				((ParametersTableModel) parametersTable.getModel()).setCurrentParameter(parameter);
			}
		});
		btnPanel.add(btnRightParameters);

		btnRemoveParameters.setAlignmentX(CENTER_ALIGNMENT);
		btnRemoveParameters.setBorder(null);
		btnRemoveParameters.setEnabled(false);
		btnRemoveParameters.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				((ParametersTableModel) parametersTable.getModel()).removeCurrentParameter();
			}
		});
		btnPanel.add(btnRemoveParameters);

		panel.add(imgMethodSelected);
		panel.add(imgNoParameter);
		panel.add(btnPanel);
		add(panel);
	}

	public JPanel createEntityPropreties()
	{
		final JPanel panel = new JPanel();
		Dimension size = new Dimension(200, 110);
		setAllSize(panel, size);
		panel.setOpaque(false);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setAlignmentY(TOP_ALIGNMENT);
		panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));

		final JPanel panelTitle = new JPanel(new FlowLayout());
		panelTitle.setOpaque(false);
		panelTitle.add(createTitleLabel("Entity"));

		size = new Dimension(200, 20);
		setAllSize(panelTitle, new Dimension((int) size.getWidth(), 40));
		setAllSize(textName, size);
		setAllSize(checkBoxAbstract, new Dimension((int) size.getWidth(), 30));
		setAllSize(comboBox, new Dimension(80, (int) size.getHeight()));

		panelTitle.setAlignmentX(LEFT_ALIGNMENT);
		textName.setAlignmentX(LEFT_ALIGNMENT);
		checkBoxAbstract.setAlignmentX(LEFT_ALIGNMENT);
		comboBox.setAlignmentX(LEFT_ALIGNMENT);

		checkBoxAbstract.setOpaque(false);

		// Event
		textName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e)
			{
				if (e.getKeyChar() == '\n')
				{
					final Entity entity = (Entity) currentObject;

					if (!entity.setName(textName.getText()))
						textName.setText(entity.getName());
					else
						entity.notifyObservers();
				}
			}
		});

		checkBoxAbstract.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				((Entity) currentObject).setAbstract(checkBoxAbstract.isSelected());
				((Entity) currentObject).notifyObservers();
			}
		});

		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e)
			{
				final Visibility newVisibility = Visibility.valueOf(comboBox.getSelectedItem().toString().toUpperCase());

				if (newVisibility != Visibility.valueOf(((Entity) currentObject).getVisibility().getName().toUpperCase()))
				{
					((Entity) currentObject).setVisibility(newVisibility);
					((Entity) currentObject).notifyObservers();
				}
			}
		});

		panel.add(panelTitle);
		panel.add(textName);
		panel.add(checkBoxAbstract);
		panel.add(comboBox);

		return panel;
	}	

	public JLabel createTitleLabel(String text)
	{
		final JLabel label = new JLabel(text);
		label.setFont(label.getFont().deriveFont(20.0f));
		label.setAlignmentX(CENTER_ALIGNMENT);

		return label;
	}

	public JPanel createWhitePanel()
	{
		final JPanel panel = new JPanel();
		panel.setBackground(SystemColor.control);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setAlignmentY(TOP_ALIGNMENT);
		panel.setOpaque(false);
		return panel;
	}

	@Override
	public void updateComponentInformations(UpdateMessage msg)
	{
		if (currentObject == null)
			return;

		final Entity entity = (Entity) currentObject;
		final AttributeTableModel modelAttributes = (AttributeTableModel) attributesTable.getModel();
		final MethodTableModel modelMethods = (MethodTableModel) methodsTable.getModel();

		final LinkedList<Attribute> attributes = entity.getAttributes();
		final LinkedList<Method> methods = entity.getMethods();

		if (msg != null && msg.equals(UpdateMessage.UNSELECT))
			if (!entity.getName().equals(textName.getText()))
				if (!entity.setName(textName.getText()))
					textName.setText(entity.getName());
				else
					entity.notifyObservers();

		textName.setText(entity.getName());
		checkBoxAbstract.setSelected(entity.isAbstract());
		checkBoxAbstract.setEnabled(currentObject.getClass() != InterfaceEntity.class);
		comboBox.setSelectedItem(entity.getVisibility().getName());

		modelAttributes.clearAll();
		modelMethods.clearAll();

		for (int i = 0; i < attributes.size(); i++)
			modelAttributes.addAttribute(attributes.get(i));

		for (int i = 0; i < methods.size(); i++)
			modelMethods.addMethod(methods.get(i));

		scrollPaneAttributes.setVisible(attributes.size() > 0);
		scrollPaneMethods.setVisible(methods.size() > 0);

		imgNoAttribute.setVisible(attributes.size() <= 0);
		imgNoMethod.setVisible(methods.size() <= 0);

		btnRemoveMethod.setEnabled(false);
		btnRemoveAttribute.setEnabled(false);
		btnUpAttribute.setEnabled(false);
		btnDownAttribute.setEnabled(false);
		btnUpMethod.setEnabled(false);
		btnDownMethod.setEnabled(false);
		btnRemoveParameters.setEnabled(false);
		btnRightParameters.setEnabled(false);
		btnLeftParameters.setEnabled(false);

		validate();
	}
}
