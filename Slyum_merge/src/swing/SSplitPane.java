package swing;

import java.awt.Component;

import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SSplitPane extends JSplitPane
{
	private static final long serialVersionUID = 2580252563592936191L;

	public SSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent)
	{
		super(newOrientation, newLeftComponent, newRightComponent);

		setOneTouchExpandable(true);
		setContinuousLayout(true);
		setOneTouchExpandable(false);
		setDividerSize(2);
		
		setUI(new BasicSplitPaneUI(){
			
			@Override
			public BasicSplitPaneDivider createDefaultDivider()
			{
				return new BasicSplitPaneDivider(this){
					
					private static final long serialVersionUID = -2421344291547432263L;

					@Override
					public void setBorder(Border border)
					{
					}
				};
			}
		});
	}
}
