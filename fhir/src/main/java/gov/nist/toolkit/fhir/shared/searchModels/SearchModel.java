package gov.nist.toolkit.fhir.shared.searchModels;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class SearchModel implements Serializable, IsSerializable {
    private ResourceType resourceType = null;
    private LogicalIdSM logicalId = null;

    public SearchModel() {}

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public LogicalIdSM getLogicalId() {
        return logicalId;
    }

    public void setLogicalId(LogicalIdSM logicalId) {
        this.logicalId = logicalId;
    }
}
