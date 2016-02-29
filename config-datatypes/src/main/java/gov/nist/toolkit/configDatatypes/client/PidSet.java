package gov.nist.toolkit.configDatatypes.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by bill on 9/23/15.
 */
public class PidSet implements Serializable, IsSerializable {
    Set<Pid> pids = new HashSet<>();

    public PidSet() {}

    public PidSet(Set<Pid> pids) { this.pids = pids; }

    public String asParsableString() {
        StringBuilder buf = new StringBuilder();
        buf.append('[');

        boolean empty = true;

        for (Pid pid : pids) {
            if (!empty) buf.append(',');
            empty = false;
            buf.append(pid.asParsableString());
        }

        buf.append(']');
        return buf.toString();
    }

    public PidSet(String s) {
        if (s == null) return;
        s = s.trim();
        if (s.equals("")) return;
        if (s.charAt(0) != '[') return;
        if (s.charAt(s.length()-1) != ']') return;
        s = s.substring(1, s.length());
        String[] pidStr = s.split(",");
        for (int i=0; i<pidStr.length; i++) {
            Pid pid = PidBuilder.createPid(pidStr[i]);
            if (pid != null) pids.add(pid);
        }
    }

    public Set<Pid> get() { return pids; }

    @Override
    public int hashCode() {
        int sum = 41;
        for (Pid pid : pids) sum += pid.hashCode();
        return sum;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof  PidSet)) return false;
        PidSet pidSet = (PidSet) o;
        for (Pid pid : pidSet.pids) {
            if (!pids.contains(pid)) return false;
        }
        return true;
    }
}
