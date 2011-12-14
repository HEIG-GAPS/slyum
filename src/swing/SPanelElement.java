package swing;

import graphic.GraphicComponent;
import graphic.GraphicView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;

import utility.PersonalizedIcon;

public class SPanelElement extends JPanelRounded implements ActionListener
{
	private static final long serialVersionUID = 143967533553175219L;
	
	private static final String TT_ADD_NOTE = "Link a note"/* + Utility.keystrokeToString(Slyum.KEY_UNDO)*/;
	private static final String TT_CHANGE_COLOR = "Color"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;
	private static final String TT_DELETE = "Delete"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;

	public SPanelElement()
	{
		setLayout(new GridLayout(1, 3, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(0, 255, 150, 10));
		setForeground(Color.GRAY);

		EmptyButton eb = new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "note.png"), Slyum.ACTION_NEW_NOTE_ASSOCIED, Color.CYAN, TT_ADD_NOTE);
		eb.addActionListener(this);
		add(eb);
		
		eb = new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "color16.png"), "Color", Color.CYAN, TT_CHANGE_COLOR);
		eb.addActionListener(this);
		add(eb);
		
		eb = new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "delete16.png"), "Delete", Color.CYAN, TT_DELETE);
		eb.addActionListener(this);
		add(eb);
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
		
		if (e.getActionCommand().equals("Delete"))
			
			gv.deleteSelectedComponents();
		
		else if (e.getActionCommand().equals("Color"))
			
			GraphicComponent.askNewColorForSelectedItems();
		
		else if (e.getActionCommand().equals(Slyum.ACTION_NEW_NOTE_ASSOCIED))
			
			gv.linkNewNoteWithSelectedEntities();
	}
}
