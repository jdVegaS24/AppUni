package appuni.explore.data;

import appuni.explore.domain.Agreement;
import appuni.explore.domain.Catalog;
import appuni.explore.domain.CatalogRules;
import appuni.explore.domain.HostUniversity;
import appuni.explore.domain.MapFrame;
import appuni.explore.domain.PartnerUniversity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class CatalogParser {
    private CatalogParser() {
    }

    public static Catalog parse(String text) throws JSONException {
        JSONObject root = new JSONObject(text);
        JSONObject host = root.getJSONObject("host");
        JSONArray partnersJson = root.getJSONArray("partners");
        List<PartnerUniversity> partners = new ArrayList<>();
        for (int index = 0; index < partnersJson.length(); index++) {
            JSONObject partner = partnersJson.getJSONObject(index);
            JSONObject agreement = partner.getJSONObject("agreement");
            partners.add(new PartnerUniversity(
                    partner.getString("id"),
                    partner.getString("name"),
                    partner.getString("city"),
                    partner.getString("country"),
                    partner.getDouble("latitude"),
                    partner.getDouble("longitude"),
                    new Agreement(agreement.getString("type"), agreement.getString("description")),
                    optionalString(partner, "logoFile"),
                    optionalString(partner, "website")
            ));
        }
        Catalog catalog = new Catalog(
                new HostUniversity(host.getString("id"), host.getString("name")),
                partners
        );
        List<String> problems = CatalogRules.violations(catalog);
        if (!problems.isEmpty()) {
            throw new IllegalArgumentException(String.join(", ", problems));
        }
        return catalog;
    }

    public static MapFrame parseMapFrame(String text) throws JSONException {
        JSONObject file = new JSONObject(text);
        return new MapFrame(
                file.getString("id"),
                file.getString("imageFile"),
                file.getDouble("physicalWidthMeters"),
                file.getInt("imagePixelWidth"),
                file.getInt("imagePixelHeight"),
                file.getString("projection"),
                file.getString("north"),
                file.getDouble("west"),
                file.getDouble("east"),
                file.getDouble("south"),
                file.getDouble("northLat"),
                file.getDouble("contentLeft"),
                file.getDouble("contentTop"),
                file.getDouble("contentRight"),
                file.getDouble("contentBottom")
        );
    }

    private static String optionalString(JSONObject object, String key) throws JSONException {
        if (!object.has(key) || object.isNull(key)) {
            return null;
        }
        return object.getString(key);
    }
}
