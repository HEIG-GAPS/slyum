package swing;

import graphic.entity.EntityView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;

import utility.PersonalizedIcon;

public class SPanelZOrder extends JPanelRounded implements ActionListener
{
	private static final long serialVersionUID = 5673984487858602763L;
	
	private static final String TT_MOVE_TOP = "Top"/* + Utility.keystrokeToString(Slyum.KEY_UNDO)*/;
	private static final String TT_MOVE_UP = "Up"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;
	private static final String TT_MOVE_DOWN = "Down"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;
	private static final String TT_MOVE_BOTTOM = "Bottom"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;
	
	public static final String ZORDER_PATH = Slyum.ICON_PATH + "zorder" + Slyum.FILE_SEPARATOR;

	public SPanelZOrder()
	{
		setLayout(new GridLayout(1, 4, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(255, 0, 150, 10));
		setForeground(Color.GRAY);

		EmptyButton eb = new EmptyButton(PersonalizedIcon.createImageIcon(ZORDER_PATH + "top.png"), "ZOrderTOP", Color.MAGENTA, TT_MOVE_TOP);
		eb.addActionListener(this);
		add(eb);
		
		eb = new EmptyButton(PersonalizedIcon.createImageIcon(ZORDER_PATH + "up.png"), "ZOrderUP", Color.MAGENTA, TT_MOVE_UP);
		eb.addActionListener(this);
		add(eb);
		
		eb = new EmptyButton(PersonalizedIcon.createImageIcon(ZORDER_PATH + "down.png"), "ZOrderDown", Color.MAGENTA, TT_MOVE_DOWN);
		eb.addActionListener(this);
		add(eb);
		
		eb = new EmptyButton(PersonalizedIcon.createImageIcon(ZORDER_PATH + "bottom.png"), "ZOrderBottom", Color.MAGENTA, TT_MOVE_BOTTOM);
		eb.addActionListener(this);
		add(eb);
		
		setMaximumSize(new Dimension(43 * ((GridLayout)getLayout()).getColumns(), 50));
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		for (EntityView ev : PanelClassDiagram.getInstance().getCurrentGraphicView().getSelectedEntities())
			
			ev.actionPerformed(e);
	}
}
