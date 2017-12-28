package gov.nist.toolkit.session.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SubMessage implements Serializable, IsSerializable, IMessage {
    private String name;
    private String value;

    List<SubMessage> subMessages = new ArrayList<>();

    public SubMessage() {}

    public SubMessage(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void addSubMessage(String name, String value) {
        SubMessage sm = new SubMessage(name, value);
        subMessages.add(sm);
    }

    public void addSubMessage(SubMessage subMessage) {
        subMessages.add(subMessage);
    }

    public List<SubMessage> getSubMessages() {
        return subMessages;
    }

    public void addSubMessages(List<SubMessage> theSubMessages) {
        subMessages.addAll(theSubMessages);
    }
}
