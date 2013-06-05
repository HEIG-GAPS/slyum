package swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.plaf.metal.MetalButtonUI;

public class SButton extends JButton {
  private final Color BACKGROUND = Color.WHITE;
	private LinkedList<Component> linkedComponents = new LinkedList<>();
	
	public SButton(Icon icon, String tooltip)
	{
		super(icon);
		init("", tooltip, null);
	}

	public SButton(Icon icon, String action, String tooltip, ActionListener al)
	{
		super(icon);
		init(action, tooltip, al);
	}
	
	public SButton(String text, String action, String tooltip, ActionListener al)
	{
		super(text);
		init(action, tooltip, al);
	}
	
	private void init(String action, String tooltip, ActionListener al) {
	  setPreferredSize(new Dimension(24, 24));
		setActionCommand(action);
		addActionListener(al);
    setContentAreaFilled(false);
		setBorderPainted(false);
		setBackground(BACKGROUND);
		setToolTipText(tooltip);
		
		setUI(new MetalButtonUI() {
		  @Override
		  protected void paintFocus(Graphics g, AbstractButton b,
		      Rectangle viewRect, Rectangle textRect, Rectangle iconRect) {
		  }
		});
		
		addMouseListener(new MouseAdapter() {
		  @Override
		  public void mouseEntered(MouseEvent e) {
		    super.mouseEntered(e);
		    if (isEnabled()) {
	        if (getBackground().equals(Color.white)) {
  	        setBackground(BACKGROUND);
  	        setContentAreaFilled(true);
	        }
	        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
		    }
		  }
		  
		  @Override
		  public void mouseExited(MouseEvent e) {
		    super.mouseExited(e);
		    if (getBackground().equals(BACKGROUND))
		      setContentAreaFilled(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		  }
    });
	}
	
	public void resetBackground() {
    setContentAreaFilled(false);
	  setBackground(BACKGROUND);
	}
	
	@Override
	public void setEnabled(boolean b)
	{
		super.setEnabled(b);
		
		for (Component c : linkedComponents)
			c.setEnabled(b);
	}
	
	public void linkComponent(Component c)
	{
		linkedComponents.add(c);
	}
}