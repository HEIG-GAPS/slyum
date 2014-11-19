package graphic.export;

import graphic.GraphicView;
import static graphic.GraphicView.DEFAULT_TITLE_BORDER_WIDTH;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
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
    
    // Create the buffered image with margin.   
    Rectangle outerBounds = getOuterBounds();
    
    if (getOuterBounds().width <= 0 || getOuterBounds().height <= 0)
      return new BufferedImage(10, 10, type);
    
    final BufferedImage img = new BufferedImage(
        outerBounds.width + DEFAULT_TITLE_BORDER_WIDTH*2,
        outerBounds.height + DEFAULT_TITLE_BORDER_WIDTH*2, 
        type);
    
    final Graphics2D g2d = img.createGraphics();
    
    if (type == BufferedImage.TYPE_INT_RGB) {
      g2d.setColor(Color.WHITE);
      g2d.fillRect(
          0, 0, 
          bounds.width + MARGIN * 2, bounds.height + MARGIN + marginTop);
    }
    
    // Translate the rectangle containing all graphic components at origin.
    g2d.translate(-(outerBounds.x - DEFAULT_TITLE_BORDER_WIDTH), 
                  -(outerBounds.y - DEFAULT_TITLE_BORDER_WIDTH));
    
    draw(g2d);
    return img;
  }
}
