package gov.nist.toolkit.testkitutilities.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StepDefinitionDAO implements Serializable, IsSerializable {
    private List<String> goals = new ArrayList<>();
    private String id;

    public StepDefinitionDAO() {
    }

    public List<String> getGoals() {
        return goals;
    }

    public void addGoals(String goal) {
        this.goals.add(goal);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
