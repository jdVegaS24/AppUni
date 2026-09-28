package appuni.explore.domain;

public final class PartnerUniversity {
    public final String id;
    public final String name;
    public final String city;
    public final String country;
    public final double latitude;
    public final double longitude;
    public final Agreement agreement;
    public final String logoFile;
    public final String website;

    public PartnerUniversity(
            String id,
            String name,
            String city,
            String country,
            double latitude,
            double longitude,
            Agreement agreement,
            String logoFile,
            String website
    ) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.agreement = agreement;
        this.logoFile = logoFile;
        this.website = website;
    }

    public PartnerUniversity(
            String id,
            String name,
            String city,
            String country,
            double latitude,
            double longitude,
            Agreement agreement
    ) {
        this(id, name, city, country, latitude, longitude, agreement, null, null);
    }
}
