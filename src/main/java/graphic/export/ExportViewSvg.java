package graphic.export;

import de.erichseifert.vectorgraphics2d.SVGGraphics2D;
import de.erichseifert.vectorgraphics2d.VectorGraphics2D;
import graphic.GraphicView;

import java.io.File;

public class ExportViewSvg extends ExportViewVectorFile {

  public static ExportViewSvg create(
      GraphicView graphicView, File file, boolean displayTitle) {
    return new ExportViewSvg(graphicView, file, displayTitle);
  }

  public static ExportViewSvg create(
      GraphicView graphicView, File file) {
    return create(
        graphicView,
        file,
        graphicView.getTxtBoxDiagramName().isVisible());
  }

  private ExportViewSvg(
      GraphicView graphicView, File file, boolean displayTitle) {

    super(graphicView, file, displayTitle);
  }

  @Override
  protected VectorGraphics2D getGraphics(
      double x1, double y1, double x2, double y2) {

    return new SVGGraphics2D(x1, y1, x2, y2);
  }

}
