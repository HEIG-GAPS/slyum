package swing.propretiesView;

import graphic.GraphicView;
import graphic.entity.AssociationClassView;
import graphic.entity.ClassView;
import graphic.entity.EntityView;
import graphic.entity.InterfaceView;
import graphic.relations.AggregationView;
import graphic.relations.AssociationView;
import graphic.relations.BinaryView;
import graphic.relations.CompositionView;
import graphic.relations.InheritanceView;
import graphic.relations.InnerClassView;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle;

import swing.FlatButton;
import swing.PanelClassDiagram;
import swing.Slyum;
import utility.PersonalizedIcon;
import utility.SMessageDialog;
import classDiagram.ClassDiagram;
import classDiagram.IDiagramComponent.UpdateMessage;
import classDiagram.components.AssociationClass;
import classDiagram.components.ClassEntity;
import classDiagram.components.Entity;
import classDiagram.components.InterfaceEntity;
import classDiagram.relationships.Aggregation;
import classDiagram.relationships.Association;
import classDiagram.relationships.Binary;
import classDiagram.relationships.Composition;
import classDiagram.relationships.Inheritance;
import classDiagram.relationships.InnerClass;

public class DiagramPropreties extends GlobalPropreties {

  private static DiagramPropreties instance;

  public static DiagramPropreties getInstance() {
    if (instance == null) instance = new DiagramPropreties();
    return instance;
  }

  public static void updateComponentInformations() {
    instance.updateComponentInformations(null);
  }

  private JLabel lblFileName = new JLabel(),
          lblFileAbsolutePath = new JLabel();

  private JButton openInExplorer;

  private JTextArea areaSelection, areaDefault;

  JPanel west = createJPanelInformations(),
          center = createJPanelInformations(),
          east = createJPanelInformations();

  private DiagramPropreties() {
    GroupLayout layout = new GroupLayout(this);
    setLayout(layout);

    // Informations générales
    west.add(lblFileName);
    west.add(lblFileAbsolutePath);
    openInExplorer = new FlatButton("Open in explorer",
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "explore.png"));

    openInExplorer.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        try {
          Desktop.getDesktop().open(
                  PanelClassDiagram.getFileOpen().getParentFile());
        } catch (IOException e1) {
          SMessageDialog.showErrorMessage("No open file!");
        }
      }
    });
    west.add(openInExplorer);

    // Statistiques
    areaDefault = new JTextArea();
    areaDefault.setEditable(false);
    center.add(areaDefault);

    areaSelection = new JTextArea();
    areaSelection.setEditable(false);
    east.add(areaSelection);

    layout.setHorizontalGroup(layout.createParallelGroup(
            GroupLayout.Alignment.LEADING).addGroup(
            layout.createSequentialGroup()
                    .addComponent(west, GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(center, GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(east, GroupLayout.DEFAULT_SIZE,
                            GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()));

    layout.setVerticalGroup(layout
            .createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(west, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(center, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(east, GroupLayout.DEFAULT_SIZE,
                    GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
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

    if (panel == null) return;

    File fileOpen = PanelClassDiagram.getFileOpen();
    GraphicView gv = panel.getCurrentGraphicView();
    ClassDiagram cd = gv.getClassDiagram();

    if (fileOpen != null) {
      west.setVisible(true);
      lblFileName.setText(String.format("Name: %s", fileOpen.getName()
              .replaceAll(Slyum.FULL_EXTENTION, "")));

      lblFileAbsolutePath.setText(String.format("Path: %s",
              fileOpen.getAbsolutePath()));
    } else {
      west.setVisible(false);
    }

    String statistics = "Entity: %s\n" + "  Class: %s\n" + "  Interface: %s\n"
            + "  Association class: %s\n" + "Inheritence: %s\n"
            + "Inner class: %s\n" + "Association: %s\n" + "  Binary: %s\n"
            + "  Aggregation: %s\n" + "  Composition: %s\n" + "Notes: %s\n";

    String regexRemoveLineWithZero = ".*0.*(\r?\n|\r)?";

    String textDefault = String.format(statistics,
            cd.countComponents(Entity.class),
            cd.countComponents(ClassEntity.class),
            cd.countComponents(InterfaceEntity.class),
            cd.countComponents(AssociationClass.class),
            cd.countComponents(Inheritance.class),
            cd.countComponents(InnerClass.class),
            cd.countComponents(Association.class),
            cd.countComponents(Binary.class),
            cd.countComponents(Aggregation.class),
            cd.countComponents(Composition.class), gv.countNotes()).replaceAll(
            regexRemoveLineWithZero, "");

    String textSelection = String.format(statistics,
            gv.countSelectedComponents(EntityView.class),
            gv.countSelectedComponents(ClassView.class),
            gv.countSelectedComponents(InterfaceView.class),
            gv.countSelectedComponents(AssociationClassView.class),
            gv.countSelectedComponents(InheritanceView.class),
            gv.countSelectedComponents(InnerClassView.class),
            gv.countSelectedComponents(AssociationView.class),
            gv.countSelectedComponents(BinaryView.class),
            gv.countSelectedComponents(AggregationView.class),
            gv.countSelectedComponents(CompositionView.class),
            gv.countSelectedNotes()).replaceAll(regexRemoveLineWithZero, "");

    if (!textDefault.isEmpty()) {
      areaDefault.setText(String.format("Total\n%s", textDefault));
      center.setVisible(true);
    } else {
      center.setVisible(false);
    }

    if (!textSelection.isEmpty()) {
      areaSelection.setText(String.format("Sélection\n%s", textSelection));
      east.setVisible(true);
    } else {
      east.setVisible(false);
    }
  }
}
