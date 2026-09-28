package appuni.explore.domain;

public final class DetailsPolicy {
    private DetailsPolicy() {
    }

    public static PartnerDetails detailsFor(Catalog catalog, String partnerId, boolean logoAvailable) {
        PartnerUniversity partner = null;
        for (PartnerUniversity candidate : catalog.partners) {
            if (candidate.id.equals(partnerId)) {
                partner = candidate;
                break;
            }
        }
        if (partner == null) {
            throw new IllegalArgumentException("Partner was not found");
        }
        boolean usesStandIn = partner.logoFile == null || partner.logoFile.trim().isEmpty() || !logoAvailable;
        return new PartnerDetails(
                partner.id,
                partner.name,
                partner.city,
                partner.country,
                partner.logoFile,
                usesStandIn,
                partner.agreement.type,
                partner.agreement.description,
                partner.website
        );
    }

    public static WebsiteAction websiteAction(String website, boolean networkValidated) {
        if (!CatalogRules.isWebsiteOpenable(website)) {
            return WebsiteAction.Unavailable.INSTANCE;
        }
        if (!networkValidated) {
            return WebsiteAction.ConnectionRequired.INSTANCE;
        }
        return new WebsiteAction.OpenWebsite(website.trim());
    }
}
