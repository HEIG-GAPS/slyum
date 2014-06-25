package swing.slyumCustomizedComponents;

import java.awt.Dimension;

public class SToolBarButton extends SButton {
  
  public SToolBarButton(SlyumAction a) {
    super(a);
    setPreferredSize(new Dimension(22, 18));
    setHideActionText(true);
  }
}
