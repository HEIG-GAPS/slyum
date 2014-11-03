package swing;

import graphic.GraphicView;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JPanel;
import org.imgscalr.Scalr;

public class ViewViewer 
    extends JPanel 
    implements MouseListener, MouseMotionListener {
 
  private List<LeftButton> diagramNames = new LinkedList<>();
  private NewViewDialog parent;
  
  private LeftButton ltnCreatDiagram;
  private LeftButton currentHover,
                     currentPressed;

  public ViewViewer(List<GraphicView> views, NewViewDialog parent) {
    super(true);
    this.parent = parent;
    
    // Listeners.
    addMouseListener(this);
    addMouseMotionListener(this);
        
    // Window properties.
    setCursor(new Cursor(Cursor.HAND_CURSOR));
    setBackground(Color.GRAY);
    setPreferredSize(new Dimension(800, 600));
    
    // Create the diagram's names.
    for (GraphicView gv : views)
      diagramNames.add(new DiagramName(gv));
    
    ltnCreatDiagram = new LeftButton("Create a new view...");
    
    currentHover = diagramNames.get(0);
    
    repaint();
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    utility.Utility.setRenderQuality(g);
    
    Graphics2D g2d = (Graphics2D)g;
    paintDiagramNames(g2d);
  }
  
  private void paintDiagramNames(Graphics2D g2d) {
    
    AffineTransform origin = g2d.getTransform();
    
    // Draw the separation line.
    g2d.setColor(Slyum.THEME_COLOR);
    g2d.drawLine(
        DiagramName.DIAGRAM_NAME_WIDTH, 
        0,
        DiagramName.DIAGRAM_NAME_WIDTH, 
        getHeight());
    
    // Draw the right background.
    g2d.setColor(Color.WHITE);
    g2d.fillRect(
        DiagramName.DIAGRAM_NAME_WIDTH + 1, 
        0, 
        getWidth(), 
        getHeight());
    
    // Draw the border.
    g2d.setColor(Slyum.THEME_COLOR);
    g2d.drawRect(
        DiagramName.DIAGRAM_NAME_WIDTH - 1, 
        0, 
        getWidth()-(DiagramName.DIAGRAM_NAME_WIDTH), 
        getHeight()-1);
    
    // Draw the left background.
    g2d.setColor(Color.GRAY);
    g2d.fillRect(
        0, 
        0, 
        DiagramName.DIAGRAM_NAME_WIDTH, 
        getHeight());
    
    for (LeftButton dn : diagramNames) {
      dn.paint(g2d, dn == currentHover, dn == currentPressed);
      g2d.translate(0, LeftButton.DIAGRAM_NAME_HEIGHT);
    }
    
    // Button create a new view
    g2d.setTransform(origin);
    g2d.translate(0, getHeight() - DiagramName.DIAGRAM_NAME_HEIGHT);
    ltnCreatDiagram.paint(
        g2d, 
        ltnCreatDiagram == currentHover, 
        ltnCreatDiagram == currentPressed);
    
    g2d.setTransform(origin);
    paintDiagramOverview(g2d);
  }
  
  private void paintDiagramOverview(Graphics2D g2d) {
    
    g2d.translate(DiagramName.DIAGRAM_NAME_WIDTH + 1, 0);

    final int MINI_WIDTH = 300;
    BufferedImage image = currentHover.getImageOverview();
    
    //TODO a enlever
    if (image == null)
      return;

    int width = image.getWidth(), 
        height = image.getHeight();

    float ratio = height / (float)width;

    int miniHeight = (int)(MINI_WIDTH * ratio);

    BufferedImage thumbnail = Scalr.resize(
        image, 
        Scalr.Method.ULTRA_QUALITY,
        Scalr.Mode.AUTOMATIC,
        getWidth() - DiagramName.DIAGRAM_NAME_WIDTH, 
        getHeight());

    g2d.drawImage(
        thumbnail,
        0, 0, null);
  }
  
  private LeftButton getLeftButtonAtLocation(Point location) {
    
    // Is in the diagram's name column?
    if (location.x >= 0 && location.x <= LeftButton.DIAGRAM_NAME_WIDTH) {
      
      // Wich diagram's name is it?
      int index = location.y / LeftButton.DIAGRAM_NAME_HEIGHT;
      if (index < diagramNames.size())
        return diagramNames.get(index);
      else if (location.y >= getHeight() - DiagramName.DIAGRAM_NAME_HEIGHT) 
        return ltnCreatDiagram;
    }
    
    return currentHover;
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    getLeftButtonAtLocation(e.getPoint()).clicked();
  }

  @Override
  public void mousePressed(MouseEvent e) {
    currentPressed = getLeftButtonAtLocation(e.getPoint());
    repaint();
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    currentPressed = null;
    repaint();
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    
  }

  @Override
  public void mouseExited(MouseEvent e) {
    
  }

  @Override
  public void mouseDragged(MouseEvent e) {
    
  }

  @Override
  public void mouseMoved(MouseEvent e) {
    currentHover = getLeftButtonAtLocation(e.getPoint());
    repaint();
  }
    
  private class LeftButton {
  
    static final int DIAGRAM_NAME_WIDTH = 180;
    static final int DIAGRAM_NAME_HEIGHT = 30;
    
    private String title;
    
    public LeftButton(String title) {
      this.title = title;
    }
    
    public void clicked() {
      ViewViewer.this.parent.setVisible(false);
      MultiViewManager.addAndOpenNewView();
    }

    public void paint(
        Graphics2D g2d, boolean mouseHover, boolean mousePressed) {
      
      Color
          drawColor = getTextColor(),
          fillColor, 
          textColor;
      
      if (mouseHover) {
        fillColor = Color.WHITE;
        textColor = getTextColor();
      } else {
        fillColor = Color.GRAY;
        textColor = Color.WHITE;
      }
      
      g2d.setColor(fillColor);
      g2d.fillRect(0, 0, DIAGRAM_NAME_WIDTH, DIAGRAM_NAME_HEIGHT);
      
      if (mouseHover) {
        g2d.setColor(drawColor);
        g2d.drawRect(0, 0, DIAGRAM_NAME_WIDTH, DIAGRAM_NAME_HEIGHT-1);
        
        g2d.setColor(fillColor);
        g2d.drawLine(DIAGRAM_NAME_WIDTH, 1, DIAGRAM_NAME_WIDTH, DIAGRAM_NAME_HEIGHT-2);
      } else {
        g2d.setColor(Slyum.THEME_COLOR);
        g2d.drawLine(DIAGRAM_NAME_WIDTH, 0, DIAGRAM_NAME_WIDTH, DIAGRAM_NAME_HEIGHT-1);
      }
      
      g2d.setColor(textColor);
      g2d.drawString(title, 20, 20);
    }
    
    protected Color getTextColor() {
      return new Color(219, 25, 25);
    }
    
    protected BufferedImage getImageOverview() {
      return null;
    }
  }
  
  private class DiagramName extends LeftButton {
    
    private GraphicView graphicView;

    public DiagramName(GraphicView graphicView) {
      super(graphicView.getName());
      this.graphicView = graphicView;
    }
    
    @Override
    public void clicked() {
      ViewViewer.this.parent.setVisible(false);
      MultiViewManager.openView(graphicView);
    }
    
    @Override
    protected Color getTextColor() {
      return Slyum.THEME_COLOR;
    }
    
    @Override
    protected BufferedImage getImageOverview() {
      return graphicView.getScreen(BufferedImage.TYPE_INT_ARGB, false);
    }
  }
}
