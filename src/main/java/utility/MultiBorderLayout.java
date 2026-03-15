package utility;

/**
 * Backward-compatibility shim retained during the Swing→JavaFX migration.
 *
 * <p>The Swing {@code MultiBorderLayout} (a {@code BorderLayout} sub-class) has been replaced by
 * {@link MultiBorderPane}, a {@code BorderPane}-based JavaFX container. Callers in the
 * {@code swing} package that reference {@code MultiBorderLayout} should be updated to
 * {@link MultiBorderPane} when those packages are migrated.
 *
 * @deprecated Use {@link MultiBorderPane} instead.
 */
@Deprecated
public class MultiBorderLayout extends MultiBorderPane {

  public MultiBorderLayout() {
    super();
  }

  public MultiBorderLayout(double hgap, double vgap) {
    super(hgap, vgap);
  }

}
