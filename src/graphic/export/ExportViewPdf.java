
package graphic.export;

import de.erichseifert.vectorgraphics2d.PDFGraphics2D;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import graphic.GraphicView;
import static graphic.export.ExportView.MARGIN;
import java.awt.Rectangle;
import java.io.File;

public class ExportViewPdf extends ExportViewVectorFile {
  
  public static ExportViewPdf create(
      GraphicView graphicView, File file, boolean displayTitle) {
    return new ExportViewPdf(graphicView, file, displayTitle);
  }
  
  public static ExportViewPdf create(
      GraphicView graphicView, File file) {
    return create(
        graphicView, 
        file, 
        graphicView.getTxtBoxDiagramName().isVisible());
  }

  private ExportViewPdf(
      GraphicView graphicView, File file, boolean displayTitle) {
    
    super(graphicView, file, displayTitle);
  }
  
  @Override
  protected Rectangle getOuterBounds() {
    return new Rectangle(
        0, 
        0, 
        bounds.x + bounds.width + MARGIN, 
        bounds.y + bounds.height + MARGIN);
  }

  @Override
  protected VectorGraphics2D getGraphics(
      double x1, double y1, double x2, double y2) {
    
    return new PDFGraphics2D(x1, y1, x2, y2);
  }
}
