package gov.nist.toolkit.xdstools2.client.inspector;

import gov.nist.toolkit.registrymetadata.client.MetadataObject;

public class MetadataObjectWrapper {
    MetadataObjectType type;
    MetadataObject object;


    public MetadataObjectWrapper(MetadataObjectType type, MetadataObject object) {
        this.type = type;
        this.object = object;
    }

    public MetadataObjectType getType() {
        return type;
    }

    public void setType(MetadataObjectType type) {
        this.type = type;
    }

    public MetadataObject getObject() {
        return object;
    }

    public void setObject(MetadataObject object) {
        this.object = object;
    }
}
