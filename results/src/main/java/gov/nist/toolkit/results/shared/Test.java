package gov.nist.toolkit.results.shared;

import java.io.Serializable;

/**
 * This object exists for display purposes. It provides data to a grid of tests located inside TestsOverviewWidget.
 * Created by Diane Azais local on 10/11/2015.
 *
 */
public class Test implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    String id;
    String name;
    String description;
    String commands;
    String timestamp;
    String status;
    boolean isSection;

    public Test() {
    }


    public Test(String _id, String _name, String _description, String _timestamp, String _status, boolean _isSection){
        id = _id;
        name = _name;
        description = _description;
        commands = "";
        timestamp = _timestamp;
        status = _status;
        isSection = _isSection;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
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
