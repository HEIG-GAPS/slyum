package swing;

import javax.swing.JPanel;
import utility.SSlider;
import java.awt.Color;

public class SToolBar extends JPanel
{
	public SToolBar() {
		
		SPanelFileComponent panel = new SPanelFileComponent();
		add(panel);
		
		SPanelUndoRedo panel_2 = new SPanelUndoRedo();
		add(panel_2);
		
		SPanelZOrder panel_3 = new SPanelZOrder();
		add(panel_3);
		
		SPanelStyleComponent panel_1 = new SPanelStyleComponent();
		add(panel_1);
	}

}
