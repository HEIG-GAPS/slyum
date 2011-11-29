package swing;

import java.awt.Component;

import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

public class SSplitPane extends JSplitPane
{

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
					@Override
					public void setBorder(Border border)
					{
					}
				};
			}
		});
	}
}
