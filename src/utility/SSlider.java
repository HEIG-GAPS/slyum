package utility;

import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.multi.MultiSliderUI;


public class SSlider extends JSlider {
	
	public SSlider(final int defaultValue, int minValue, int maxValue) 
	{
	  super(minValue, maxValue, defaultValue);
		setToolTipText("Zoom (Ctrl+MouseWheel)(Right click : " + defaultValue + ")");
		addMouseListener(new MouseAdapter() {
		  
		  @Override
		  public void mouseClicked(MouseEvent e) {
	      if (e.getButton() == MouseEvent.BUTTON3)
	        setValue(defaultValue);
		  };
    });
		setUI(new BasicSliderUI(this){
		  @Override
		  public void paintThumb(Graphics g) {
		    //Utility.setRenderQuality(g);
		    super.paintThumb(g);
		  }
		  
		  @Override
		  public void paintTicks(Graphics g) {
		    super.paintTicks(g);
		  }
		  
		  @Override
		  public void paintTrack(Graphics g) {
		    super.paintTrack(g);
		  }
		});
	}
}
