package swing.propretiesView;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import swing.slyumCustomizedComponents.FlatPanel;
import swing.PanelClassDiagram;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.relationships.Association;
import classDiagram.relationships.Association.NavigateDirection;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Dependency;
import classDiagram.relationships.Role;
import swing.slyumCustomizedComponents.SRadioButton;
import swing.slyumCustomizedComponents.TextFieldWithPrompt;

/**
 * Show the propreties of an association and its roles with Swing components.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public class RelationPropreties extends GlobalPropreties {
  private static RelationPropreties instance = new RelationPropreties();

  /**
   * Get the unique instance of this class.
   * 
   * @return the unique instance of RelationPropreties
   */
  public static RelationPropreties getInstance() {
    return instance;
  }

  private ButtonGroup btnGrpNavigation;
  private JRadioButton radBidirectional, radFirstToSecond, radSecondToFirst;
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
    textFieldLabel = new TextFieldWithPrompt("", "Enter the relation's name");
    textFieldLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25));
    textFieldLabel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (currentObject != null) if (currentObject instanceof Association) {
          ((Association) currentObject).setLabel(textFieldLabel.getText());
          ((Association) currentObject).notifyObservers();
        } else if (currentObject instanceof Dependency) {
          ((Dependency) currentObject).setLabel(textFieldLabel.getText());
          ((Dependency) currentObject).notifyObservers();
        }
      }
    });

    radBidirectional = new SRadioButton();
    radBidirectional.setBackground(null);
    radBidirectional.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        setCurrentObjectDirected(NavigateDirection.BIDIRECTIONAL);
      }
    });

    radFirstToSecond = new SRadioButton();
    radFirstToSecond.setBackground(null);
    radFirstToSecond.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        setCurrentObjectDirected(NavigateDirection.FIRST_TO_SECOND);
      }
    });

    radSecondToFirst = new SRadioButton();
    radSecondToFirst.setBackground(null);
    radSecondToFirst.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent evt) {
        setCurrentObjectDirected(NavigateDirection.SECOND_TO_FIRST);
      }
    });

    btnGrpNavigation = new ButtonGroup();
    btnGrpNavigation.add(radBidirectional);
    btnGrpNavigation.add(radFirstToSecond);
    btnGrpNavigation.add(radSecondToFirst);

    pnlGeneral.setLayout(new BoxLayout(pnlGeneral, BoxLayout.PAGE_AXIS));
    pnlGeneral.setMaximumSize(new Dimension(250, Integer.MAX_VALUE));
    pnlGeneral.add(textFieldLabel);
    pnlGeneral.add(Box.createVerticalGlue());
    pnlGeneral.add(radBidirectional);
    pnlGeneral.add(radFirstToSecond);
    pnlGeneral.add(radSecondToFirst);
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

  private void setCurrentObjectDirected(NavigateDirection direction) {
    if (currentObject != null && currentObject instanceof Association) {
      ((Association) currentObject).setDirected(direction);
      ((Association) currentObject).notifyObservers();
    }
  }

  @Override
  public void updateComponentInformations(UpdateMessage msg) {
    if (currentObject != null) {
      if (currentObject instanceof Association) {
        final Association association = (Association) currentObject;

        if (msg != null && msg.equals(UpdateMessage.UNSELECT)) {
          association.setName(textFieldLabel.getText());
          association.notifyObservers();

          for (final Component c : pnlRoles.getComponents())
            if (c instanceof SlyumRolePanel) ((SlyumRolePanel) c).confirm();
        }

        switch (association.getDirected()) {
          case FIRST_TO_SECOND:
            btnGrpNavigation.setSelected(radFirstToSecond.getModel(), true);
            break;
          case SECOND_TO_FIRST:
            btnGrpNavigation.setSelected(radSecondToFirst.getModel(), true);
            break;
          case BIDIRECTIONAL:
            btnGrpNavigation.setSelected(radBidirectional.getModel(), true);
            break;
          default:
            break;
        }
        setMenuItemText();

        textFieldLabel.setText(association.getLabel());

        if (pnlRoles.getComponentCount() == 0 || msg == UpdateMessage.SELECT) {
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

        textFieldLabel.setText(dependency.getLabel());
      }
      btnChangeOrientation.changeActionListener(PanelClassDiagram.getInstance()
              .getCurrentGraphicView().searchAssociedComponent(currentObject));
      setVisibleNavigationBtn(currentObject instanceof Binary);
      btnChangeOrientation.setVisible(currentObject instanceof Binary
              || currentObject instanceof Dependency);
    }
  }

  private void setMenuItemText() {
    if (currentObject != null && currentObject instanceof Association) {
      String sourceName = ((Association) currentObject).getSource().getName(), targetName = ((Association) currentObject)
              .getTarget().getName();
      radBidirectional
              .setText("Bidirectional");
      radFirstToSecond.setText(String
              .format("%s -> %s", sourceName, targetName));
      radSecondToFirst.setText(String
              .format("%s -> %s", targetName, sourceName));
    }
  }

  private void setVisibleNavigationBtn(boolean visible) {
    radBidirectional.setVisible(visible);
    radFirstToSecond.setVisible(visible);
    radSecondToFirst.setVisible(visible);
    btnChangeOrientation.setVisible(visible);
  }
}
