package i18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Centralised access to localised messages.
 *
 * <p>Supported locales (resolved automatically from the OS):
 * <ul>
 *   <li>fr-CH – Swiss French</li>
 *   <li>en-GB – British English</li>
 *   <li>it-CH – Swiss Italian</li>
 *   <li>de-CH – Swiss German</li>
 * </ul>
 *
 * <p>Usage:
 * <pre>{@code
 *   String label = Messages.get("menu.file");
 * }</pre>
 */
public final class Messages {

    private static final String BUNDLE_BASE_NAME = "i18n/messages";

    private static ResourceBundle bundle;

    static {
        reload(Locale.getDefault());
    }

    private Messages() {
        // utility class
    }

    /**
     * Reload the resource bundle for the given locale.
     *
     * @param locale the locale to load
     */
    public static void reload(final Locale locale) {
        bundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale);
    }

    /**
     * Returns the localised string for the given key. Falls back to the key
     * itself when no translation is available so that the application never
     * throws at runtime.
     *
     * @param key the message key
     * @return the localised string, or the key if the translation is missing
     */
    public static String get(final String key) {
        try {
            return bundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Returns the localised string formatted with the supplied arguments.
     *
     * @param key  the message key
     * @param args format arguments passed to {@link String#format}
     * @return the formatted localised string
     */
    public static String get(final String key, final Object... args) {
        return String.format(get(key), args);
    }
}
