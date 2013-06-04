package swing.propretiesView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import swing.FlatPanel;
import swing.PanelClassDiagram;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Association;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Role;

/**
 * Show the propreties of an association and its roles with Swing components.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class RelationPropreties extends GlobalPropreties
{
	private static RelationPropreties instance = new RelationPropreties();

	/**
	 * Get the unique instance of this class.
	 * 
	 * @return the unique instance of RelationPropreties
	 */
	public static RelationPropreties getInstance() {
		return instance;
	}

	private JCheckBox chckbxDirect;
	private JPanel pnlRoles;
	private JTextField textFieldLabel;
	private ButtonChangeOrientation btnChangeOrientation;

	/**
	 * Create the panel.
	 */
	public RelationPropreties() {
	  JPanel pnlGeneral = new FlatPanel();
	  
	  // Initialization
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		
		// Panel général
    textFieldLabel = new JTextField();
    textFieldLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
    textFieldLabel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (currentObject != null)
          if (currentObject instanceof Association) {
            ((Association) currentObject).setLabel(textFieldLabel.getText());
            ((Association) currentObject).notifyObservers();
          } else if (currentObject instanceof Dependency) {
            ((Dependency) currentObject).setLabel(textFieldLabel.getText());
            ((Dependency) currentObject).notifyObservers();
          }
      }
    });

    chckbxDirect = new JCheckBox("Directed");
    chckbxDirect.setBackground(null);
    chckbxDirect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (currentObject != null && currentObject instanceof Association) {
          ((Association) currentObject).setDirected(chckbxDirect.isSelected());
          ((Association) currentObject).notifyObservers();
        }
      }
    });
    
    pnlGeneral.setLayout(new BoxLayout(pnlGeneral, BoxLayout.PAGE_AXIS));
    pnlGeneral.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));
    pnlGeneral.add(textFieldLabel);
    pnlGeneral.add(chckbxDirect);
    pnlGeneral.add(Box.createVerticalGlue());
    pnlGeneral.add(btnChangeOrientation = new ButtonChangeOrientation());

    // Panel roles & ScrollPane
    pnlRoles = new JPanel();
    pnlRoles.setLayout(new BoxLayout(pnlRoles, BoxLayout.LINE_AXIS));
    pnlRoles.setBackground(null);
    pnlRoles.setBorder(null);
    
    add(pnlGeneral);
    add(Box.createHorizontalStrut(5));
    add(pnlRoles);
	}

	@Override
	public void updateComponentInformations(UpdateMessage msg)
	{
		if (currentObject != null)
			if (currentObject instanceof Association) {
				final Association association = (Association) currentObject;

				if (msg != null && msg.equals(UpdateMessage.UNSELECT)) {
					association.setName(textFieldLabel.getText());
					association.notifyObservers();

					for (final Component c : pnlRoles.getComponents())
					  if (c instanceof SlyumRolePanel)
					    ((SlyumRolePanel) c).confirm();
				}

				chckbxDirect.setEnabled(true);
				chckbxDirect.setSelected(association.isDirected());
				textFieldLabel.setText(association.getLabel());

				if (pnlRoles.getComponentCount() == 0 || 
				    msg == UpdateMessage.SELECT) {
					for (final Component c : pnlRoles.getComponents()) {
					  if (c instanceof SlyumRolePanel)
					    ((SlyumRolePanel) c).stopObserving();
						pnlRoles.removeAll();
					}

					for (final Role role : association.getRoles()) {
						pnlRoles.add(new SlyumRolePanel(role));
						pnlRoles.add(Box.createHorizontalStrut(5));
					}
				}
			} else if (currentObject instanceof Dependency) {
				for (final Component c : pnlRoles.getComponents()) {
				  if (c instanceof SlyumRolePanel)
				    ((SlyumRolePanel) c).stopObserving();
					pnlRoles.removeAll();
				}

				Dependency dependency = (Dependency) currentObject;

				if (msg != null && msg.equals(UpdateMessage.UNSELECT)) {
					dependency.setLabel(textFieldLabel.getText());
					dependency.notifyObservers();
				}

				chckbxDirect.setEnabled(false);
				textFieldLabel.setText(dependency.getLabel());
			}
      btnChangeOrientation.changeActionListener(
          PanelClassDiagram.getInstance().getCurrentGraphicView()
              .searchAssociedComponent(currentObject));
	}
}
