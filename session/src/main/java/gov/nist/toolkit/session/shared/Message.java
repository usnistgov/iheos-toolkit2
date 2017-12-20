package gov.nist.toolkit.session.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Message implements Serializable, IsSerializable {
    // header and body
    private List<String> parts = new ArrayList<>();

    // structured representation of the body
    List<SubMessage> subMessages = new ArrayList<>();

    public Message() {
    }

    public Message(String in) {
        parts.add(in);
    }

    public Message(String in, String in2) {
        parts.add(in);
        parts.add(in2);
    }

    public List<SubMessage> getSubMessages() {
        return subMessages;
    }

    public void add(String part) {
        parts.add(part);
    }

    public void addSubMessage(String name, String value) {
        SubMessage sm = new SubMessage(name, value);
        subMessages.add(sm);
    }

    public int getSubMessageCount() { return subMessages.size(); }

    public boolean hasSubMessages() { return getSubMessageCount() > 0; }

    public SubMessage getSubMessage(int i) {
        if (i >= getSubMessageCount())
            return new SubMessage("", "");
        return subMessages.get(i);
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

    public void addSubMessages(List<SubMessage> theSubMessages) {
        subMessages.addAll(theSubMessages);
    }
}
