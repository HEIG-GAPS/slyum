package swing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

import utility.SMessageDialog;

public class PropertyLoader {
    public static final String COLOR_ENTITIES = "colorEntities";
    public static final String COLOR_GRAPHIC_VIEW = "colorGraphicView";
    public static final String BACKGROUND_GRADIENT = "backgroundGradient";
    public static final String ENTITY_GRADIENT = "entityGradient";
    public static final String GRID_POINT_OPACITY = "GridPointOpacity";
    public static final String GRID_OPACITY_ENABLE = "gridOpacityEnable";
    public static final String SHOW_ERROR_MESSAGES = "ShowErrorMessages";
    public static final String SHOW_CROSS_MENU = "ShowCrossMenu";
    public static final String GRID_VISIBLE = "GridVisible";
    public static final String GRID_ENABLE = "GridEnable";
    public static final String GRAPHIC_QUALITY = "GraphicQuality";
    public static final String FONT_POLICE = "FontPolice";
    public static final String FONT_SIZE = "FontSize";
    public static final String AUTOMATIC_GRID_COLOR = "AutomaticGridColor";
    public static final String GRID_COLOR = "GridColor";
    public static final String GRID_SIZE = "gridSize";
    public static final String DIVIDER_LEFT = "dividerLeft";
    public static final String DIVIDER_BOTTOM = "dividerBottom";
    public static final String AUTO_ADJUST_INHERITANCE = "autoAdjustInheritance";
    public static final String MODE_CURSOR = "ModeCursor";
    public static final String VIEW_METHODS = "ViewMethods";
    public static final String VIEW_ENTITIES = "ViewEntities";

    public static final String filename = Slyum.getPathAppDir()
            + Slyum.FILE_SEPARATOR + "config.properties";

    private static PropertyLoader instance;

    private Properties properties = new Properties();

    private PropertyLoader() throws IOException {
        createPropertiesFile();

        final FileInputStream input = new FileInputStream(filename);

        properties.load(input);

        input.close();
    }

    public void createPropertiesFile() {
        final File file = new File(filename);

        try {
            file.createNewFile();
        } catch (final IOException e) {
            SMessageDialog.showErrorMessage("Error to create config file.");
        }
    }

    /**
     * Push properties in properties file.
     */
    public void push() {
        final File file = new File(filename);

        try {
            OutputStream out = new FileOutputStream(file);

            properties.store(out, "Generals properties of Slyum");
        } catch (FileNotFoundException e) {
            createPropertiesFile();
            push();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PropertyLoader getInstance() {
        if (instance == null)
            try {
                instance = new PropertyLoader();
            } catch (IOException e) {
                SMessageDialog
                     .showErrorMessage(SMessageDialog.ERROR_LOAD_PROPERTY_FILE);
            }
        return instance;
    }

    public void setProperty(Properties prop) {
        this.properties = prop;

        push();
    }

    public void reset() {
        final File file = new File(filename);

        file.delete();

        properties = new Properties();
    }

    public Properties getProperties() {
        return properties;
    }
}
