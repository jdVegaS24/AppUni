package appuni.explore.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.List;

public class DetailsPolicyTest {
    @Test
    public void detailsDescribeOnlyTheSelectedPartner() {
        PartnerDetails details = DetailsPolicy.detailsFor(catalog(), "coastal", true);
        assertEquals("Coastal Spain University", details.name);
        assertEquals("Barcelona", details.city);
        assertEquals("Spain", details.country);
        assertEquals("Research", details.agreementType);
        assertTrue(!details.name.equals("Northern Spain University"));
    }

    @Test
    public void aSecondIdReplacesTheFirstPartner() {
        PartnerDetails first = DetailsPolicy.detailsFor(catalog(), "northern", true);
        PartnerDetails second = DetailsPolicy.detailsFor(catalog(), "coastal", true);
        assertEquals("northern", first.id);
        assertEquals("coastal", second.id);
        assertTrue(!second.name.equals(first.name));
        assertTrue(!second.city.equals(first.city));
    }

    @Test
    public void blankOrNonHttpsWebsiteIsUnavailable() {
        assertEquals(WebsiteAction.Unavailable.INSTANCE, DetailsPolicy.websiteAction(null, true));
        assertEquals(WebsiteAction.Unavailable.INSTANCE, DetailsPolicy.websiteAction("  ", true));
        assertEquals(WebsiteAction.Unavailable.INSTANCE, DetailsPolicy.websiteAction("http://example.com", true));
    }

    @Test
    public void missingValidatedNetworkYieldsConnectionRequired() {
        WebsiteAction action = DetailsPolicy.websiteAction("https://example.com/partner", false);
        assertEquals(WebsiteAction.ConnectionRequired.INSTANCE, action);
    }

    private static Catalog catalog() {
        return new Catalog(
                new HostUniversity("host", "Host University"),
                List.of(
                        new PartnerUniversity(
                                "northern",
                                "Northern Spain University",
                                "Salamanca",
                                "Spain",
                                40.96,
                                -5.66,
                                new Agreement("Exchange", "Student exchange"),
                                null,
                                "https://example.com/northern"
                        ),
                        new PartnerUniversity(
                                "coastal",
                                "Coastal Spain University",
                                "Barcelona",
                                "Spain",
                                41.39,
                                2.17,
                                new Agreement("Research", "Joint research"),
                                null,
                                "https://example.com/coastal"
                        )
                )
        );
    }
}
