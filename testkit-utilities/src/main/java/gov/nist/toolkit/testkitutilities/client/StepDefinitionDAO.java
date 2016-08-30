package gov.nist.toolkit.testkitutilities.client;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StepDefinitionDAO {
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
