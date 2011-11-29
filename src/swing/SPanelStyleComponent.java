package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;

import utility.PersonnalizedIcon;
import utility.SSlider;

public class SPanelStyleComponent extends JPanelRounded
{
	private static final long serialVersionUID = -9156467758854311341L;
	
	private EmptyButton undo, redo;

	public SPanelStyleComponent()
	{
		setLayout(new GridLayout(1, 7, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(0, 255,0, 10));
		setForeground(Color.GRAY);

		add(undo = new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/undo.png"), "undo", Color.GREEN));
		add(redo = new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/redo.png"), "redo", Color.GREEN));

		add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/alignTop.png"), "alignTop", Color.GREEN));
		add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/alignBottom.png"), "alignBottom", Color.GREEN));
		add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/alignRight.png"), "alignRight", Color.GREEN));
		add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/alignLeft.png"), "alignLeft", Color.GREEN));
		
		add(new EmptyButton(PersonnalizedIcon.createImageIcon("resources/icon/adjustWidth.png"), "adjustWidth", Color.GREEN));
		
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
