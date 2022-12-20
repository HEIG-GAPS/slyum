package update;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.semver4j.Semver;

import java.io.IOException;
import java.net.URL;

/** Updater for Slyum. */
public final class Updater {

  private static Updater instance;

  public static Updater getInstance() {
    if (instance == null) instance = new Updater();
    return instance;
  }

  /** The GitHub API end point to get the latest release. */
  private static final String GITHUB_LATEST_RELEASE_URL = "https://api.github.com/repos/HEIG-GAPS/slyum/releases" +
                                                          "/latest";

  /** The {@link LatestRelease} instance, created from the GitHub end point. */
  private final LatestRelease latestRelease;

  /** Utility class. */
  private Updater() {
    LatestRelease tempLatestRelease;

    try {
      final URL url = new URL(GITHUB_LATEST_RELEASE_URL);
      final ObjectMapper objectMapper = new ObjectMapper();
      tempLatestRelease = objectMapper.readValue(url, LatestRelease.class);
    } catch (IOException e) {
      tempLatestRelease = null;
      /* Do nothing, nullity will be handled. */
    }
    latestRelease = tempLatestRelease;
  }

  public boolean couldContactServer() { return latestRelease != null; }

  public Semver getLatestVersion() {
    return latestRelease.getVersion();
  }

  public String getWhatsNew() {
    return latestRelease.getDescriptionAsHtml();
  }

  /** @return the {@link URL} where to redirect the user, so he will be able to download the latest binaries. */
  public URL getRedirectUrl() { return latestRelease.getUrl(); }

}
