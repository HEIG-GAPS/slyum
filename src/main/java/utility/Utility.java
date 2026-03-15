package utility;

import classDiagram.components.Visibility;
import classDiagram.relationships.Multiplicity;
import graphic.GraphicComponent;
import javafx.geometry.Dimension2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ui.PropertyLoader;
import ui.SlyumApp;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.File;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

/**
 * Utilities class containing tools methods.
 *
 * @author David Miserez
 * @version 1.0 - 24.07.2011
 */
public class Utility {

  public static final GraphicQualityType GRAPHIQUE_QUALITY_TYPE = GraphicQualityType.MAX;

  /**
   * Set the given size for preferredSize, maximumSize and minimumSize to the given component.
   *
   * @param component the component to resize
   * @param width the desired width
   * @param height the desired height
   */
  public static void setAllSize(final Region component, final double width, final double height) {
    component.setMinWidth(width);
    component.setPrefWidth(width);
    component.setMaxWidth(width);
    component.setMinHeight(height);
    component.setPrefHeight(height);
    component.setMaxHeight(height);
  }

  /**
   * Set the given size for preferredSize, maximumSize and minimumSize to the given component.
   *
   * @param component the component to resize
   * @param size the size
   */
  public static void setAllSize(final Region component, final Dimension2D size) {
    setAllSize(component, size.getWidth(), size.getHeight());
  }

  public enum GraphicQualityType {
    LOW, MAX, MEDIUM
  }

  public static String proposeNewName(final String message) {
    TextInputDialog dialog = new TextInputDialog();
    dialog.setTitle("Slyum - Change name");
    dialog.setHeaderText(null);
    dialog.setContentText(message);
    Optional<String> result = dialog.showAndWait();
    return result.orElse("-1");
  }

  public static <T> int count(Class<?> type, List<T> list) {
    int count = 0;

    for (T component : list)
      if (type.isInstance(component)) count++;
    return count;
  }

  /**
   * Scale the given rectangle with the given scale.
   *
   * @param rect rectangle
   * @param scale scale
   *
   * @return the scaling rectangle.
   */
  public static Rectangle scaleRect(Rectangle rect, double scale) {
    Rectangle r = new Rectangle(rect);

    r.x *= scale;
    r.y *= scale;
    r.width *= scale;
    r.height *= scale;

    return r;
  }

  /**
   * Copy the given JavaFX Image to the system clipboard.
   *
   * @param image the image to copy
   */
  public static void copyImageToClipboard(javafx.scene.image.Image image) {
    ClipboardContent content = new ClipboardContent();
    content.putImage(image);
    Clipboard.getSystemClipboard().setContent(content);
  }

  public static String keystrokeToString(final String a) {
    StringBuilder result = new StringBuilder();
    String[] string = a.split(" ");

    for (int i = 0; i < string.length; i++) {
      String car = i == string.length - 1 ? "-" : "+";
      String s = string[i].substring(0, 1).toUpperCase();
      result.append(car.concat(s.concat(string[i].substring(1))));
    }

    return "(" + result.substring(1).toString() + ")"; // remove first "+"
  }

  public static Element boundsToXmlElement(final Document doc, final Rectangle bounds, final String tag) {
    Element element = doc.createElement(tag), x = doc.createElement("x"), y = doc.createElement(
        "y"), w = doc.createElement("w"), h = doc.createElement("h");

    x.setTextContent(String.valueOf(bounds.x));
    y.setTextContent(String.valueOf(bounds.y));
    w.setTextContent(String.valueOf(bounds.width));
    h.setTextContent(String.valueOf(bounds.height));

    element.appendChild(x);
    element.appendChild(y);
    element.appendChild(w);
    element.appendChild(h);

    return element;
  }

  public static Element pointToXmlElement(Point pt, String tag, Document doc) {
    Element element = doc.createElement(tag), x = doc.createElement("x"), y = doc.createElement("y");

    x.setTextContent(String.valueOf(pt.x));
    y.setTextContent(String.valueOf(pt.y));

    element.appendChild(x);
    element.appendChild(y);

    return element;
  }

