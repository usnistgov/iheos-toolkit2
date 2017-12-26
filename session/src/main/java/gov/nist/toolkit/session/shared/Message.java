package gov.nist.toolkit.session.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Message implements Serializable, IsSerializable, IMessage {
    // header and body
    private String name = "";
    private List<String> parts = new ArrayList<>();

    // structured representation of the body
    private List<SubMessage> subMessages = new ArrayList<>();

    public Message() {}

    public Message(String name) {
        this.name = name;
    }

    public boolean isEmpty() {
        return parts.isEmpty() && subMessages.isEmpty();
    }

    public List<SubMessage> getSubMessages() {
        return subMessages;
    }

    public Message add(String part) {
        return add("", part);
    }

    public Message setName(String name) { this.name = name; return this; }

    public Message add(String label, String part) {
//        partLabels.add(label);
        parts.add(part);
        return this;
    }

    public void addSubMessage(String name, String value) {
        SubMessage sm = new SubMessage(name, value);
        subMessages.add(sm);
    }

    public List<String> getParts() { return parts; }

    public String toString() {
        String sep = "-----";
        StringBuilder b = new StringBuilder();
        b.append(sep).append("\n");
        for (String part : parts) {
            b.append(part).append("\n").append(sep).append("\n");
        }
        return b.toString();
    }

    public void addSubMessage(SubMessage subMessage) {
        subMessages.add(subMessage);
    }

    public String getName() {
        return name;
    }
}
