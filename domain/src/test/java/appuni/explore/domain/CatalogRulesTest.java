package appuni.explore.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class CatalogRulesTest {
    @Test
    public void blankHostIdIsRejected() {
        Catalog catalog = new Catalog(new HostUniversity("  ", "Host University"), sample().partners);
        assertTrue(CatalogRules.violations(catalog).stream().anyMatch(item -> item.contains("Host id")));
    }

    @Test
    public void blankHostNameIsRejected() {
        Catalog catalog = new Catalog(new HostUniversity("host", ""), sample().partners);
        assertTrue(CatalogRules.violations(catalog).stream().anyMatch(item -> item.contains("Host name")));
    }

    @Test
    public void duplicatePartnerIdsAreRejected() {
        PartnerUniversity partner = sample().partners.get(0);
        Catalog catalog = new Catalog(sample().host, List.of(partner, partner));
        assertTrue(CatalogRules.violations(catalog).stream().anyMatch(item -> item.contains("unique")));
    }

    @Test
    public void latitudeOutsideRangeIsRejected() {
        PartnerUniversity partner = copyLatitude(sample().partners.get(0), 90.1);
        Catalog catalog = new Catalog(sample().host, List.of(partner));
        assertTrue(CatalogRules.violations(catalog).stream().anyMatch(item -> item.contains("Latitude")));
    }

    @Test
    public void longitudeOutsideRangeIsRejected() {
        PartnerUniversity partner = copyLongitude(sample().partners.get(0), -180.1);
        Catalog catalog = new Catalog(sample().host, List.of(partner));
        assertTrue(CatalogRules.violations(catalog).stream().anyMatch(item -> item.contains("Longitude")));
    }

    @Test
    public void blankAgreementTypeOrDescriptionIsRejected() {
        PartnerUniversity base = sample().partners.get(0);
        PartnerUniversity missingType = copyAgreement(base, new Agreement("", "Study"));
        PartnerUniversity missingDescription = copyAgreement(base, new Agreement("Exchange", " "));
        assertTrue(CatalogRules.violations(new Catalog(sample().host, List.of(missingType))).stream().anyMatch(item -> item.contains("type")));
        assertTrue(CatalogRules.violations(new Catalog(sample().host, List.of(missingDescription))).stream().anyMatch(item -> item.contains("description")));
    }

    @Test
    public void nonHttpsBlankAndNullWebsitesAreNotOpenable() {
        assertFalse(CatalogRules.isWebsiteOpenable("http://example.com"));
        assertFalse(CatalogRules.isWebsiteOpenable(""));
        assertFalse(CatalogRules.isWebsiteOpenable("   "));
        assertFalse(CatalogRules.isWebsiteOpenable(null));
        assertTrue(CatalogRules.isWebsiteOpenable("https://example.com/partner"));
    }

    @Test
    public void validCatalogHasNoViolations() {
        assertEquals(List.of(), CatalogRules.violations(sample()));
    }

    private static Catalog sample() {
        return new Catalog(
                new HostUniversity("host", "Host University"),
                List.of(new PartnerUniversity(
                        "salamanca",
                        "Northern Spain University",
                        "Salamanca",
                        "Spain",
                        40.96,
                        -5.66,
                        new Agreement("Exchange", "Student exchange"),
                        null,
                        "https://example.com/salamanca"
                ))
        );
    }

    private static PartnerUniversity copyLatitude(PartnerUniversity partner, double latitude) {
        return new PartnerUniversity(partner.id, partner.name, partner.city, partner.country, latitude, partner.longitude, partner.agreement, partner.logoFile, partner.website);
    }

    private static PartnerUniversity copyLongitude(PartnerUniversity partner, double longitude) {
        return new PartnerUniversity(partner.id, partner.name, partner.city, partner.country, partner.latitude, longitude, partner.agreement, partner.logoFile, partner.website);
    }

    private static PartnerUniversity copyAgreement(PartnerUniversity partner, Agreement agreement) {
        return new PartnerUniversity(partner.id, partner.name, partner.city, partner.country, partner.latitude, partner.longitude, agreement, partner.logoFile, partner.website);
    }
}
