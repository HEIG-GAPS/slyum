package swing;

import graphic.GraphicView;
import graphic.export.ExportViewImage;
import org.imgscalr.Scalr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

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

    Graphics2D g2d = (Graphics2D) g;
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
        getWidth() - (DiagramName.DIAGRAM_NAME_WIDTH),
        getHeight() - 1);

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

    final int CONTENT_WIDTH = getWidth() - DiagramName.DIAGRAM_NAME_WIDTH,
        CONTENT_HEIGHT = getHeight();

    g2d.translate(DiagramName.DIAGRAM_NAME_WIDTH + 1, 0);

    BufferedImage image = currentHover.getImageOverview();

    if (image == null) {
      final String EMPTY_DIAGRAM_MESSAGE = currentHover.getNoImageOverviewMessage();

      g2d.setColor(Color.black);
      g2d.setFont(g2d.getFont().deriveFont(18.0f));
      FontMetrics fm = g2d.getFontMetrics();
      int widthMessage = fm.stringWidth(EMPTY_DIAGRAM_MESSAGE),
          heightMessage = fm.getAscent() + fm.getDescent();

      g2d.drawString(EMPTY_DIAGRAM_MESSAGE, (CONTENT_WIDTH - widthMessage) / 2,
                     (CONTENT_HEIGHT - heightMessage) / 2);
      return;
    }

    int imgWidth = image.getWidth(),
        imgHeight = image.getHeight();

    if (imgWidth > CONTENT_WIDTH) {
      imgHeight = (CONTENT_WIDTH * imgHeight) / imgWidth;
      imgWidth = CONTENT_WIDTH;
    }

    if (imgHeight > CONTENT_HEIGHT) {
      imgWidth = (CONTENT_HEIGHT * imgWidth) / imgHeight;
      imgHeight = CONTENT_HEIGHT;
    }

    BufferedImage thumbnail = Scalr.resize(
        image,
        Scalr.Method.ULTRA_QUALITY,
        Scalr.Mode.AUTOMATIC,
        imgWidth,
        imgHeight);

    int thumbX = (CONTENT_WIDTH - thumbnail.getWidth()) / 2,
        thumbY = (CONTENT_HEIGHT - thumbnail.getHeight()) / 2;

    g2d.drawImage(thumbnail, thumbX, thumbY, null);
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

    static final String NO_IMAGE_MESSAGE = "click to create a new view...";
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
        g2d.drawRect(0, 0, DIAGRAM_NAME_WIDTH, DIAGRAM_NAME_HEIGHT - 1);

        g2d.setColor(fillColor);
        g2d.drawLine(DIAGRAM_NAME_WIDTH, 1, DIAGRAM_NAME_WIDTH, DIAGRAM_NAME_HEIGHT - 2);
      } else {
        g2d.setColor(Slyum.THEME_COLOR);
        g2d.drawLine(DIAGRAM_NAME_WIDTH, 0, DIAGRAM_NAME_WIDTH, DIAGRAM_NAME_HEIGHT - 1);
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

    protected String getNoImageOverviewMessage() {
      return NO_IMAGE_MESSAGE;
    }

  }

  private class DiagramName extends LeftButton {

    static final String NO_IMAGE_MESSAGE = "empty diagram";

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
      if (graphicView.getAllDiagramComponents().isEmpty())
        return null;
      return ExportViewImage.create(graphicView, false).export();
    }

    @Override
    protected String getNoImageOverviewMessage() {
      return NO_IMAGE_MESSAGE;
    }

  }

}
