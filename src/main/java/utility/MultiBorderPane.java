package utility;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

/**
 * JavaFX equivalent of the Swing MultiBorderLayout. Extends {@link BorderPane} to support
 * multiple children in each compass region by stacking them inside a {@link VBox} (top/bottom)
 * or {@link HBox} (left/right). This mirrors the original behaviour of allowing several toolbars
 * (or other nodes) in the same border region.
 *
 * <p>Usage:
 * <pre>
 *   MultiBorderPane pane = new MultiBorderPane();
 *   pane.addTop(toolbar1);
 *   pane.addTop(toolbar2);
 *   pane.setCenter(canvas);
 * </pre>
 *
 * @author Stanislav Lapitsky (original idea)
 * @author Wanja Gayk (major rework)
 * @author migration (JavaFX port)
 */
public class MultiBorderPane extends BorderPane {

  private final VBox topBox = new VBox();
  private final VBox bottomBox = new VBox();
  private final HBox leftBox = new HBox();
  private final HBox rightBox = new HBox();

  /** Tracks center nodes so the most-recently added one is set as the BorderPane centre. */
  private final List<Node> centerNodes = new ArrayList<>();

  /** Constructs a MultiBorderPane with the default gap of 0. */
  public MultiBorderPane() {
    setTop(topBox);
    setBottom(bottomBox);
    setLeft(leftBox);
    setRight(rightBox);
  }

  /**
   * Constructs a MultiBorderPane with the given horizontal and vertical gap between stacked nodes.
   *
   * @param hgap horizontal gap (applied inside top/bottom VBox rows)
   * @param vgap vertical gap (applied inside left/right HBox columns)
   */
  public MultiBorderPane(double hgap, double vgap) {
    this();
    topBox.setSpacing(hgap);
    bottomBox.setSpacing(hgap);
    leftBox.setSpacing(vgap);
    rightBox.setSpacing(vgap);
  }

  /** Appends {@code node} to the top region. */
  public void addTop(Node node) {
    topBox.getChildren().add(node);
  }

  /** Appends {@code node} to the bottom region. */
  public void addBottom(Node node) {
    bottomBox.getChildren().add(node);
  }

  /** Appends {@code node} to the left region. */
  public void addLeft(Node node) {
    leftBox.getChildren().add(node);
  }

  /** Appends {@code node} to the right region. */
  public void addRight(Node node) {
    rightBox.getChildren().add(node);
  }

  /**
   * Adds {@code node} as a centre region child. Only the last added centre node is displayed at a
   * time (set on the underlying {@link BorderPane}).
   */
  public void addCenter(Node node) {
    centerNodes.add(node);
    setCenter(node);
  }

  /** Removes {@code node} from whichever region it belongs to. */
  public void removeNode(Node node) {
    topBox.getChildren().remove(node);
    bottomBox.getChildren().remove(node);
    leftBox.getChildren().remove(node);
    rightBox.getChildren().remove(node);
    if (centerNodes.remove(node)) {
      setCenter(centerNodes.isEmpty() ? null : centerNodes.get(centerNodes.size() - 1));
    }
  }

  // ----- gap accessors preserved from the original MultiBorderLayout API -----

  public double getHgap() {
    return topBox.getSpacing();
  }

  public void setHgap(double hgap) {
    topBox.setSpacing(hgap);
    bottomBox.setSpacing(hgap);
  }

  public double getVgap() {
    return leftBox.getSpacing();
  }

  public void setVgap(double vgap) {
    leftBox.setSpacing(vgap);
    rightBox.setSpacing(vgap);
  }

}
