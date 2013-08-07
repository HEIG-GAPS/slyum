package swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import utility.Utility;
import classDiagram.components.Method;
import classDiagram.components.SimpleEntity;
import classDiagram.components.Variable;

public class OverridesAndImplementationsDialog extends JDialog
{

	public class CheckableItem
	{
		private boolean isSelected;

		private final Method m;

		public CheckableItem(Method m)
		{
			this.m = m;
			isSelected = false;
		}

		public Method getMethod()
		{
			return m;
		}

		public boolean isSelected()
		{
			return isSelected;
		}

		public void setSelected(boolean b)
		{
			isSelected = b;
		}

		@Override
		public String toString()
		{
			String methodString = m.getName();

			methodString += "(";

			for (final Variable v : m.getParameters())
				methodString += v.toString() + ", ";

			if (m.getParameters().size() > 0)
				methodString = methodString.substring(0, methodString.length() - 2);

			methodString += ") : " + m.getReturnType();

			return methodString;
		}
	}

	class CheckListRenderer extends JCheckBox implements ListCellRenderer<Object>
	{
		private static final long serialVersionUID = 1514851566910580095L;

		public CheckListRenderer()
		{
			setBackground(UIManager.getColor("List.textBackground"));
			setForeground(UIManager.getColor("List.textForeground"));
		}

		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean hasFocus)
		{
			setEnabled(list.isEnabled());
			setSelected(((CheckableItem) value).isSelected());
			setFont(list.getFont());
			setText(value.toString());
			return this;
		}
	}

	private static final long serialVersionUID = 2174601193484988913L;
	private boolean accepted = false;
	private final JPanel contentPanel = new JPanel();

	Vector<CheckableItem> items = new Vector<OverridesAndImplementationsDialog.CheckableItem>();

	private final SimpleEntity parent, child;

	/**
	 * Create the dialog.
	 */
	public OverridesAndImplementationsDialog(
	    SimpleEntity parent, SimpleEntity child) {
	  Utility.setRootPaneActionOnEsc(getRootPane(), new AbstractAction() {
	    
      private static final long serialVersionUID = -9137055482704631902L;

      public void actionPerformed(ActionEvent e)
            {
              setVisible(false);
            }
    });
	  
		this.parent = parent;
		this.child = child;
		setTitle("Overrides & Implementations");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 244, 332);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		final GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[] { 0, 0 };
		gbl_contentPanel.rowHeights = new int[] { 0, 0 };
		gbl_contentPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPanel.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPanel.setLayout(gbl_contentPanel);
		{
			final JScrollPane scrollPane = new JScrollPane();
			final GridBagConstraints gbc_scrollPane = new GridBagConstraints();
			gbc_scrollPane.fill = GridBagConstraints.BOTH;
			gbc_scrollPane.gridx = 0;
			gbc_scrollPane.gridy = 0;
			contentPanel.add(scrollPane, gbc_scrollPane);
			{
				final JList<?> list = new JList<Object>(createData());
				scrollPane.setViewportView(list);
				list.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
				list.setCellRenderer(new CheckListRenderer());
				list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				list.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(MouseEvent e)
					{
						final int index = list.locationToIndex(e.getPoint());
						final CheckableItem item = (CheckableItem) list.getModel().getElementAt(index);
						item.setSelected(!item.isSelected());
						final Rectangle rect = list.getCellBounds(index, index);
						list.repaint(rect);
					}
				});
			}
		}
		{
			final JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				final JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						accepted = true;
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				final JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e)
					{
						accepted = false;
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}

		setLocationRelativeTo(Slyum.getInstance());
		setVisible(true);
	}

	private Vector<CheckableItem> createData()
	{
		items = new Vector<CheckableItem>();

		for (final SimpleEntity e : parent.getAllParents())

			out: for (final Method m : e.getMethods())
			{
				for (final CheckableItem c : items)
					if (c.getMethod().equals(m))
						continue out;

				if (m.isStatic())
					continue;
				
				final CheckableItem ci = new CheckableItem(m);
				ci.setSelected(child.getMethods().contains(m));
				items.add(ci);
			}

		return items;
	}

	public Vector<CheckableItem> getCheckableItems()
	{
		return items;
	}

	public boolean isAccepted()
	{
		return accepted;
	}

}
