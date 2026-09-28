package appuni.explore.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class CatalogRules {
    private CatalogRules() {
    }

    public static List<String> violations(Catalog catalog) {
        List<String> problems = new ArrayList<>();
        if (isBlank(catalog.host.id)) {
            problems.add("Host id is blank");
        }
        if (isBlank(catalog.host.name)) {
            problems.add("Host name is blank");
        }
        List<String> ids = new ArrayList<>();
        for (PartnerUniversity partner : catalog.partners) {
            ids.add(partner.id);
        }
        Set<String> unique = new HashSet<>(ids);
        if (ids.size() != unique.size()) {
            problems.add("Partner ids are not unique");
        }
        for (PartnerUniversity partner : catalog.partners) {
            if (isBlank(partner.id)) {
                problems.add("Partner id is blank");
            }
            if (isBlank(partner.name)) {
                problems.add("Partner name is blank");
            }
            if (isBlank(partner.city)) {
                problems.add("Partner city is blank");
            }
            if (isBlank(partner.country)) {
                problems.add("Partner country is blank");
            }
            if (partner.latitude < -90.0 || partner.latitude > 90.0) {
                problems.add("Latitude out of range");
            }
            if (partner.longitude < -180.0 || partner.longitude > 180.0) {
                problems.add("Longitude out of range");
            }
            if (isBlank(partner.agreement.type)) {
                problems.add("Agreement type is blank");
            }
            if (isBlank(partner.agreement.description)) {
                problems.add("Agreement description is blank");
            }
        }
        return problems;
    }

    public static boolean isWebsiteOpenable(String website) {
        String value = website == null ? "" : website.trim();
        return value.startsWith("https://") && value.length() > "https://".length();
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
