package swing;

import graphic.GraphicView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelStyleComponent extends JPanelRounded implements IListenerComponentSelectionChanged, ActionListener
{
	private static final long serialVersionUID = -9156467758854311341L;
	
	private static final String TT_ALIGN_TOP = "Align top " + Utility.keystrokeToString(Slyum.KEY_ALIGN_UP);
	private static final String TT_ALIGN_BOTTOM = "Align bottom " + Utility.keystrokeToString(Slyum.KEY_ALIGN_DOWN);
	private static final String TT_ALIGN_RIGTH = "Align right " + Utility.keystrokeToString(Slyum.KEY_ALIGN_RIGHT);
	private static final String TT_ALIGN_LEFT = "Align left " + Utility.keystrokeToString(Slyum.KEY_ALIGN_LEFT);
	
	private static final String TT_ADJUST_WIDTH = "Adjust size " + Utility.keystrokeToString(Slyum.KEY_ADJUST_SIZE);
	
	private SButton top, bottom, right, left, adujst;
	
	private static SPanelStyleComponent instance = new SPanelStyleComponent();

	private SPanelStyleComponent()
	{
		setLayout(new GridLayout(1, 5, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(0, 255,0, 10));
		setForeground(Color.GRAY);

		add(top = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "alignTop.png"), Slyum.ACTION_ALIGN_TOP, Color.GREEN, TT_ALIGN_TOP));
		add(bottom = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "alignBottom.png"), Slyum.ACTION_ALIGN_BOTTOM, Color.GREEN, TT_ALIGN_BOTTOM));
		add(right = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "alignRight.png"), Slyum.ACTION_ALIGN_RIGHT, Color.GREEN, TT_ALIGN_RIGTH));
		add(left = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "alignLeft.png"), Slyum.ACTION_ALIGN_LEFT, Color.GREEN, TT_ALIGN_LEFT));
		
		add(adujst = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "adjustWidth.png"), Slyum.ACTION_ADJUST_WIDTH, Color.GREEN, TT_ADJUST_WIDTH));

		top.setEnabled(false);
		bottom.setEnabled(false);
		right.setEnabled(false);
		left.setEnabled(false);
		adujst.setEnabled(false);
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
		
		if (Slyum.ACTION_ALIGN_TOP.equals(e.getActionCommand()))
			gv.alignHorizontal(true);

		else if (Slyum.ACTION_ALIGN_BOTTOM.equals(e.getActionCommand()))
			gv.alignHorizontal(false);

		else if (Slyum.ACTION_ALIGN_LEFT.equals(e.getActionCommand()))
			gv.alignVertical(true);

		else if (Slyum.ACTION_ALIGN_RIGHT.equals(e.getActionCommand()))
			gv.alignVertical(false);

		else if (Slyum.ACTION_ADJUST_WIDTH.equals(e.getActionCommand()))
			gv.adjustWidthSelectedEntities();
	}

	private SButton createEmptyButton(ImageIcon ii, String action, Color c, String tt)
	{
		SButton ee = new SButton(ii, action, c, tt, this);
		ee.setEnabled(false);
		return ee;
	}
	
	public SButton getBtnTop()
	{
		return top;
	}
	
	public SButton getBtnRight()
	{
		return right;
	}
	
	public SButton getBtnLeft()
	{
		return left;
	}
	
	public SButton getBtnBottom()
	{
		return bottom;
	}
	
	public SButton getBtnAdjust()
	{
		return adujst;
	}
	
	public void componentSelectionChanged()
	{
		updateBtnState();
	}
	
	public void updateBtnState()
	{
		int nb = PanelClassDiagram.getInstance().getCurrentGraphicView().countSelectedEntities();
		boolean enable = nb > 1;
		top.setEnabled(enable);
		bottom.setEnabled(enable);
		right.setEnabled(enable);
		left.setEnabled(enable);
		
		enable = nb > 0;
		adujst.setEnabled(enable);
	}
	
	public static SPanelStyleComponent getInstance()
	{
		return instance;
	}
}
