package swing;

import javax.swing.JPanel;
import javax.swing.JToolBar;

public class SToolBar extends JPanel
{
	private static final long serialVersionUID = 3947955937687151315L;

	public SToolBar() {
		
		SPanelFileComponent panel = new SPanelFileComponent();
		add(panel);
		
		SPanelUndoRedo panel_2 = new SPanelUndoRedo();
		add(panel_2);
		
		SPanelZOrder panel_3 = new SPanelZOrder();
		add(panel_3);
		
		JToolBar toolBar = new JToolBar();
		add(toolBar);
		
		SPanelStyleComponent panel_1 = new SPanelStyleComponent();
		toolBar.add(panel_1);
	}

}
