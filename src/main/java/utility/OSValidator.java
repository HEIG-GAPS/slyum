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
    return os.contains("mac");
  }

  private static boolean isUnix() {
    final String os = System.getProperty("os.name").toLowerCase();
    // linux or unix
    return os.contains("nix") || os.contains("nux");
  }

  private static boolean isWindows() {
    final String os = System.getProperty("os.name").toLowerCase();
    // windows
    return os.contains("win");
  }

}
