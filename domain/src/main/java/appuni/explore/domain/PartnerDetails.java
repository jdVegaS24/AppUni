package appuni.explore.domain;

public final class PartnerDetails {
    public final String id;
    public final String name;
    public final String city;
    public final String country;
    public final String logoFile;
    public final boolean usesStandIn;
    public final String agreementType;
    public final String agreementDescription;
    public final String website;

    public PartnerDetails(
            String id,
            String name,
            String city,
            String country,
            String logoFile,
            boolean usesStandIn,
            String agreementType,
            String agreementDescription,
            String website
    ) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.logoFile = logoFile;
        this.usesStandIn = usesStandIn;
        this.agreementType = agreementType;
        this.agreementDescription = agreementDescription;
        this.website = website;
    }
}