  /**
   * Compute a point on the segment defined by p1 and p2 that are the nearest point from p3. This algorithm was found on
   * : http://paulbourke.net/geometry/pointline/
   *
   * @param p1 the first point defined a segment
   * @param p2 the second point defined a segment
   * @param p3 the point used for find the nearest point on the segment (p1, p2)
   *
   * @return the {@link Point2D} instance of the nearest point.
   */
  public static Point2D distanceToSegment(final Point2D p1, final Point2D p2, final Point2D p3) {
    final double xDelta = p2.getX() - p1.getX();
    final double yDelta = p2.getY() - p1.getY();

    if (xDelta == 0 && yDelta == 0)

      return p1;

    final double u =
        ((p3.getX() - p1.getX()) * xDelta + (p3.getY() - p1.getY()) * yDelta) / (xDelta * xDelta + yDelta * yDelta);

    Point2D closestPoint;

    if (u < 0)

      closestPoint = p1;

    else if (u > 1)

      closestPoint = p2;

    else

      closestPoint = new Point2D.Double(p1.getX() + u * xDelta, p1.getY() + u * yDelta);

    return closestPoint;
  }

  public static Point2D.Double getLinesIntersection(Line2D.Double firstLine, Line2D.Double secondLine) {
    Point2D a = firstLine.getP1(), b = firstLine.getP2(), c = secondLine.getP1(), d = secondLine.getP2();

    if (!firstLine.intersectsLine(secondLine)) return null;

    double denom = ((a.getX() - b.getX()) * (c.getY() - d.getY())) - ((a.getY() - b.getY()) * (c.getX() - d.getX()));

    if (denom == 0) return null;

    double axbyaybx = (a.getX() * b.getY()) - (a.getY() * b.getX());
    double cxdycydx = (c.getX() * d.getY()) - (c.getY() * d.getX());

    double intersectX = ((axbyaybx * (c.getX() - d.getX())) - ((a.getX() - b.getX()) * cxdycydx)) / denom;

    double intersecty = ((axbyaybx * (c.getY() - d.getY())) - ((a.getY() - b.getY()) * cxdycydx)) / denom;

    return new Point2D.Double(intersectX, intersecty);
  }

  public static Point2D.Double getPointOnLineByDistance(Line2D.Double line, double distance) {

    Point2D source = line.getP1(), target = line.getP2();

    final double deltaX = target.getX() - source.getX();
    final double deltaY = target.getY() - source.getY();
    final double alpha = getLineAngleRadian(line);
    final double length = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

    double x = Math.cos(alpha) * (length + distance) + source.getX();
    double y = Math.sin(alpha) * (length + distance) + source.getY();

    return new Point2D.Double(x, y);
  }

  public static double getLineAngleRadian(Line2D line) {
    Point2D source = line.getP1(), target = line.getP2();
    final double deltaX = target.getX() - source.getX();
    final double deltaY = target.getY() - source.getY();
    return Math.atan2(deltaY, deltaX);
  }

  public static double getLineAngleDegree(Line2D line) {
    return Math.toDegrees(getLineAngleRadian(line));
  }

  /**
   * Find a MenuItem in the ContextMenu given that corresponds to the given text. Returns null if
   * no MenuItem is found. The given text does not need to be the exact title; the comparison uses
   * startsWith().
   *
   * @param menu the context menu where to search for the MenuItem
   * @param text the title prefix of the MenuItem to find
   *
   * @return the MenuItem found; or null if none matches.
   */
  public static MenuItem findMenuItem(ContextMenu menu, String text) {
    for (MenuItem item : menu.getItems())
      if (item.getText() != null && item.getText().startsWith(text))
        return item;

    return null;
  }

  /**
   * Return a string containing a 'number' of '\t'.
   *
   * @param number the number of '\t'
   *
   * @return a string with 'number' tabs
   */
  public static String generateTab(int number) {
    String tab = "";

    for (int i = 0; i < number; i++)
      tab += "\t";

    return tab;
  }

  /**
   * Return the complementary color. The complementary color is calculated in the way defined here:
   * http://help.adobe.com/fr_FR/Illustrator/13.0/help.html?content=WS714a382cdf7d304e7e07d0100196cbc5f-6288.html
   *
   * @param color the color to compute its complementary
   *
   * @return the complementary color
   */
  public static Color getComplementaryColor(Color color) {
    final double r = color.getRed();
    final double g = color.getGreen();
    final double b = color.getBlue();

    final double min = Math.min(Math.min(r, g), b);
    final double max = Math.max(Math.max(r, g), b);

    final double add = min + max;

    return new Color(
        clamp(add - r),
        clamp(add - g),
        clamp(add - b),
        color.getOpacity());
  }

  private static double clamp(double value) {
    return Math.max(0.0, Math.min(1.0, value));
  }

  /**
   * Get the extension of a file. Return null if the file has no extension. (find on : http://download.oracle.com/)
   *
   * @param f the file to find its extension
   *
   * @return the extension of the file; or null if no extension are found.
   */
  public static String getExtension(File f) {
    String ext = null;
    final String s = f.getName();
    final int i = s.lastIndexOf('.');

    if (i > 0 && i < s.length() - 1) ext = s.substring(i + 1).toLowerCase();
    return ext;
  }

