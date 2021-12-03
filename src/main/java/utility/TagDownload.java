package utility;

import java.io.InputStream;
import java.net.URL;

/**
 * @author David Miserez
 */
public class TagDownload {

  private static final String defaultUrl =
      "https://raw.githubusercontent.com/HEIG-GAPS/slyum/master/bin/utils/version.html";

  public static String getContentTag(String tag, String url) throws Exception {
    String data = getData(url);
    return data.substring(data.indexOf(tag) + tag.length(),
                          data.indexOf(createCloseTag(tag)));
  }

  public static String getContentTag(String tag) throws Exception {
    return getContentTag(tag, defaultUrl);
  }

  private static String getData(String address) throws Exception {
    URL url = new URL(address);
    InputStream html = url.openStream();
    int c = 0;
    StringBuilder buffer = new StringBuilder("");

    while (c != -1) {
      c = html.read();
      buffer.append((char) c);
    }
    return buffer.toString();
  }

  private static String createCloseTag(String openTag) {
    return openTag.substring(0, 1) + "/" +
           openTag.substring(1, openTag.length());
  }

}
