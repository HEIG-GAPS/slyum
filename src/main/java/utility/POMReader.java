package utility;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

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

  /** The {@link Model} that will store the content of the {@code pom.xml} file. */
  private Model model;

  /** Creates a new {@link POMReader} instance. */
  private POMReader() {
    MavenXpp3Reader reader = new MavenXpp3Reader();
    try {
      model = reader.read(new FileReader("pom.xml"));
    } catch (IOException | XmlPullParserException e) {
      /* Do nothing, model will be null and this will be handled below. */
    }
  }

  /** @return the version, as stated in the {@code pom.xml} file. */
  public String getVersion() { return model == null ? "0.0.0" : model.getVersion(); }

}
