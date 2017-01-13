package gov.nist.toolkit.configDatatypes.client;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class PidSet implements Serializable {
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
        s = s.substring(1, s.length()-1);
        String[] pidStr = s.split("\n");
        for (int i=0; i<pidStr.length; i++) {
            String values[]=pidStr[i].split(",");
            String name=values[values.length-1];
            for (int j=0;j<(values.length-1);j++){
                Pid pid = PidBuilder.createPid(values[j]+","+name);
                if (pid != null) pids.add(pid);
            }
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
