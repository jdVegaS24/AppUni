package appuni.explore.domain;

import java.util.List;

public final class Catalog {
    public final HostUniversity host;
    public final List<PartnerUniversity> partners;

    public Catalog(HostUniversity host, List<PartnerUniversity> partners) {
        this.host = host;
        this.partners = partners;
    }
}
