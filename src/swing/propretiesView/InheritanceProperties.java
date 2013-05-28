package swing.propretiesView;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import swing.FlatButton;
import swing.FlatPanel;
import swing.Slyum;
import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.relationships.Inheritance;

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
	
	public InheritanceProperties() {
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		JPanel panel = new FlatPanel();
		
		lblType = new JLabel("inheritanceType");
		panel.add(lblType);
		
		lblName = new JLabel("inheritanceName");
		panel.add(lblName);
		
		btnOI = new FlatButton("Overrides & Implementations...");
		btnOI.setActionCommand(ACTION_OI);
		btnOI.setToolTipText("Open Overrides & Implementations");
		btnOI.addActionListener(this);
		btnOI.setIcon(
		    PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "method.png"));
		panel.add(Box.createVerticalGlue());
		panel.add(btnOI);

    panel.setMaximumSize(new Dimension(250, Short.MAX_VALUE));
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    add(panel);
	}

	@Override
	public void updateComponentInformations(UpdateMessage msg)
	{
		if (currentObject != null)
		{
			Inheritance i = (Inheritance)currentObject;
			Entity parent = i.getParent();
			String lblTypeText = "generalize";
			
			if (parent.getClass() == InterfaceEntity.class)
				lblTypeText = "realize";
			
			lblType.setText(lblTypeText);
			
			lblName.setText(i.getChild().getName() + " -> " + parent.getName());
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
