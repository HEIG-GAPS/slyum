package i18n;

import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Unit tests for {@link Messages}.
 */
class MessagesTest {

    @Test
    void testGetReturnsTranslation() {
        Messages.reload(Locale.forLanguageTag("en-GB"));
        String value = Messages.get("app.name");
        assertEquals("Slyum", value);
    }

    @Test
    void testGetMissingKeyReturnsFallback() {
        Messages.reload(Locale.forLanguageTag("en-GB"));
        String value = Messages.get("this.key.does.not.exist");
        assertEquals("this.key.does.not.exist", value);
    }

    @Test
    void testGetWithFormatArgs() {
        Messages.reload(Locale.forLanguageTag("en-GB"));
        String value = Messages.get("dialog.error.open", "test.sly");
        assertNotNull(value);
        assert value.contains("test.sly") : "Formatted message should contain the argument";
    }

    @Test
    void testFrenchLocale() {
        Messages.reload(Locale.forLanguageTag("fr-CH"));
        String value = Messages.get("menu.file");
        assertEquals("Fichier", value);
    }

    @Test
    void testGermanLocale() {
        Messages.reload(Locale.forLanguageTag("de-CH"));
        String value = Messages.get("menu.file");
        assertEquals("Datei", value);
    }

    @Test
    void testItalianLocale() {
        Messages.reload(Locale.forLanguageTag("it-CH"));
        String value = Messages.get("menu.file");
        assertEquals("File", value);
    }

    @Test
    void testReloadRestoresLocale() {
        Messages.reload(Locale.forLanguageTag("fr-CH"));
        assertEquals("Fichier", Messages.get("menu.file"));

        Messages.reload(Locale.forLanguageTag("en-GB"));
        assertEquals("File", Messages.get("menu.file"));
    }
}
