package utility;

import javafx.scene.ImageCursor;
import javafx.scene.image.Image;

/**
 * Utility for creating custom-sized cursors in JavaFX.
 */
public class SizedCursor {

  /**
   * Create a JavaFX {@link ImageCursor} from the given image. The hotspot is placed at the centre
   * of the image. JavaFX handles cursor-size negotiation with the platform automatically.
   *
   * @param image the cursor image
   *
   * @return an {@link ImageCursor} backed by the given image
   */
  public static ImageCursor getPreferredSizedCursor(Image image) {
    double hotspotX = image.getWidth() / 2;
    double hotspotY = image.getHeight() / 2;
    return new ImageCursor(image, hotspotX, hotspotY);
  }

}

