package swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelStyleComponent extends JPanelRounded
{
	private static final long serialVersionUID = -9156467758854311341L;
	
	private static final String TT_ALIGN_TOP = "Align top " + Utility.keystrokeToString(Slyum.KEY_ALIGN_UP);
	private static final String TT_ALIGN_BOTTOM = "Align bottom " + Utility.keystrokeToString(Slyum.KEY_ALIGN_DOWN);
	private static final String TT_ALIGN_RIGTH = "Align right " + Utility.keystrokeToString(Slyum.KEY_ALIGN_RIGHT);
	private static final String TT_ALIGN_LEFT = "Align left " + Utility.keystrokeToString(Slyum.KEY_ALIGN_LEFT);
	
	private static final String TT_ADJUST_WIDTH = "Adjust size " + Utility.keystrokeToString(Slyum.KEY_ADJUST_SIZE);

	public SPanelStyleComponent()
	{
		setLayout(new GridLayout(1, 5, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(0, 255,0, 10));
		setForeground(Color.GRAY);

		add(new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "alignTop.png"), "alignTop", Color.GREEN, TT_ALIGN_TOP));
		add(new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "alignBottom.png"), "alignBottom", Color.GREEN, TT_ALIGN_BOTTOM));
		add(new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "alignRight.png"), "alignRight", Color.GREEN, TT_ALIGN_RIGTH));
		add(new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "alignLeft.png"), "alignLeft", Color.GREEN, TT_ALIGN_LEFT));
		
		add(new EmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "adjustWidth.png"), "adjustWidth", Color.GREEN, TT_ADJUST_WIDTH));
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
}
