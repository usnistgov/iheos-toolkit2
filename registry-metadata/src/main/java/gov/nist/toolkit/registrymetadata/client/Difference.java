package gov.nist.toolkit.registrymetadata.client;

public class Difference {
    String metadataAttributeName;

    public Difference() {
    }

    public Difference(String metadataAttributeName) {
        this.metadataAttributeName = metadataAttributeName;
    }

    public String getMetadataAttributeName() {
        return metadataAttributeName;
    }
}
