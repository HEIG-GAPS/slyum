package swing.propretiesView;

import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.Method;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import swing.PanelClassDiagram;
import swing.slyumCustomizedComponents.SCheckBox;
import swing.slyumCustomizedComponents.SComboBox;

public class DiagramPropreties 
    extends GlobalPropreties 
    implements ActionListener {

  private static DiagramPropreties instance;

  public static DiagramPropreties getInstance() {
    if (instance == null) instance = new DiagramPropreties();
    return instance;
  }

  public static void updateComponentInformations() {
    instance.updateComponentInformations(null);
  }

  JPanel west = createJPanelInformations();
  
  private final String ACTION_ENTITY_VIEW = "1",
                       ACTION_METHODS_VIEW = "2",
                       ACTION_VISIBLE_TYPE = "3",
                       ACTION_VISIBLE_ENUM = "4";
  
  private final SComboBox<ClassDiagram.ViewEntity> cbbEntityView;
  private final SComboBox<Method.ParametersViewStyle> cbbParametersView;
  private final SCheckBox chkDisplayTypes;
  private final SCheckBox chkViewEnum;
  private boolean raiseEvent;

  private DiagramPropreties() {
    
    final Dimension CCB_DIMENSION = new Dimension(130, 25);
    final int HEIGHT_STRUT = 5;
    
    PanelClassDiagram.getInstance().getClassDiagram().addObserver(this);
    
    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);

    // Informations générales
    west.add(new JLabel("Entities view types"));
    cbbEntityView = 
        new SComboBox<>(ClassDiagram.ViewEntity.values());
    cbbEntityView.setMaximumSize(CCB_DIMENSION);
    cbbEntityView.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbbEntityView.setActionCommand(ACTION_ENTITY_VIEW);
    cbbEntityView.addActionListener(this);
    west.add(cbbEntityView);
    west.add(Box.createVerticalStrut(HEIGHT_STRUT));
    
    west.add(new JLabel("Methods view type"));
    cbbParametersView = 
        new SComboBox<>(Method.ParametersViewStyle.values());
    cbbParametersView.removeItemAt(0);
    cbbParametersView.setMaximumSize(CCB_DIMENSION);
    cbbParametersView.setAlignmentX(Component.LEFT_ALIGNMENT);
    cbbParametersView.setActionCommand(ACTION_METHODS_VIEW);
    cbbParametersView.addActionListener(this);
    west.add(cbbParametersView);
    west.add(Box.createVerticalStrut(HEIGHT_STRUT));
    
    chkViewEnum = new SCheckBox("View enum values");
    chkViewEnum.setAlignmentX(Component.LEFT_ALIGNMENT);
    chkViewEnum.setBackground(Color.WHITE);
    chkViewEnum.setActionCommand(ACTION_VISIBLE_ENUM);
    chkViewEnum.addActionListener(this);
    west.add(chkViewEnum);
    west.add(Box.createVerticalStrut(HEIGHT_STRUT-5));
    
    chkDisplayTypes = new SCheckBox("Display types");
    chkDisplayTypes.setAlignmentX(Component.LEFT_ALIGNMENT);
    chkDisplayTypes.setBackground(Color.WHITE);
    chkDisplayTypes.setActionCommand(ACTION_VISIBLE_TYPE);
    chkDisplayTypes.addActionListener(this);
    west.add(chkDisplayTypes);
    
    JPanel pnlDiagramProperties = new JPanel();
    pnlDiagramProperties.setLayout(
        new BoxLayout(pnlDiagramProperties, BoxLayout.Y_AXIS));
    pnlDiagramProperties.setMaximumSize(new Dimension(140, Short.MAX_VALUE));
    
    JLabel lblTitle = new JLabel("Diagram's properties");
    lblTitle.setHorizontalTextPosition(JLabel.LEFT);
    lblTitle.setVerticalTextPosition(JLabel.BOTTOM);
    lblTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 0));
    
    pnlDiagramProperties.add(lblTitle);
    pnlDiagramProperties.add(west);
    
    
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    add(pnlDiagramProperties);
    add(Box.createHorizontalGlue());
    add(new JLabel("Select a component to see it's members"));
    add(Box.createHorizontalGlue());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (!raiseEvent)
      return;
    
    PanelClassDiagram p = PanelClassDiagram.getInstance();
    if (p == null) return;
    ClassDiagram cd = p.getClassDiagram();
    if (cd == null) return;
    
    switch (e.getActionCommand()) {
      case ACTION_ENTITY_VIEW:
        cd.setViewEntity(
            (ClassDiagram.ViewEntity) cbbEntityView.getSelectedItem());
    
        cd.notifyObservers(true);
        break;
      case ACTION_METHODS_VIEW:
        cd.setDefaultViewMethods(
            (Method.ParametersViewStyle) cbbParametersView.getSelectedItem());
    
    cd.notifyObservers();
        break;
      case ACTION_VISIBLE_ENUM:
        cd.setDefaultViewEnum(chkViewEnum.isSelected());
    
    cd.notifyObservers();
        break;
      case ACTION_VISIBLE_TYPE:
        cd.setVisibleType(chkDisplayTypes.isSelected());
    
    cd.notifyObservers();
        break;
    }
  }

  private JPanel createJPanelInformations() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    panel.setBackground(Color.WHITE);

    return panel;
  }
 
 @Override
  public void updateComponentInformations(UpdateMessage msg) {
    PanelClassDiagram panel = PanelClassDiagram.getInstance();
    if (panel == null) {
      west.setVisible(false);
      return;
    }
    
    ClassDiagram classDiagram = panel.getClassDiagram();
    if (classDiagram == null) return;
    
    west.setVisible(true);
    
    raiseEvent = false;
    cbbEntityView.setSelectedItem(classDiagram.getDefaultViewEntities());
    cbbParametersView.setSelectedItem(classDiagram.getDefaultViewMethods());
    chkDisplayTypes.setSelected(classDiagram.getDefaultVisibleTypes());
    chkViewEnum.setSelected(classDiagram.getDefaultViewEnum());
    raiseEvent = true;
  }
}
