package graphic.export;

import graphic.GraphicComponent;
import graphic.GraphicView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import swing.PanelClassDiagram;
import swing.Slyum;
import utility.Utility;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;

import static graphic.GraphicView.DEFAULT_TITLE_BORDER_WIDTH;
import static graphic.GraphicView.isTitleBorderPainted;

/**
 * Base class for exporting the diagram view. Subclasses provide the final output format.
 * The rendering is performed on a JavaFX {@link Canvas}.
 */
public abstract class ExportView<G> {
  protected static final int MARGIN = 20;

  protected GraphicView graphicView;
  protected Rectangle bounds;
  protected int marginTop;

  private boolean displayTitle;

  public ExportView(final GraphicView graphicView, final boolean displayTitle) {
    this.graphicView = graphicView;
    this.displayTitle = displayTitle;
    initializeBounds();
  }

  private void initializeBounds() {
    marginTop = MARGIN;

    if (displayTitle)
      marginTop += graphicView.getTxtBoxDiagramName().getBounds().height;

    int minX = Integer.MAX_VALUE,
        minY = Integer.MAX_VALUE,
        maxX = 0,
        maxY = 0;

    final LinkedList<GraphicComponent> components =
        graphicView.getAllDiagramComponents();

    if (components.isEmpty()) {
      bounds = new Rectangle();
      return;
    }

    // Compute the rectangle englobing all graphic components.
    for (final GraphicComponent component : components) {
      final Rectangle localBounds = component.getBounds();
      final Point max = new Point(localBounds.x + localBounds.width,
                                  localBounds.y + localBounds.height);

      if (minX > localBounds.x) minX = localBounds.x;
      if (minY > localBounds.y) minY = localBounds.y;
      if (maxX < max.x) maxX = max.x;
      if (maxY < max.y) maxY = max.y;
    }

    bounds = new Rectangle(minX, minY, maxX - minX, maxY - minY);
  }

  public abstract Object export();

  protected Rectangle getOuterBounds() {
    return new Rectangle(
        Math.max(bounds.x - MARGIN, 0),
        Math.max(bounds.y - marginTop, 0),
        bounds.width + 2 * MARGIN,
        bounds.height + marginTop + MARGIN);
  }

  /**
   * Renders the diagram onto a JavaFX {@link Canvas} of the given size and returns a {@link BufferedImage}.
   *
   * @param outerBounds the dimensions of the exported area
   * @param type        {@link BufferedImage} type constant
   *
   * @return the rendered {@link BufferedImage}
   */
  protected BufferedImage renderToImage(Rectangle outerBounds, int type) {
    int w = Math.max(outerBounds.width + DEFAULT_TITLE_BORDER_WIDTH * 2, 1);
    int h = Math.max(outerBounds.height + DEFAULT_TITLE_BORDER_WIDTH * 2, 1);
    Canvas canvas = new Canvas(w, h);
    GraphicsContext gc = canvas.getGraphicsContext2D();

    if (type == BufferedImage.TYPE_INT_RGB) {
      gc.setFill(javafx.scene.paint.Color.WHITE);
      gc.fillRect(0, 0, w, h);
    }

    gc.translate(-(outerBounds.x - DEFAULT_TITLE_BORDER_WIDTH),
                 -(outerBounds.y - DEFAULT_TITLE_BORDER_WIDTH));

    drawToContext(gc);

    javafx.scene.image.WritableImage fxImage = new javafx.scene.image.WritableImage(w, h);
    canvas.snapshot(null, fxImage);
    // Convert WritableImage to BufferedImage manually
    int imgW = (int)fxImage.getWidth(), imgH = (int)fxImage.getHeight();
    BufferedImage bimg = new BufferedImage(imgW, imgH, type);
    javafx.scene.image.PixelReader pr = fxImage.getPixelReader();
    for (int y = 0; y < imgH; y++)
      for (int x = 0; x < imgW; x++)
        bimg.setRGB(x, y, pr.getArgb(x, y));
    return bimg;
  }

  /**
   * Draws all diagram components onto the given {@link GraphicsContext}.
   *
   * @param gc the JavaFX graphics context
   */
  protected void drawToContext(GraphicsContext gc) {
    graphicView.setPictureMode(true);
    Utility.setRenderQuality(gc);

    // Paint diagram's name
    if (displayTitle) {
      Rectangle outerBounds = getOuterBounds();
      graphicView.getTxtBoxDiagramName().paintComponentAt(
          gc, new Point(outerBounds.x, outerBounds.y));

      if (isTitleBorderPainted()) {
        gc.setLineWidth(DEFAULT_TITLE_BORDER_WIDTH);
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.strokeRect(outerBounds.x, outerBounds.y,
                      outerBounds.width - DEFAULT_TITLE_BORDER_WIDTH,
                      outerBounds.height - DEFAULT_TITLE_BORDER_WIDTH);
      }
    }

    // Paint all components.
    for (final GraphicComponent graphicComponent : graphicView.getAllDiagramComponents()) {
      graphicComponent.paintComponent(gc);
    }

    graphicView.setPictureMode(false);
  }

}
