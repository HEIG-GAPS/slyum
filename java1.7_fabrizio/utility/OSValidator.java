package utility;

/**
 * Detect wich os is installed on the system running Java.
 * 
 * @author
 *         http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname
 *         /
 * 
 */
public class OSValidator
{
	public static boolean isMac()
	{
		final String os = System.getProperty("os.name").toLowerCase();
		// Mac
		return os.indexOf("mac") >= 0;

	}

	public static boolean isUnix()
	{
		final String os = System.getProperty("os.name").toLowerCase();
		// linux or unix
		return os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0;

	}

	public static boolean isWindows()
	{
		final String os = System.getProperty("os.name").toLowerCase();
		// windows
		return os.indexOf("win") >= 0;

	}
}
