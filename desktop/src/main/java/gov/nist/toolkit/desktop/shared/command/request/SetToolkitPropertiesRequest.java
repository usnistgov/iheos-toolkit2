package gov.nist.toolkit.desktop.shared.command.request;


import gov.nist.toolkit.desktop.client.commands.util.CommandContext;

import java.util.Map;

/**
 * Created by onh2 on 11/4/16.
 */
public class SetToolkitPropertiesRequest extends CommandContext {
    private Map<String, String> properties;

    public SetToolkitPropertiesRequest(){}
    public SetToolkitPropertiesRequest(CommandContext context, Map<String, String> props){
        copyFrom(context);
        this.properties=props;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
