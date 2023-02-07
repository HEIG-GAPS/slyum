package update;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vdurmont.semver4j.Semver;
import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.net.URL;

/** {@code LatestRelease} is the Object representation of the response to the GitHub API latest release request. */
@JsonIgnoreProperties({
    "url", "assets_url", "upload_url", "id", "author", "node_id", "target_commitish", "name", "draft", "prerelease",
    "created_at", "published_at", "assets", "tarball_url", "zipball_url"})
public final class LatestRelease {

  /** The {@link URL} to redirect the user into, to download the last release assets. */
  private URL url;
  /** The release tag, to identify the version. */
  private String releaseTag;
  /** The {@link Semver} instance, representing the version (acts as a singleton). */
  private Semver version;
  /** The description of the release. */
  private String description;
  /** The description, converted into HTML (acts as a singleton). */
  private String descriptionAsHtml;

  /**
   * Creates a new {@link update.LatestRelease} instance.
   *
   * @param url the {@link URL} to redirect the user into, to download the last release assets.
   * @param releaseTag the release tag, to identify the version.
   * @param description the description of the release.
   */
  public LatestRelease(@JsonProperty("html_url") final URL url,
                       @JsonProperty("tag_name") final String releaseTag,
                       @JsonProperty("body") final String description) {
    this.url = url;
    this.releaseTag = releaseTag;
    this.description = description;
  }

  /** @return the {@link URL} to redirect the user into, to download the last release assets. */
  public URL getUrl() { return url; }

  /**
   * Sets the {@link URL} to redirect the user into, to download the last release assets.
   *
   * @param url the release {@link URL}.
   *
   * @return this instance to chain setter calls.
   */
  public LatestRelease setUrl(final URL url) {
    this.url = url;
    return this;
  }

  /** @return the release tag, to identify the version. */
  public String getReleaseTag() { return releaseTag; }

  /**
   * Sets the release tag, to identify the version.
   *
   * @param releaseTag the release tag.
   *
   * @return this instance to chain setter calls.
   */
  public LatestRelease setReleaseTag(final String releaseTag) {
    this.releaseTag = releaseTag;
    version = null;
    return this;
  }

  /** @return the description of the release. */
  public String getDescription() { return description; }

  /**
   * Sets the description of the release.
   *
   * @param description the description of the release.
   *
   * @return this instance to chain setter calls.
   */
  public LatestRelease setDescription(final String description) {
    this.description = description;
    descriptionAsHtml = null;
    return this;
  }

  /** @return the {@link Semver} instance, corresponding to the release version. */
  public Semver getVersion() {
    if (releaseTag != null && version == null) {
      version = new Semver(releaseTag.split("slyum-")[1]);
    }
    return version;
  }

  /** @return the description of the release, converted into HTML. */
  public String getDescriptionAsHtml() {
    if (description != null && descriptionAsHtml == null) {
      Parser parser = Parser.builder().build();
      Node document = parser.parse("This is *Sparta*");
      HtmlRenderer renderer = HtmlRenderer.builder().build();
      descriptionAsHtml = renderer.render(document);
    }
    return descriptionAsHtml;
  }

}
