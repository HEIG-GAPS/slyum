package swing;

import graphic.GraphicView;
import graphic.factory.AggregationFactory;
import graphic.factory.AssociationClassFactory;
import graphic.factory.BinaryFactory;
import graphic.factory.ClassFactory;
import graphic.factory.CompositionFactory;
import graphic.factory.DependencyFactory;
import graphic.factory.EnumFactory;
import graphic.factory.InheritanceFactory;
import graphic.factory.InnerClassFactory;
import graphic.factory.InterfaceFactory;
import graphic.factory.LineCommentaryFactory;
import graphic.factory.MultiFactory;
import graphic.factory.NoteFactory;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelDiagramComponent 
    extends SToolBar implements ActionListener {

  public enum Mode {
    CURSOR(getInstance().btnCursorMode), GRIP(getInstance().btnGripMode);

    private SButton btnMode;

    private Mode(SButton btn) {
      btnMode = btn;
    }

    public SButton getBtnMode() {
      return btnMode;
    }
  }

  private static final String TT_MODE_CURSOR = "Default cursor "
          + Utility.keystrokeToString(Slyum.KEY_DEFAULT_MODE);

  private static final String TT_MODE_GRIP = "Add grips "
          + Utility.keystrokeToString(Slyum.KEY_GRIPS_MODE);

  private static final String TT_CLASS = "Class "
          + Utility.keystrokeToString(Slyum.KEY_CLASS);

  private static final String TT_ENUM = "Enum "
          + Utility.keystrokeToString(Slyum.KEY_ENUM);

  private static final String TT_INTERFACE = "Interface "
          + Utility.keystrokeToString(Slyum.KEY_INTERFACE);

  private static final String TT_CLASS_ASSOC = "Association class "
          + Utility.keystrokeToString(Slyum.KEY_ASSOCIATION_CLASS);

  private static final String TT_GENERALIZE = "Generalize & Realize "
          + Utility.keystrokeToString(Slyum.KEY_INHERITANCE);

  private static final String TT_DEPENDENCY = "Dependency "
          + Utility.keystrokeToString(Slyum.KEY_DEPENDENCY);

  private static final String TT_INNER_CLASS = "Inner class "
          + Utility.keystrokeToString(Slyum.KEY_INNER_CLASS);

  private static final String TT_ASSOCIATION = "Association "
          + Utility.keystrokeToString(Slyum.KEY_ASSOCIATION);

  private static final String TT_AGGREGATION = "Aggregation "
          + Utility.keystrokeToString(Slyum.KEY_AGGREGATION);

  private static final String TT_COMPOSITION = "Composition "
          + Utility.keystrokeToString(Slyum.KEY_COMPOSITION);

  private static final String TT_MULTI = "Multi-association "
          + Utility.keystrokeToString(Slyum.KEY_MULTI_ASSOCIATION);

  private static final String TT_NOTE = "Note "
          + Utility.keystrokeToString(Slyum.KEY_NOTE);

  private static final String TT_LINK_NOTE = "Link note "
          + Utility.keystrokeToString(Slyum.KEY_LINK_NOTE);

  private SButton btnCursorMode, btnGripMode, btnClass, btnEnum, btnInterface,
          btnClassAssociation, btnGeneralize, btnDependeny, btnInnerClass,
          btnAssociation, btnAggregation, btnComposition, btnMulti, btnNote,
          btnLinkNote;

  private Mode currentMode;
  private static SPanelDiagramComponent instance;

  public static SPanelDiagramComponent getInstance() {
    if (instance == null) instance = new SPanelDiagramComponent();
    return instance;
  }

  private SPanelDiagramComponent() {

    add(btnCursorMode = new SToolBarButton(SlyumAction.ACTION_MODE_CURSOR));

    add(btnGripMode = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
                    + "pointer-grip.png"), Slyum.ACTION_MODE_GRIP, Color.RED,
            TT_MODE_GRIP));

    add(new SSeparator());

    add(btnClass = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "class.png"),
            Slyum.ACTION_NEW_CLASS, Color.RED, TT_CLASS));

    add(btnInterface = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "interface.png"),
            Slyum.ACTION_NEW_INTERFACE, Color.RED, TT_INTERFACE));

    add(btnEnum = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "enum.png"),
            Slyum.ACTION_NEW_ENUM, Color.RED, TT_ENUM));

    add(btnClassAssociation = createSButton(
            PersonalizedIcon
                    .createImageIcon(Slyum.ICON_PATH + "classAssoc.png"),
            Slyum.ACTION_NEW_CLASS_ASSOCIATION, Color.RED, TT_CLASS_ASSOC));

    add(new SSeparator());

    add(btnGeneralize = createSButton(
            PersonalizedIcon
                    .createImageIcon(Slyum.ICON_PATH + "generalize.png"),
            Slyum.ACTION_NEW_GENERALIZE, Color.RED, TT_GENERALIZE));

    add(btnDependeny = createSButton(
            PersonalizedIcon
                    .createImageIcon(Slyum.ICON_PATH + "dependency.png"),
            Slyum.ACTION_NEW_DEPENDENCY, Color.RED, TT_DEPENDENCY));

    add(btnInnerClass = createSButton(
            PersonalizedIcon
                    .createImageIcon(Slyum.ICON_PATH + "innerClass.png"),
            Slyum.ACTION_NEW_INNER_CLASS, Color.RED, TT_INNER_CLASS));

    add(new SSeparator());

    add(btnAssociation = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
                    + "association.png"), Slyum.ACTION_NEW_ASSOCIATION,
            Color.RED, TT_ASSOCIATION));

    add(btnAggregation = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
                    + "aggregation.png"), Slyum.ACTION_NEW_AGGREGATION,
            Color.RED, TT_AGGREGATION));

    add(btnComposition = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH
                    + "composition.png"), Slyum.ACTION_NEW_COMPOSITION,
            Color.RED, TT_COMPOSITION));

    add(btnMulti = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "multi.png"),
            Slyum.ACTION_NEW_MULTI, Color.RED, TT_MULTI));

    add(new SSeparator());

    add(btnNote = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "note.png"),
            Slyum.ACTION_NEW_NOTE, Color.RED, TT_NOTE));

    add(btnLinkNote = createSButton(
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "linkNote.png"),
            Slyum.ACTION_NEW_LINK_NOTE, Color.RED, TT_LINK_NOTE));
  }

  public void setButtonModeStyle(SButton button) {
    // Réinitialise le style de tous les boutons.
    synchronized (getTreeLock()) {
      for (Component c : getComponents())
        if (c instanceof SButton) ((SButton) c).resetBackground();
    }

    // Attribut le nouveau bouton définissant le mode.
    button.setBackground(Slyum.THEME_COLOR.brighter());
    button.setContentAreaFilled(true);
  }

  private SButton createSButton(ImageIcon ii, String a, Color c, String tt) {
    return new SToolBarButton(ii, a, c, tt, this);
  }

  public Mode getMode() {
    return currentMode;
  }

  public void setMode(Mode newMode) {
    currentMode = newMode;
    PropertyLoader.getInstance().getProperties()
            .put(PropertyLoader.MODE_CURSOR, currentMode.toString());
    PropertyLoader.getInstance().push();
    PanelClassDiagram.getInstance().getCurrentGraphicView()
            .deleteCurrentFactory();
    setButtonModeStyle(newMode.getBtnMode());
  }

  public void applyMode() {
    setButtonModeStyle(currentMode.getBtnMode());
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();

    if (Slyum.ACTION_NEW_CLASS.equals(e.getActionCommand()))
      gv.initNewComponent(new ClassFactory(gv));

    else if (Slyum.ACTION_NEW_INTERFACE.equals(e.getActionCommand()))
      gv.initNewComponent(new InterfaceFactory(gv));

    else if (Slyum.ACTION_NEW_ENUM.equals(e.getActionCommand()))
      gv.initNewComponent(new EnumFactory(gv));

    else if (Slyum.ACTION_NEW_GENERALIZE.equals(e.getActionCommand()))
      gv.initNewComponent(new InheritanceFactory(gv));

    else if (Slyum.ACTION_NEW_INNER_CLASS.equals(e.getActionCommand()))
      gv.initNewComponent(new InnerClassFactory(gv));

    else if (Slyum.ACTION_NEW_DEPENDENCY.equals(e.getActionCommand()))
      gv.initNewComponent(new DependencyFactory(gv));

    else if (Slyum.ACTION_NEW_ASSOCIATION.equals(e.getActionCommand()))
      gv.initNewComponent(new BinaryFactory(gv));

    else if (Slyum.ACTION_NEW_AGGREGATION.equals(e.getActionCommand()))
      gv.initNewComponent(new AggregationFactory(gv));

    else if (Slyum.ACTION_NEW_COMPOSITION.equals(e.getActionCommand()))
      gv.initNewComponent(new CompositionFactory(gv));

    else if (Slyum.ACTION_NEW_CLASS_ASSOCIATION.equals(e.getActionCommand()))
      gv.initNewComponent(new AssociationClassFactory(gv));

    else if (Slyum.ACTION_NEW_MULTI.equals(e.getActionCommand()))
      gv.initNewComponent(new MultiFactory(gv));

    else if (Slyum.ACTION_NEW_NOTE.equals(e.getActionCommand()))
      gv.initNewComponent(new NoteFactory(gv));

    else if (Slyum.ACTION_NEW_LINK_NOTE.equals(e.getActionCommand()))
      gv.initNewComponent(new LineCommentaryFactory(gv));

    else if (Slyum.ACTION_MODE_GRIP.equals(e.getActionCommand()))
      setMode(Mode.GRIP);
  }

  public SButton getBtnClass() {
    return btnClass;
  }

  public SButton getBtnInterface() {
    return btnInterface;
  }

  public SButton getBtnClassAssociation() {
    return btnClassAssociation;
  }

  public SButton getBtnGeneralize() {
    return btnGeneralize;
  }

  public SButton getBtnDependency() {
    return btnDependeny;
  }

  public SButton getBtnInnerClass() {
    return btnInnerClass;
  }

  public SButton getBtnAssociation() {
    return btnAssociation;
  }

  public SButton getBtnAggregation() {
    return btnAggregation;
  }

  public SButton getBtnComposition() {
    return btnComposition;
  }

  public SButton getBtnMulti() {
    return btnMulti;
  }

  public SButton getBtnNote() {
    return btnNote;
  }

  public SButton getBtnLinkNote() {
    return btnLinkNote;
  }

  public SButton getBtnEnum() {
    return btnEnum;
  }
}
