package update;

import utility.TagDownload;
/**
 * Updater for Slyum.
 * @author David Miserez
 */
public class Updater {
  
  public static final String tagVersion = "[version]";
  public static final String tagHistory = "[history]";
    
  public static String getLatestVersion() throws Exception {
    return TagDownload.getContentTag(tagVersion);
  }
  
  public static String getWhatsNew() throws Exception {
    return TagDownload.getContentTag(tagHistory);
  }
}
