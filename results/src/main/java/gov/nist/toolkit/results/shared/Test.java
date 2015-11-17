package gov.nist.toolkit.results.shared;

import java.io.Serializable;

/**
 * This object exists for display purposes. It provides data to a grid of tests located inside TestsOverviewWidget.
 * Created by Diane Azais local on 10/11/2015.
 *
 */
public class Test implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    // TODO the commands parameter should ultimately go away. An empty space in the data is needed for display but
    // should be built automatically.
    String id;
    String description;
    String commands;
    String timestamp;
    String status;
    boolean isSection;

    public Test() {
    }


    public Test(String _id, String _description, String _commands, String _timestamp, String _status, boolean _isSection){
        id = _id;
        description = _description;
        commands = _commands;
        timestamp = _timestamp;
        status = _status;
        isSection = _isSection;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCommands() {
        return commands;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStatus() {
        return status;
    }

    public boolean isSection() { return isSection; }
}
