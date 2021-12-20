package gov.nist.toolkit.xdstools2.shared.command.request;

import gov.nist.toolkit.xdstools2.shared.command.CommandContext;

import java.util.Map;

/**
 * Created by onh2 on 11/4/16.
 */
public class SetToolkitPropertiesRequest extends CommandContext{
    private String hash;
    private Map<String, String> properties;

    public SetToolkitPropertiesRequest(){}
    public SetToolkitPropertiesRequest(CommandContext context, String hash, Map<String, String> props){
        copyFrom(context);
        this.hash = hash;
        this.properties=props;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public String getHash() {
        return hash;
    }
}
