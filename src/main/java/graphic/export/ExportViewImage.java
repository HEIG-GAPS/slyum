package graphic.export;

import graphic.GraphicView;

import java.awt.*;
import java.awt.image.BufferedImage;


public class ExportViewImage extends ExportView {

  int type;

  private ExportViewImage(GraphicView graphicView, boolean displayTitle, int type) {
    super(graphicView, displayTitle);
    this.type = type;
  }

  public static ExportViewImage create(
      GraphicView graphicView, boolean displayTitle, int type) {

    return new ExportViewImage(graphicView, displayTitle, type);
  }

  public static ExportViewImage create(
      GraphicView graphicView, boolean displayTitle) {

    return new ExportViewImage(
        graphicView, displayTitle, BufferedImage.TYPE_4BYTE_ABGR_PRE);
  }

  public static ExportViewImage create(GraphicView graphicView) {
    return new ExportViewImage(
        graphicView,
        graphicView.getTxtBoxDiagramName().isVisible(),
        BufferedImage.TYPE_4BYTE_ABGR_PRE);
  }

  public static ExportViewImage create(GraphicView graphicView, int type) {
    return new ExportViewImage(
        graphicView, graphicView.getTxtBoxDiagramName().isVisible(), type);
  }

  @Override
  public BufferedImage export() {
    Rectangle outerBounds = getOuterBounds();
    if (outerBounds.width <= 0 || outerBounds.height <= 0)
      return new BufferedImage(10, 10, type);
    return renderToImage(outerBounds, type);
  }

}
