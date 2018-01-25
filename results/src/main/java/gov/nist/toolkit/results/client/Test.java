package gov.nist.toolkit.results.client;

import java.io.Serializable;

/**
 * This model exists for display purposes. It provides data to a grid of tests located inside TestsOverviewWidget.
 * Created by Diane Azais local on 10/11/2015.
 *
 */
public class Test implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    int id;
    boolean isSection;
    String idWithSection;
    String name; /* Display name (test or section ID) */
    String description;
    String commands;
    String timestamp;
    String status;

    public Test() {
    }


    public Test(int _id, boolean _isSection, String _idWithSection, String _name, String _description, String _timestamp, String _status){
        id = _id;
        isSection = _isSection;
        idWithSection = _idWithSection;
        name = _name;
        description = _description;
        commands = "";
        timestamp = _timestamp;
        status = _status;
    }

    public int getId() { return id; }

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

    public String getIdWithSection(){
        return idWithSection;
    }
}