  public static GraphicQualityType getGraphicQualityType() {
    String graphiqueQuality = PropertyLoader.getInstance().getProperties().getProperty("GraphicQuality");
    GraphicQualityType gqt = GRAPHIQUE_QUALITY_TYPE;

    if (graphiqueQuality != null) gqt = GraphicQualityType.valueOf(graphiqueQuality);

    return gqt;
  }

  /**
   * Compute the gray level of a color. The gray level is calculated by adding the highest RGB
   * component with the lowest and divided by 2. The gray level is a float value between 0.0 and
   * 1.0f. This method returns 100 if the gray level is less than 0.5, 200 otherwise. It permits
   * finding a gray color that is visible on the given color (except if the color is a gray near
   * 127 in 0-255 terms).
   *
   * @param color the {@link Color}.
   *
   * @return 100 for a light background, 200 for a dark background.
   */
  public static int getColorGrayLevel(final Color color) {
    final double rp = color.getRed();
    final double gp = color.getGreen();
    final double bp = color.getBlue();

    final double max = Math.max(Math.max(rp, gp), bp);
    final double min = Math.min(Math.min(rp, gp), bp);

    return (max + min) / 2.0 > 0.5 ? 100 : 200;
  }

  /**
   * Return a set of keys that corresponding to the given value in a Map. Find on :
   * http://stackoverflow.com/questions/1383797/java-hashmap-how-to-get-key -from-value
   *
   * @param <T> the type of the keys.
   * @param <E> the type of the values.
   * @param map the {@link Map} to retrieve keys.
   * @param value the value.
   *
   * @return a {@link Set} containing the keys.
   */
  public static <T, E> Set<T> getKeysByValue(final Map<T, E> map, final E value) {
    final Set<T> keys = new HashSet<>();
    for (final Entry<T, E> entry : map.entrySet())
      if (entry.getValue().equals(value)) keys.add(entry.getKey());
    return keys;
  }

  /**
   * Return a rectangle englobing all graphic components from the given list.
   *
   * @param components the graphic components
   *
   * @return a rectangle englobing all graphic components from the given list
   */
  public static Rectangle getLimits(LinkedList<? extends GraphicComponent> components) {
    if (components.size() == 0) return new Rectangle();

    int top = Integer.MAX_VALUE;
    int left = Integer.MAX_VALUE;
    int right = Integer.MIN_VALUE;
    int bottom = Integer.MIN_VALUE;

    for (final GraphicComponent c : components) {
      final Rectangle current = c.getBounds();

      // find lower y value
      if (top > current.y) top = current.y;

      // find lower x value
      if (left > current.x) left = current.x;

      // find rightmost x value (for width)
      if (right < current.x + current.width) right = current.x + current.width;

      // find hightest y value (for hight)
      if (bottom < current.y + current.height) bottom = current.y + current.height;
    }

    return new Rectangle(left, top, right - left, bottom - top);
  }

  /**
   * Return a ComboBox containing all default multiplicities.
   *
   * @return a ComboBox containing all default multiplicities
   */
  public static ComboBox<Multiplicity> getMultiplicityComboBox() {
    ComboBox<Multiplicity> cmb = new ComboBox<>();
    cmb.setEditable(true);

    cmb.getItems().addAll(
        Multiplicity.ONE_ONLY,
        Multiplicity.ONE_OR_MORE,
        Multiplicity.ZERO,
        Multiplicity.ZERO_OR_MORE,
        Multiplicity.ZERO_OR_ONE);

    cmb.getSelectionModel().selectFirst();

    return cmb;
  }

  /**
   * Return a ComboBox containing all default visibilities.
   *
   * @return a ComboBox containing all default visibilities
   */
  public static ComboBox<String> getVisibilityComboBox() {
    ComboBox<String> cmb = new ComboBox<>();

    for (Visibility v : Visibility.values())
      cmb.getItems().add(v.getName());

    return cmb;
  }

  /**
   * Grow rectangle in this way: x-size, y-size, width+size*2, height+size*2.
   *
   * @param rect Rectangle to grow.
   * @param size Size to add.
   *
   * @return The rectangle elarged.
   */
  public static Rectangle growRectangle(Rectangle rect, int size) {
    final int size2 = size * 2;

    return new Rectangle(rect.x - size, rect.y - size, rect.width + size2, rect.height + size2);
  }

