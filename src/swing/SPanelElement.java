package swing;

import graphic.GraphicComponent;
import graphic.GraphicView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import utility.PersonalizedIcon;

public class SPanelElement extends JPanelRounded implements ActionListener, IListenerComponentSelectionChanged
{
	private static final long serialVersionUID = 143967533553175219L;
	
	private static final String TT_ADD_NOTE = "Link a note"/* + Utility.keystrokeToString(Slyum.KEY_UNDO)*/;
	private static final String TT_CHANGE_COLOR = "Color"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;
	private static final String TT_DELETE = "Delete"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;
	
	private SButton btnDelete;
	
	private static SPanelElement instance;

	public static SPanelElement getInstance()
	{
		if (instance == null)
			instance = new SPanelElement();
		
		return instance;
	}

	private SPanelElement()
	{
		setLayout(new GridLayout(1, 3, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(0, 255, 150, 10));
		setForeground(Color.GRAY);

		
		add(createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "note.png"), Slyum.ACTION_NEW_NOTE_ASSOCIED, Color.CYAN, TT_ADD_NOTE, true));
		add(createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "color16.png"), "Color", Color.CYAN, TT_CHANGE_COLOR, true));
		btnDelete = createSButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "delete16.png"), "Delete", Color.CYAN, TT_DELETE, false);
		add(btnDelete);
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
	
	private SButton createSButton(ImageIcon ii, String a, Color c, String tt, boolean enable)
	{
		SButton sb = new SButton(ii, a, c, tt, this);
		sb.setEnabled(enable);
		return sb;
	}

	public void componentSelectionChanged()
	{
		btnDelete.setEnabled(PanelClassDiagram.getInstance().getCurrentGraphicView().countSelectedComponents() > 0);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
		
		if (e.getActionCommand().equals(Slyum.ACTION_DELETE))
			
			gv.deleteSelectedComponents();
		
		else if (e.getActionCommand().equals(Slyum.ACTION_COLOR))
			
			GraphicComponent.askNewColorForSelectedItems();
		
		else if (e.getActionCommand().equals(Slyum.ACTION_NEW_NOTE_ASSOCIED))
			
			gv.linkNewNoteWithSelectedEntities();
	}
	
	public SButton getBtnDelete()
	{
		return btnDelete;
	}
}
