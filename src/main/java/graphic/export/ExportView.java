package graphic.export;

import graphic.GraphicComponent;
import graphic.GraphicView;
import swing.PanelClassDiagram;
import swing.Slyum;
import utility.Utility;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.LinkedList;

import static graphic.GraphicView.DEFAULT_TITLE_BORDER_WIDTH;
import static graphic.GraphicView.isTitleBorderPainted;

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

    // Paint diagram's informations
    String informations = PanelClassDiagram.getInstance().getClassDiagram().getInformations();
    if (Slyum.isDisplayedDiagramInformationOnExport() && !informations.isEmpty()) {

      final int WIDTH = 250;
      final int INFORMATIONS_PADDING = 5;
      final int INFORMATION_MARGIN = 10;
      final int ROUNDED = 10;

      g2d.setStroke(new BasicStroke(DEFAULT_TITLE_BORDER_WIDTH));

      FontRenderContext frc = g2d.getFontRenderContext();
      AttributedString styledText = new AttributedString(informations);
      AttributedCharacterIterator iterator = styledText.getIterator();
      LineBreakMeasurer measurer = new LineBreakMeasurer(iterator, frc);
      int start = iterator.getBeginIndex(),
          end = iterator.getEndIndex();

      measurer.setPosition(start);

      int REAL_WIDTH = WIDTH - INFORMATIONS_PADDING * 2;

      // Compute height
      float height = 0,
          width = 0;
      while (measurer.getPosition() < end) {
        TextLayout layout = measurer.nextLayout(REAL_WIDTH, getLimitAtReturnChar(measurer, REAL_WIDTH, informations),
                                                true);

        if (width < layout.getAdvance())
          width = layout.getAdvance();

        height += layout.getAscent() + layout.getDescent() + layout.getLeading();
      }

      width = Math.min(width, REAL_WIDTH);

      width += INFORMATIONS_PADDING * 2;
      height += INFORMATIONS_PADDING * 2;

      Rectangle outerBounds = getOuterBounds();

      Rectangle informationsRectangle = new Rectangle(
          outerBounds.x + outerBounds.width - (int) width - DEFAULT_TITLE_BORDER_WIDTH - INFORMATION_MARGIN,
          outerBounds.y + outerBounds.height - (int) height - DEFAULT_TITLE_BORDER_WIDTH - INFORMATION_MARGIN,
          (int) width, (int) height);

      // Draw border and background
      g2d.setColor(new Color(250, 250, 250));
      g2d.fillRoundRect(informationsRectangle.x,
                        informationsRectangle.y,
                        informationsRectangle.width,
                        informationsRectangle.height,
                        ROUNDED,
                        ROUNDED);

      g2d.setColor(Color.BLACK);
      g2d.drawRoundRect(informationsRectangle.x,
                        informationsRectangle.y,
                        informationsRectangle.width,
                        informationsRectangle.height,
                        ROUNDED,
                        ROUNDED);

      // Draw text
      float y = informationsRectangle.y + INFORMATIONS_PADDING,
          x = informationsRectangle.x + INFORMATIONS_PADDING;

      g2d.setColor(new Color(50, 50, 50));

      measurer.setPosition(0);
      while (measurer.getPosition() < end) {

        TextLayout layout = measurer.nextLayout(REAL_WIDTH, getLimitAtReturnChar(measurer, REAL_WIDTH, informations),
                                                true);

        y += layout.getAscent();
        layout.draw(g2d, x, y);
        y += layout.getDescent() + layout.getLeading();
      }
    }

    graphicView.setPictureMode(false);
    return g2d;
  }

  private int getLimitAtReturnChar(LineBreakMeasurer measurer, int width, String text) {

    int next = measurer.nextOffset(width);
    int limit = next;

    if (limit <= text.length())
      for (int i = measurer.getPosition(); i < next; ++i)
        if (text.charAt(i) == '\n') {
          limit = i + 1;
          break;
        }

    return limit;
  }

}
