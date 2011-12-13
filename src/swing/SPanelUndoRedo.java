package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelUndoRedo extends JPanelRounded
{
	private static final long serialVersionUID = -2478917416625191084L;
	
	private static final String TT_UNDO = "Undo " + Utility.keystrokeToString(Slyum.KEY_UNDO);
	private static final String TT_REDO = "Redo " + Utility.keystrokeToString(Slyum.KEY_REDO);
	
	private EmptyButton undo, redo;

	public SPanelUndoRedo()
	{
		setLayout(new GridLayout(1, 2, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(255, 150, 0, 20));
		setForeground(Color.GRAY);

		add(undo = new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "undo.png"), Slyum.ACTION_UNDO, Color.ORANGE, TT_UNDO));
		add(redo = new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "redo.png"), Slyum.ACTION_REDO, Color.ORANGE, TT_REDO));
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
	
	public EmptyButton getUndoButton()
	{
		return undo;
	}
	
	public EmptyButton getRedoButton()
	{
		return redo;
	}
}
