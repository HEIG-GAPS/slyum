package graphic.export;

import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import graphic.GraphicView;
import utility.SMessageDialog;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import static graphic.GraphicView.DEFAULT_TITLE_BORDER_WIDTH;

public abstract class ExportViewVectorFile extends ExportView {

  protected File file;

  public ExportViewVectorFile(
      GraphicView graphicView, File file, boolean displayTitle) {

    super(graphicView, displayTitle);
    this.file = file;
  }

  @Override
  public Object export() {
    // Write the output to a file
    Rectangle outerBounds = getOuterBounds();

    VectorGraphics2D g2d = getGraphics(
        outerBounds.x - DEFAULT_TITLE_BORDER_WIDTH,
        outerBounds.y - DEFAULT_TITLE_BORDER_WIDTH,
        outerBounds.width + 2 * DEFAULT_TITLE_BORDER_WIDTH,
        outerBounds.height + 2 * DEFAULT_TITLE_BORDER_WIDTH);

    draw(g2d);

    try (FileOutputStream fileStream = new FileOutputStream(file)) {
      fileStream.write(g2d.getBytes());
    } catch (Exception ex) {
      Logger.getLogger(ExportViewPdf.class.getName()).log(Level.SEVERE, null, ex);
      SMessageDialog.showErrorMessage(ex.getMessage());
    }
    return null;
  }

  protected abstract VectorGraphics2D getGraphics(
      double x1, double y1, double x2, double y2);

}
