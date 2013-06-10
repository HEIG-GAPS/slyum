package utility;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

import swing.Slyum;


public class SSlider extends JSlider {
	
	public SSlider(final int defaultValue, int minValue, int maxValue) {
	  super(minValue, maxValue, defaultValue);
	  
		setToolTipText("Zoom (Ctrl+MouseWheel)(Right click : " + defaultValue + ")");
		setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
		addChangeListener(new ChangeListener() {
      
      @Override
      public void stateChanged(ChangeEvent evt) {
        repaint();
      }
    });
		
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
		    Rectangle knobBounds = thumbRect;
        int w = knobBounds.width - 4;
        int h = knobBounds.height - 4;

        g.translate(knobBounds.x, knobBounds.y);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(2, 2, w, h);
        

        g.translate(-knobBounds.x, -knobBounds.y);
		  }
		  
		  @Override
		  public void paintTrack(Graphics g) {
		    Rectangle trackBounds = trackRect;

        int cy = (trackBounds.height / 2) - 2;
        int cw = trackBounds.width;

        g.translate(trackBounds.x, trackBounds.y + cy);

        g.setColor(Color.GRAY);
        g.fillRect(0, 0, cw, 3);

        g.translate(-trackBounds.x, -(trackBounds.y + cy));
		  }
		  
		  @Override
		  public void paintFocus(Graphics g) {
		  }
		  
		  @Override
		  public void paint(Graphics g, JComponent c) {
		    super.paint(g, c);
		    Utility.setRenderQuality(g);
		    // Paint the current number.
		    g.setColor(Color.DARK_GRAY);
		    g.setFont(Slyum.getDefaultFont().deriveFont(11.0f));
		    g.drawString(String.valueOf(getValue()), getWidth() - 20, 12);
		    Utility.setDefaultRenderQuality(g);
		  }
		});
	}
}
