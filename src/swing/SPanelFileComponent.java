package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelFileComponent extends JPanelRounded
{
	private static final long serialVersionUID = -3219782414246923686L;
	
	private static final String TT_NEW_PROJECT = "New project " + Utility.keystrokeToString(Slyum.KEY_NEW_PROJECT);
	private static final String TT_OPEN = "Open " + Utility.keystrokeToString(Slyum.KEY_OPEN_PROJECT);
	private static final String TT_SAVE = "Save " + Utility.keystrokeToString(Slyum.KEY_SAVE);
	private static final String TT_EXPORT = "Export image " + Utility.keystrokeToString(Slyum.KEY_EXPORT);
	private static final String TT_CLIPBOARD = "Presse-papier " + Utility.keystrokeToString(Slyum.KEY_KLIPPER);
	private static final String TT_PRINT = "Print " + Utility.keystrokeToString(Slyum.KEY_PRINT);
	
	public SPanelFileComponent()
	{
		setLayout(new GridLayout(1, 6, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(/*Color.WHITE*/ new Color(0, 0, 255, 10));
		setForeground(Color.GRAY);
		setMaximumSize(new Dimension(300, 50));

		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/new.png"), "newProject", Color.BLUE, TT_NEW_PROJECT));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/open.png"), "open", Color.BLUE, TT_OPEN));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/save.png"), "save", Color.BLUE, TT_SAVE));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/export.png"), "export", Color.BLUE, TT_EXPORT));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/klipper.png"), "klipper", Color.BLUE, TT_CLIPBOARD));
		add(new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/print.png"), "print", Color.BLUE, TT_PRINT));
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
}
