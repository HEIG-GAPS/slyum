package swing;

import graphic.GraphicComponent;
import graphic.entity.EntityView;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;

import utility.PersonalizedIcon;
import utility.Utility;

public class SPanelZOrder extends JPanelRounded implements ActionListener
{
	private static final String TT_MOVE_TOP = "Top"/* + Utility.keystrokeToString(Slyum.KEY_UNDO)*/;
	private static final String TT_MOVE_UP = "Up"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;
	private static final String TT_MOVE_DOWN = "Down"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;
	private static final String TT_MOVE_BOTTOM = "Bottom"/* + Utility.keystrokeToString(Slyum.KEY_REDO)*/;

	public SPanelZOrder()
	{
		setLayout(new GridLayout(1, 4, 5, 5));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 7));
		setBackground(/*Color.WHITE*/ new Color(255, 0, 150, 10));
		setForeground(Color.GRAY);

		EmptyButton eb = new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/zorder/top.png"), "ZOrderTOP", Color.MAGENTA, TT_MOVE_TOP);
		eb.addActionListener(this);
		add(eb);
		
		eb = new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/zorder/up.png"), "ZOrderUP", Color.MAGENTA, TT_MOVE_UP);
		eb.addActionListener(this);
		add(eb);
		
		eb = new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/zorder/down.png"), "ZOrderDown", Color.MAGENTA, TT_MOVE_DOWN);
		eb.addActionListener(this);
		add(eb);
		
		eb = new EmptyButton(PersonalizedIcon.createImageIcon("resources/icon/zorder/bottom.png"), "ZOrderBottom", Color.MAGENTA, TT_MOVE_BOTTOM);
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
