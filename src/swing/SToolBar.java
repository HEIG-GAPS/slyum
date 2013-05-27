package swing;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JToolBar;

import utility.PersonalizedIcon;

public class SToolBar extends JToolBar {
  public SToolBar() {

    setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 7, 10, 0), 
        BorderFactory.createMatteBorder(
            0, 12, 0, 0, 
            PersonalizedIcon.createImageIcon(Slyum.ICON_PATH + "toolbat-grip.png"))));
    
    setBackground(null);
    setForeground(Color.GRAY);
  }
}
