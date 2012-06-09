package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;

import change.Change;

import graphic.GraphicView;
import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelUndoRedo extends JPanelRounded implements ActionListener
{
	private static final long serialVersionUID = -2478917416625191084L;
	
	private static final String TT_UNDO = "Undo " + Utility.keystrokeToString(Slyum.KEY_UNDO);
	private static final String TT_REDO = "Redo " + Utility.keystrokeToString(Slyum.KEY_REDO);
	
	private SButton undo, redo;
	
	private static SPanelUndoRedo instance;
	
	public static SPanelUndoRedo getInstance()
	{
		if (instance == null)
			instance = new SPanelUndoRedo();
		
		return instance;
	}

	private SPanelUndoRedo()
	{
		setLayout(new GridLayout(1, 2, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(255, 150, 0, 20));
		setForeground(Color.GRAY);
		
		add(undo = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "undo.png"), Slyum.ACTION_UNDO, Color.ORANGE, TT_UNDO, this));
		add(redo = new SButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "redo.png"), Slyum.ACTION_REDO, Color.ORANGE, TT_REDO, this));
		
		undo.setEnabled(false);
		redo.setEnabled(false);
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
		gv.setStopRepaint(true);
		
		if (Slyum.ACTION_UNDO.equals(e.getActionCommand()))
			Change.undo();
		
		else if (Slyum.ACTION_REDO.equals(e.getActionCommand()))
			Change.redo();
		
		gv.goRepaint();
	}

	public SButton getUndoButton()
	{
		return undo;
	}
	
	public SButton getRedoButton()
	{
		return redo;
	}
}
