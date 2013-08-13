package utility;

/**
 * Detect wich os is installed on the system running Java.
 */
public class OSValidator {

  public static boolean IS_MAC = isMac();
  public static boolean IS_UNIX = isUnix();
  public static boolean IS_WINDOWS = isWindows();

  private static boolean isMac() {
    final String os = System.getProperty("os.name").toLowerCase();
    // Mac
    return os.indexOf("mac") >= 0;

  }

  private static boolean isUnix() {
    final String os = System.getProperty("os.name").toLowerCase();
    // linux or unix
    return os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;

  }

  private static boolean isWindows() {
    final String os = System.getProperty("os.name").toLowerCase();
    // windows
    return os.indexOf("win") >= 0;
  }
}
