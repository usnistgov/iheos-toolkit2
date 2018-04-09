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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Difference)) return false;

        Difference that = (Difference) o;

        return metadataAttributeName.equals(that.metadataAttributeName);
    }

    @Override
    public int hashCode() {
        return metadataAttributeName.hashCode();
    }
}
