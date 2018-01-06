package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class ResourceItem extends MetadataObject implements IsSerializable, Serializable {
    private String type;
    private String json;
    private String htmlizedJson;

    public ResourceItem() {}

    public ResourceItem(String type, String json, String htmlizedJson) {
        this.type = type;
        this.json = json;
        this.htmlizedJson = htmlizedJson;
    }

    @Override
    public String displayName() {
        return type + "(" + id + ")";
    }

    public String getType() {
        return type;
    }

    public String getJson() {
        return json;
    }

    public String getHtmlizedJson() {
        return htmlizedJson;
    }
}
