package graphic.export;

import graphic.GraphicComponent;
import graphic.GraphicView;
import static graphic.GraphicView.DEFAULT_TITLE_BORDER_WIDTH;
import static graphic.GraphicView.isTitleBorderPainted;
import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import utility.Utility;


public abstract class ExportView {
  protected static final int MARGIN = 20;
  
  protected GraphicView graphicView;
  protected Rectangle bounds;
  protected int marginTop;
  
  private boolean displayTitle;

  public ExportView(GraphicView graphicView, boolean displayTitle) {
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
    
    if (components.size() == 0)
      bounds = new Rectangle();

    // Compute the rectangle englobing all graphic components.
    for (final GraphicComponent c : components) {
      final Rectangle localBounds = c.getBounds();
      final Point max = new Point(localBounds.x + localBounds.width, localBounds.y
              + localBounds.height);

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
        bounds.x - MARGIN, 
        bounds.y - marginTop, 
        bounds.width + 2 * MARGIN, 
        bounds.height + marginTop + MARGIN);
  }
  
  protected Graphics2D draw(Graphics2D g2d) {
    graphicView.setPictureMode(true);
    
    Utility.setRenderQuality(g2d);

    // Paint all components on picture.
    for (final GraphicComponent c : graphicView.getAllDiagramComponents())
      c.paintComponent(g2d);
    
    // Paint diagram's name
    if (displayTitle) {
      Rectangle outerBounds = getOuterBounds();
      
      graphicView.getTxtBoxDiagramName().paintComponentAt(
          g2d, new Point(outerBounds.x, outerBounds.y));

      // Paint border
      if (isTitleBorderPainted()) {
        g2d.setStroke(new BasicStroke(DEFAULT_TITLE_BORDER_WIDTH));
        g2d.draw(new Rectangle2D.Float(
            outerBounds.x,
            outerBounds.y,
            outerBounds.width - DEFAULT_TITLE_BORDER_WIDTH,
            outerBounds.height - DEFAULT_TITLE_BORDER_WIDTH));
      }
    }
    
    graphicView.setPictureMode(false);
    return g2d;
  }
}
