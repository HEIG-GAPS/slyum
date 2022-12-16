package graphic.export;

import graphic.GraphicView;
import org.apache.batik.svggen.SVGGraphics2D;
import utility.SMessageDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ExportViewVectorFile extends ExportView<SVGGraphics2D> {

  /** The output file. */
  private final File file;

  public ExportViewVectorFile(final GraphicView graphicView, final File file, final boolean displayTitle) {
    super(graphicView, displayTitle);
    this.file = file;
  }

  @Override
  public final Object export() {

    try (FileOutputStream fileStream = new FileOutputStream(file)) {
      writeToFile(fileStream, this::draw);
    } catch (Exception ex) {
      Logger.getLogger(ExportViewPdf.class.getName()).log(Level.SEVERE, null, ex);
      SMessageDialog.showErrorMessage(ex.getMessage());
    }
    return null;
  }

  protected abstract void writeToFile(FileOutputStream fileOutputStream,
                                      Function<SVGGraphics2D, SVGGraphics2D> draw) throws Exception;

}
