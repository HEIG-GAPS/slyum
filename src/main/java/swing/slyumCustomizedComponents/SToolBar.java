package swing.slyumCustomizedComponents;

import swing.Slyum;
import utility.PersonalizedIcon;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SToolBar extends JToolBar {

  private Border borderHorizontal, borderVertical;

  public SToolBar() {
    borderHorizontal = BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(10, 7, 10, 0),
        BorderFactory.createMatteBorder(
            0,
            12,
            0,
            0,
            PersonalizedIcon.createImageIcon("toolbat-grip.png")));

    borderVertical = BorderFactory.createCompoundBorder(
        BorderFactory.createEmptyBorder(0, 5, 0, 5),
        BorderFactory.createMatteBorder(
            12,
            0,
            0,
            0,
            PersonalizedIcon.createImageIcon("toolbat-grip-vertical.png")));

    setBackground(null);
    setForeground(Color.GRAY);
    setBorder(borderHorizontal);

    addPropertyChangeListener("orientation", new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        if (getOrientation() == SwingUtilities.HORIZONTAL)
          setBorder(borderHorizontal);
        else
          setBorder(borderVertical);
      }
    });
  }

}