  /**
   * Normalize the given rect. If the given rectangle have some negative values, compute a new rectangle having the same
   * bounds, but with only positive values.
   *
   * @param rect the rectangle to normalize
   *
   * @return the normalized rectangle
   */
  public static Rectangle normalizeRect(Rectangle rect) {
    final Rectangle normalizedRect = new Rectangle();

    normalizedRect.width = Math.abs(rect.width);
    normalizedRect.height = Math.abs(rect.height);

    normalizedRect.x = rect.width < 0 ? rect.x + rect.width : rect.x;
    normalizedRect.y = rect.height < 0 ? rect.y + rect.height : rect.y;

    return normalizedRect;
  }

  /**
   * Put the default render quality settings on the given GraphicsContext.
   *
   * @param gc the JavaFX GraphicsContext
   */
  public static void setDefaultRenderQuality(GraphicsContext gc) {
    // Disable image smoothing for default (unenhanced) quality
    gc.setImageSmoothing(false);
  }

  public static void setGraphicQualityType(GraphicQualityType type) {
    PropertyLoader.getInstance().getProperties().put("GraphicQuality", type);
    PropertyLoader.getInstance().push();
  }

  /**
   * Apply the Slyum render quality settings to the given GraphicsContext.
   *
   * @param gc the JavaFX GraphicsContext
   */
  public static void setRenderQuality(final GraphicsContext gc) {
    if (getGraphicQualityType().equals(GraphicQualityType.LOW)) {
      gc.setImageSmoothing(false);
      return;
    }

    // MEDIUM and MAX quality: enable image smoothing
    gc.setImageSmoothing(true);
  }

  /**
   * Install an ESC-key handler on the given JavaFX {@link javafx.scene.Scene}.
   *
   * @param scene   the Scene that should respond to the ESC key
   * @param handler the handler invoked when ESC is pressed
   */
  public static void setSceneActionOnEsc(javafx.scene.Scene scene,
      javafx.event.EventHandler<KeyEvent> handler) {
    scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
      if (event.getCode() == KeyCode.ESCAPE) {
        handler.handle(event);
      }
    });
  }

  /**
   * Truncate the given text so its rendered width does not exceed the specified width. Uses a
   * JavaFX {@link Text} node to measure text size. If truncation is necessary {@code "..."} is
   * appended at the end.
   *
   * @param font  the font used to measure the text
   * @param text  the text to truncate
   * @param width the maximum allowed width in pixels
   *
   * @return the (possibly truncated) text; or an empty string if even {@code "..."} does not fit.
   */
  public static String truncate(Font font, String text, double width) {
    final String carTrunc = "...";
    final Text textNode = new Text(text);
    textNode.setFont(font);

    if (textNode.getBoundsInLocal().getWidth() < width) return text;

    text += carTrunc;

    do {
      if (text.length() <= 3) return ""; // "..." alone is wider than the allowed width

      text = text.substring(0, text.length() - carTrunc.length() - 1) + carTrunc;
      textNode.setText(text);
    } while (textNode.getBoundsInLocal().getWidth() > width);

    return text;
  }

  /**
   * Return a copy of {@code color} with the given alpha value applied as opacity.
   *
   * @param color the base color (RGB components are preserved)
   * @param alpha opacity in the 0–255 range (0 = fully transparent, 255 = fully opaque)
   *
   * @return a new {@link Color} with the same RGB values and the specified opacity
   */
  protected static Color getAlphaColor(Color color, int alpha) {
    return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha / 255.0);
  }

  public static void drawInfoRect(String text, Rectangle bounds, GraphicsContext gc, int offset) {
    setRenderQuality(gc);
    Color color = Color.rgb(100, 100, 100, 50.0 / 255.0);
    Color colorText = Color.rgb(20, 20, 20, 150.0 / 255.0);

    gc.setFill(color);
    gc.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

    Font font = Font.font(SlyumApp.getDefaultFont().getFamily(), 16);
    gc.setFont(font);
    gc.setFill(colorText);

    Text textNode = new Text(text);
    textNode.setFont(font);
    double stringWidth = textNode.getBoundsInLocal().getWidth();

    gc.fillText(text,
                (bounds.x + bounds.width - stringWidth) / 2,
                bounds.y + Math.min(bounds.height, offset));
  }

  public static String stripAccents(String s) {
    s = Normalizer.normalize(s, Normalizer.Form.NFD);
    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    return s;
  }

  public static String adaptOSKeyStroke(String accelerator) {

    if (accelerator != null && accelerator.contains("ctrl") && OSValidator.IS_MAC) {
      accelerator = accelerator.replace("ctrl", "meta");
      return accelerator.replace("control", "meta");
    }

    return accelerator;
  }

}
