package gov.nist.toolkit.actorfactory.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actortransaction.client.ActorType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bill on 9/15/15.
 */
public class SimulatorStats implements Serializable, IsSerializable {
    public SimId simId;
    public Map<String, String> stats = new HashMap<>();
    public ActorType actorType;

    static public final String DOCUMENT_ENTRY_COUNT = "DocumentEntries";
    static public final String SUBMISSION_SET_COUNT = "SubmissionSets";
    static public final String ASSOCIATION_COUNT = "Associations";
    static public final String FOLDER_COUNT = "Folders";
    static public final String PATIENT_ID_COUNT = "PatientIds";
    static public final String DOCUMENT_COUNT = "Documents";

    public static List<String> displayOrder = new ArrayList<>();

    static {
        displayOrder.add(SUBMISSION_SET_COUNT);
        displayOrder.add(DOCUMENT_ENTRY_COUNT);
        displayOrder.add(FOLDER_COUNT);
        displayOrder.add(DOCUMENT_COUNT);
        displayOrder.add(PATIENT_ID_COUNT);
    }

    public void put(String name, String value) {
        stats.put(name, value);
    }

    public void put(String name, int value) {
        stats.put(name, Integer.toString(value));
    }

    public Map<String, String> getStats() { return stats; }

    public String toString() { return stats.toString(); }

    public void add(SimulatorStats ss) {
        for (String key : ss.stats.keySet()) {
            String value = ss.stats.get(key);
            stats.put(key, value);
        }
    }
}
