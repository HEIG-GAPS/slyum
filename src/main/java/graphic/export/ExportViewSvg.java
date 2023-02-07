package graphic.export;

import graphic.GraphicView;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.function.Function;

import static graphic.GraphicView.DEFAULT_TITLE_BORDER_WIDTH;

public final class ExportViewSvg extends ExportViewVectorFile {

  public static ExportViewSvg create(final GraphicView graphicView, final File file, final boolean displayTitle) {
    return new ExportViewSvg(graphicView, file, displayTitle);
  }

  public static ExportViewSvg create(final GraphicView graphicView, final File file) {
    return create(graphicView, file, graphicView.getTxtBoxDiagramName().isVisible());
  }

  private ExportViewSvg(final GraphicView graphicView, final File file, final boolean displayTitle) {
    super(graphicView, file, displayTitle);
  }

  static SVGGraphics2D createSVG(final ExportViewVectorFile exportViewVectorFile,
                                 final Function<SVGGraphics2D, SVGGraphics2D> draw) {
    // Get a DOMImplementation.
    DOMImplementation domImpl = GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document.
    String svgNS = "http://www.w3.org/2000/svg";
    Document document = domImpl.createDocument(svgNS, "svg", null);

    Rectangle outerBounds = exportViewVectorFile.getOuterBounds();
    final Dimension dimension = new Dimension(outerBounds.width + 2 * DEFAULT_TITLE_BORDER_WIDTH,
                                              outerBounds.height + 2 * DEFAULT_TITLE_BORDER_WIDTH);

    final SVGGeneratorContext svgGeneratorContext = SVGGeneratorContext.createDefault(document);
    svgGeneratorContext.setPrecision(12);

    // Create an instance of the SVG Generator.
    final SVGGraphics2D svgGraphics2D = draw.apply(new SVGGraphics2D(svgGeneratorContext, true));
    svgGraphics2D.setSVGCanvasSize(dimension);
    return svgGraphics2D;
  }

  @Override
  protected void writeToFile(final FileOutputStream fileOutputStream,
                             final Function<SVGGraphics2D, SVGGraphics2D> draw) throws Exception {

    try (Writer writer = new OutputStreamWriter(fileOutputStream)) {
      createSVG(this, draw).stream(writer, true);
      writer.flush();
    }
  }

}
