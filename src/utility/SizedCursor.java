package utility;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

public class SizedCursor
{
  public static Image getPreferredSizedCursor(
      Image image)
  {
    Dimension bestDimension = Toolkit
        .getDefaultToolkit()
        .getBestCursorSize(
          image
              .getWidth(null),
          image
              .getHeight(null));
    
    if (bestDimensionsEqualsImageSize(
      image,
      bestDimension))
    {
      return image;
    }
    else
    {
      BufferedImage resizedImage = new BufferedImage(
          bestDimension.width,
          bestDimension.height,
          BufferedImage.TYPE_INT_ARGB);
      Graphics2D g = (Graphics2D) resizedImage
          .getGraphics();
      
      g.drawImage(
        image, 0,
        0, null);
      
      return resizedImage;
    }
  }
  
  private static boolean bestDimensionsEqualsImageSize(
      Image image,
      Dimension bestDimension)
  {
    return bestDimension
        .getWidth() == image
        .getWidth(null)
        && bestDimension
            .getHeight() == image
            .getHeight(null);
  }
}
