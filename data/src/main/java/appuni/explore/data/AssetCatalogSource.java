package appuni.explore.data;

import android.content.res.AssetManager;

import appuni.explore.domain.Catalog;
import appuni.explore.domain.MapFrame;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class AssetCatalogSource {
    private final AssetManager assets;

    public AssetCatalogSource(AssetManager assets) {
        this.assets = assets;
    }

    public Catalog catalog() throws IOException, JSONException {
        return CatalogParser.parse(read(assets.open("catalog/partners.json")));
    }

    public MapFrame mapFrame() throws IOException, JSONException {
        return CatalogParser.parseMapFrame(read(assets.open("maps/world-map.json")));
    }

    public InputStream openLogo(String logoFile) {
        try {
            return assets.open("catalog/logos/" + logoFile);
        } catch (IOException ignored) {
            return null;
        }
    }

    private static String read(InputStream stream) throws IOException {
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
