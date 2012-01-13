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

public class SPanelZOrder extends JPanelRounded implements ActionListener, IListenerComponentSelectionChanged
{
	private static final long serialVersionUID = 5673984487858602763L;
	
	private static final String TT_MOVE_TOP = "Top" + Utility.keystrokeToString(Slyum.KEY_MOVE_TOP);
	private static final String TT_MOVE_UP = "Up" + Utility.keystrokeToString(Slyum.KEY_MOVE_UP);
	private static final String TT_MOVE_DOWN = "Down" + Utility.keystrokeToString(Slyum.KEY_MOVE_DOWN);
	private static final String TT_MOVE_BOTTOM = "Bottom" + Utility.keystrokeToString(Slyum.KEY_MOVE_BOTTOM);
	
	private static SPanelZOrder instance = new SPanelZOrder();

	private SButton top, up, down, bottom;

	private SPanelZOrder()
	{
		setLayout(new GridLayout(1, 4, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(255, 0, 150, 10));
		setForeground(Color.GRAY);

		add(top = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "top.png"), Slyum.ACTION_MOVE_TOP, Color.MAGENTA, TT_MOVE_TOP));
		add(up = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "up.png"), Slyum.ACTION_MOVE_UP, Color.MAGENTA, TT_MOVE_UP));
		add(down = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "down.png"), Slyum.ACTION_MOVE_DOWN, Color.MAGENTA, TT_MOVE_DOWN));
		add(bottom = createEmptyButton(PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "bottom.png"), Slyum.ACTION_MOVE_BOTTOM, Color.MAGENTA, TT_MOVE_BOTTOM));
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}
	
	public SButton getBtnTop()
	{
		return top;
	}
	
	public SButton getBtnUp()
	{
		return up;
	}
	
	public SButton getBtnDown()
	{
		return down;
	}
	
	public SButton getBtnBottom()
	{
		return bottom;
	}
	
	private SButton createEmptyButton(ImageIcon ii, String action, Color c, String tt)
	{
		SButton ee = new SButton(ii, action, c, tt, this);
		ee.setEnabled(false);
		
		return ee;
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
		
		if (Slyum.ACTION_MOVE_TOP.equals(e.getActionCommand()))
			
			gv.moveZOrderTopSelectedEntities();
		
		else if (Slyum.ACTION_MOVE_UP.equals(e.getActionCommand()))

			gv.moveZOrderUpSelectedEntities();
		
		else if (Slyum.ACTION_MOVE_DOWN.equals(e.getActionCommand()))

			gv.moveZOrderDownSelectedEntities();

		else if (Slyum.ACTION_MOVE_BOTTOM.equals(e.getActionCommand()))

			gv.moveZOrderBottomSelectedEntities();
	}
	
	@Override
	public void componentSelectionChanged()
	{
		updateBtnState();
	}
	
	public void updateBtnState()
	{
		GraphicView gv = PanelClassDiagram.getInstance().getCurrentGraphicView();
		boolean enable = gv.countEntities() > 1 && gv.countSelectedEntities() > 0;
		
		top.setEnabled(enable);
		up.setEnabled(enable);
		down.setEnabled(enable);
		bottom.setEnabled(enable);
	}

	public static SPanelZOrder getInstance()
	{
		return instance;
	}
}
