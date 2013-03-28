package swing;

import java.awt.Component;
import javax.swing.JSplitPane;

public class SSplitPane extends JSplitPane {
	public SSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
		setOneTouchExpandable(true);
		setContinuousLayout(true);
		setDividerSize(10);
	}
}
