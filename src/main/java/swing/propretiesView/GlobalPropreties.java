package swing.propretiesView;

import classDiagram.IDiagramComponent.UpdateMessage;
import graphic.relations.RelationView;
import swing.PanelClassDiagram;
import swing.Slyum;
import swing.slyumCustomizedComponents.FlatButton;
import utility.PersonalizedIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * This is a JPanel that is notified when an UML component is selected. When notifying, the JPanel show this propreties
 * according to its childs.
 *
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public abstract class GlobalPropreties extends JPanel implements Observer {

  class ButtonChangeOrientation extends FlatButton {

    public ButtonChangeOrientation() {
      super("Change orientation", PersonalizedIcon
          .createImageIcon("orientation.png"));
      setActionCommand(RelationView.ACTION_CHANGE_ORIENTATION);
      setMaximumSize(new Dimension(250, 100));
      setHorizontalAlignment(SwingUtilities.LEFT);
    }

    public void changeActionListener(ActionListener listener) {
      for (ActionListener l : getActionListeners())
        removeActionListener(l);
      addActionListener(listener);
    }

  }

  protected Object currentObject = null;

  public GlobalPropreties() {
    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
    setBackground(SystemColor.control);
  }

  /**
   * Display the JPanel in the propreties view.
   */
  public void showInProperties() {
    PropretiesChanger.getInstance().setViewportView(this);
  }

  @Override
  public void update(Observable observable, Object object) {
    if (PanelClassDiagram.getInstance().isDisabledUpdate())
      return;

    if (object != null && object instanceof UpdateMessage)
      switch ((UpdateMessage) object) {
        case SELECT:
          currentObject = observable;
          updateComponentInformations((UpdateMessage) object);
          showInProperties();

          break;
        case UNSELECT:
          if (observable == currentObject)
            PropretiesChanger.getInstance().setViewportView(null);
          updateComponentInformations((UpdateMessage) object);
          break;
        default:
          updateComponentInformations((UpdateMessage) object);
          break;
      }
    else
      updateComponentInformations(null);
  }

  public abstract void updateComponentInformations(UpdateMessage msg);

}
