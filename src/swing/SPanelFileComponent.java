package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;

import utility.PersonalizedIcon;

public class SPanelFileComponent extends JPanelRounded
{

	public SPanelFileComponent()
	{
		setLayout(new GridLayout(1, 5, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(/*Color.WHITE*/ new Color(0, 0, 255, 10));
		setForeground(Color.GRAY);
		setMaximumSize(new Dimension(300, 50));

		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/new.png"), "newProject", Color.BLUE));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/open.png"), "open", Color.BLUE));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/save.png"), "save", Color.BLUE));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/export.png"), "export", Color.BLUE));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/print.png"), "print", Color.BLUE));
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
}
