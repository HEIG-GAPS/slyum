package swing.propretiesView;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import swing.slyumCustomizedComponents.FlatButton;
import swing.slyumCustomizedComponents.FlatPanel;
import swing.PanelClassDiagram;
import swing.Slyum;
import utility.PersonalizedIcon;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.InterfaceEntity;
import classDiagram.components.SimpleEntity;
import classDiagram.relationships.Inheritance;

public class InheritanceProperties extends GlobalPropreties implements ActionListener {
  public static final String ACTION_OI = "O&I";

  private static InheritanceProperties instance;

  private JLabel lblName, lblType;
  private JButton btnOI, btnAdjustInheritance;
  private ButtonChangeOrientation btnChangeOrientation;

  public static InheritanceProperties getInstance() {
    if (instance == null) instance = new InheritanceProperties();

    return instance;
  }

  public InheritanceProperties() {
    JPanel panel = new FlatPanel();

    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

    lblType = new JLabel("inheritanceType");
    panel.add(lblType);

    lblName = new JLabel("inheritanceName");
    panel.add(lblName);

    btnOI = new FlatButton("Overrides & Implementations...",
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "method.png"));
    btnOI.setToolTipText("Open Overrides & Implementations");
    btnOI.setActionCommand(ACTION_OI);
    btnOI.addActionListener(this);
    btnOI.setMaximumSize(new Dimension(250, 100));
    btnOI.setHorizontalAlignment(SwingUtilities.LEFT);

    btnAdjustInheritance = new FlatButton("Autopath",
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
                    + "adjust-inheritance.png"));
    btnAdjustInheritance.setActionCommand(Slyum.ACTION_ADJUST_INHERITANCE);
    btnAdjustInheritance.addActionListener(this);
    btnAdjustInheritance.setMaximumSize(new Dimension(250, 100));
    btnAdjustInheritance.setHorizontalAlignment(SwingUtilities.LEFT);

    panel.add(Box.createVerticalGlue());
    panel.add(btnChangeOrientation = new ButtonChangeOrientation());
    panel.add(Box.createVerticalStrut(5));
    panel.add(btnOI);
    panel.add(Box.createVerticalStrut(5));
    panel.add(btnAdjustInheritance);

    panel.setMaximumSize(new Dimension(250, Short.MAX_VALUE));
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
    add(panel);
  }

  @Override
  public void updateComponentInformations(UpdateMessage msg) {
    if (currentObject != null) {
      Inheritance i = (Inheritance) currentObject;
      SimpleEntity parent = i.getParent();
      String lblTypeText = "generalize";

      if (parent.getClass() == InterfaceEntity.class) lblTypeText = "realize";

      lblType.setText(lblTypeText);

      lblName.setText(i.getChild().getName() + " -> " + parent.getName());
      btnOI.setEnabled(!parent.isEveryMethodsStatic());
      btnChangeOrientation.changeActionListener(PanelClassDiagram.getInstance()
              .getCurrentGraphicView().searchAssociedComponent(currentObject));
    }
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Inheritance i = (Inheritance) currentObject;
    if (ACTION_OI.equals(e.getActionCommand()))
      i.showOverridesAndImplementations();
    else if (Slyum.ACTION_ADJUST_INHERITANCE.equals(e.getActionCommand()))
      PanelClassDiagram.getInstance().getCurrentGraphicView()
              .adjustInheritances();
  }
}
