package gov.nist.toolkit.services.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Message implements Serializable, IsSerializable {
    private List<String> parts = new ArrayList<>();

    public Message() {
    }

    public Message(String in) {
        parts.add(in);
    }

    public Message(String in, String in2) {
        parts.add(in);
        parts.add(in2);
    }

    public void add(String part) {
        parts.add(part);
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
}
