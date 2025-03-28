package graphic.export;

import graphic.GraphicView;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.commons.io.input.XmlStreamReader;
import org.apache.fop.svg.PDFTranscoder;

import java.io.*;
import java.util.function.Function;

public final class ExportViewEps extends ExportViewVectorFile {

  public static ExportViewEps create(final GraphicView graphicView, final File file, final boolean displayTitle) {
    return new ExportViewEps(graphicView, file, displayTitle);
  }

  public static ExportViewEps create(final GraphicView graphicView, final File file) {
    return create(graphicView, file, graphicView.getTxtBoxDiagramName().isVisible());
  }

  private ExportViewEps(final GraphicView graphicView, final File file, final boolean displayTitle) {
    super(graphicView, file, displayTitle);
  }

  @Override
  protected void writeToFile(final FileOutputStream fileOutputStream,
                             final Function<SVGGraphics2D, SVGGraphics2D> draw) throws Exception {

    try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
         Writer writer = new OutputStreamWriter(byteArrayOutputStream)) {
      ExportViewSvg.createSVG(this, draw).stream(writer);
      writer.flush();

      try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
           Reader reader = new XmlStreamReader(byteArrayInputStream)) {
        TranscoderInput transcoderInput = new TranscoderInput(reader);
        TranscoderOutput transcoderOutput = new TranscoderOutput(fileOutputStream);
        Transcoder transcoder = new PDFTranscoder();
        transcoder.transcode(transcoderInput, transcoderOutput);
      }
    }
  }

}
