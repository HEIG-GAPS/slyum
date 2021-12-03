package swing;

import javax.swing.*;
import java.awt.*;

public class NewViewDialog extends JDialog {

  public static void displayDialog() {
    NewViewDialog dialog = new NewViewDialog();

    dialog.setSize(new Dimension(1000, 600));
    dialog.setContentPane(new ViewViewer(MultiViewManager.getAllClosedGraphicViews(), dialog));
    dialog.setLocationRelativeTo(Slyum.getInstance());
    dialog.setVisible(true);
  }

  private NewViewDialog() {
    super(Slyum.getInstance(), "Slyum - List of views", true);
  }

}
