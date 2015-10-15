package gov.nist.toolkit.results.shared;

import java.io.Serializable;

/**
 * Created by Diane Azais local on 10/11/2015.
 */
public class Test implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;


    String number;
    String description;
    String commands;
    String time;
    String status;

    public Test() {
    }


    public Test(String _number, String _description, String _commands, String _time, String _status){
        number = _number;
        description = _description;
        commands = _commands;
        time = _time;
        status =_status;
    }

    public String getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public String getCommands() {
        return commands;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }
}
