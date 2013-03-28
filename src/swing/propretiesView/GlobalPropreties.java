package swing.propretiesView;

import java.awt.SystemColor;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import classDiagram.IDiagramComponent.UpdateMessage;

/**
 * This is a JPanel that is notified when an UML component is selected. When
 * notifying, the JPanel show this propreties according to its childs.
 * 
 * @author David Miserez
 * @version 1.0 - 28.07.2011
 */
public abstract class GlobalPropreties extends JPanel implements Observer {
	protected Object currentObject = null;

	public GlobalPropreties()
	{
		setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		setBackground(SystemColor.control);
	}

	/**
	 * Display the JPanel in the propreties view.
	 */
	public void showInProperties()
	{
		PropretiesChanger.getInstance().setViewportView(this);
	}

	@Override
	public void update(Observable arg0, Object arg1)
	{
		if (arg1 != null && arg1 instanceof UpdateMessage)
			switch ((UpdateMessage) arg1)
			{
				case SELECT:
					currentObject = arg0;
					updateComponentInformations((UpdateMessage) arg1);
					showInProperties();

					break;
				case UNSELECT:
					if (arg0.equals(currentObject))
						PropretiesChanger.getInstance().setViewportView(null);
					updateComponentInformations((UpdateMessage) arg1);
					break;
				default:
					updateComponentInformations((UpdateMessage) arg1);
					break;
			}
		else
			updateComponentInformations(null);
	}

	public abstract void updateComponentInformations(UpdateMessage msg);
}
