package utility;

import com.vdurmont.semver4j.Semver;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/** Utility class to read the {@code pom.xml} file. */
public final class POMReader {

  /** The unique instance. */
  private static POMReader instance = null;

  /** @return the unique instance. */
  public static POMReader getInstance() {
    if (instance == null) instance = new POMReader();
    return instance;
  }

  /** The version of the app. */
  private String version;

  /** Creates a new {@link POMReader} instance. */
  private POMReader() {
    final File file = new File("pom.xml");
    if (file.exists() && file.canRead()) {
      MavenXpp3Reader reader = new MavenXpp3Reader();
      try {
        version = reader.read(new FileReader("pom.xml")).getVersion();
      } catch (IOException | XmlPullParserException e) {
        /* Do nothing, model will be null and this will be handled below. */
      }
    }

    if (version == null) {
      version = getClass().getPackage().getImplementationVersion();
    }

    if (version == null) {
      version = "Unknown";
    }
  }

  /** @return the version, as stated in the {@code pom.xml} file. */
  public Semver getVersion() { return new Semver(version); }

}
