
package swing;

import graphic.GraphicView;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Customized implementation of PrinterJob for needs of Slyum.
 * @author: David Miserez
 * @date: 05.11.2013
 */
public class SlyumPrinterJob {

  // Implementation of Singleton.
  private static SlyumPrinterJob instance;
  public static SlyumPrinterJob getSlyumPrinterJob() {
    if (instance == null)
      instance = new SlyumPrinterJob();
    
    return instance;
  }
  
  public static boolean print(GraphicView graphicView) 
          throws PrinterException {
    PrinterJob prnJob = getSlyumPrinterJob().printerJob;
    prnJob.setPrintable(
            graphicView, 
            SlyumPrinterJob.getSlyumPrinterJob().getPageFormat(graphicView));

    if (!prnJob.printDialog())
      return false;

    prnJob.print();
    return true;
  }
  
  public static void pageDialog(GraphicView graphicView) {
    SlyumPrinterJob spj = getSlyumPrinterJob();
    PageFormat pf = SlyumPrinterJob.getSlyumPrinterJob().getPageFormat(graphicView),
               pfTemp = spj.printerJob.pageDialog(pf);
    
    if (!pf.equals(pfTemp))
      spj.pageFormat = pfTemp;
  }
  
  private PrinterJob printerJob = PrinterJob.getPrinterJob();
  private PageFormat pageFormat = null;
  
  private SlyumPrinterJob() {
    
  }
  
  /**
   * Return the preferred orientation for the given graphicview.
   * @param graphicView graphicview for preferred orientation.
   * @return PageFormat.PORTRAIT or PageFormat.LANDSCAPE.
   */
  public static int getPreferredOrientation(GraphicView graphicView) {
    BufferedImage bi = graphicView.getScreen(BufferedImage.TYPE_INT_ARGB_PRE);
    Dimension sceneSize = new Dimension(bi.getWidth(), bi.getHeight());
    
    if (sceneSize.width > sceneSize.height)
      return PageFormat.LANDSCAPE;
    else
      return PageFormat.PORTRAIT;
  }
  
  private PageFormat getPageFormat(GraphicView graphicView) {
    if (pageFormat == null) {
      final int MARGIN = 36;
      PageFormat pf = getSlyumPrinterJob().printerJob.defaultPage();
      Paper paper = pf.getPaper();
      int orientation = getPreferredOrientation(graphicView);
    
      // Change default margins
      paper.setImageableArea(
              MARGIN, MARGIN, 
              paper.getWidth() - MARGIN * 2, 
              paper.getHeight() - MARGIN * 2);
      
      // Set PageFormat attributes.
      pf.setPaper(paper);
      pf.setOrientation(orientation);
      
      return pf;
    } else {
      return pageFormat;
    }
  }
}
