package appuni.explore.data;

import appuni.explore.domain.Catalog;

import org.json.JSONException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class CatalogParserTest {
    @Test
    public void fixtureMatchingTheSchemaIsAccepted() throws Exception {
        String text = read("/catalog-valid.json");
        Catalog catalog = CatalogParser.parse(text);
        assertEquals("host-university", catalog.host.id);
        assertEquals("Northern Spain University", catalog.partners.get(0).name);
        assertEquals(1, catalog.partners.size());
        assertEquals("https://example.com/northern-spain", catalog.partners.get(0).website);
    }

    @Test
    public void brokenFixtureIsRejected() throws Exception {
        String text = read("/catalog-broken.json");
        assertThrows(JSONException.class, () -> CatalogParser.parse(text));
    }

    private static String read(String name) throws Exception {
        InputStream stream = CatalogParserTest.class.getResourceAsStream(name);
        if (stream == null) {
            throw new IllegalStateException(name);
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line).append('\n');
            }
            return text.toString();
        }
    }
}
