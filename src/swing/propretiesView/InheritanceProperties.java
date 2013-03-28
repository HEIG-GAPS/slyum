package swing.propretiesView;

import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.relationships.Inheritance;

import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import swing.JPanelRounded;
import swing.Slyum;
import utility.PersonalizedIcon;

import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InheritanceProperties extends GlobalPropreties implements ActionListener
{
	public static final String ACTION_OI = "O&I";

	private static InheritanceProperties instance;
	
	private JLabel lblName, lblType;
	private JButton btnOI;
	
	public static InheritanceProperties getInstance()
	{
		if (instance == null)
			instance = new InheritanceProperties();
		
		return instance;
	}
	
	public InheritanceProperties()
	{
		setBackground(Color.WHITE);		
		setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		JPanelRounded panel = new JPanelRounded();
		panel.setBorder(new EmptyBorder(20, 20, 20, 20));
		panel.setBounds(0, 0, 136, 272);
		add(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{0, 0};
		gbl_panel.rowHeights = new int[]{0, 10, 0, 0};
		gbl_panel.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{0.0, 1.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblType = new JLabel("inheritanceType");
		lblType.setFont(lblType.getFont().deriveFont(16.0f));
		GridBagConstraints gbc_lblInheritancetype = new GridBagConstraints();
		gbc_lblInheritancetype.insets = new Insets(0, 0, 5, 0);
		gbc_lblInheritancetype.gridx = 0;
		gbc_lblInheritancetype.gridy = 0;
		panel.add(lblType, gbc_lblInheritancetype);
		
		lblName = new JLabel("inheritanceName");
		lblName.setFont(lblName.getFont().deriveFont(13.0f));
		
		GridBagConstraints gbc_lblInheritancename = new GridBagConstraints();
		gbc_lblInheritancename.insets = new Insets(0, 0, 5, 0);
		gbc_lblInheritancename.gridx = 0;
		gbc_lblInheritancename.gridy = 1;
		panel.add(lblName, gbc_lblInheritancename);
		
		btnOI = new JButton("Overrides & Implementations...");
		btnOI.setActionCommand(ACTION_OI);
		btnOI.setToolTipText("Open Overrides & Implementations");
		btnOI.addActionListener(this);
		btnOI.setIcon(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "method.png"));
		GridBagConstraints gbc_btnOverridesImplementations = new GridBagConstraints();
		gbc_btnOverridesImplementations.gridx = 0;
		gbc_btnOverridesImplementations.gridy = 2;
		panel.add(btnOI, gbc_btnOverridesImplementations);
		
	}

	@Override
	public void updateComponentInformations(UpdateMessage msg)
	{
		if (currentObject != null)
		{
			Inheritance i = (Inheritance)currentObject;
			Entity parent = i.getParent();
			String lblTypeText = "Generalize";
			
			if (parent.getClass() == InterfaceEntity.class)
				lblTypeText = "Realize";
			
			lblType.setText(lblTypeText);
			
			lblName.setText(i.getChild().getName() + " - " + parent.getName());
			btnOI.setEnabled(!parent.isEveryMethodsStatic());
		}
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (ACTION_OI.equals(e.getActionCommand()))
			
			((Inheritance)currentObject).showOverridesAndImplementations();
	}
}
