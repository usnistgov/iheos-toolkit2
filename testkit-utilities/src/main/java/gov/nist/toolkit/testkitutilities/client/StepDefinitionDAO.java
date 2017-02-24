package gov.nist.toolkit.testkitutilities.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StepDefinitionDAO implements Serializable, IsSerializable {
    private List<String> goals = new ArrayList<>();
    private String id;
    private List<InteractingEntity> interactionSequence;

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

    public List<InteractingEntity> getInteractionSequence() {
        return interactionSequence;
    }

    public void setInteractionSequence(List<InteractingEntity> interactionSequence) {
        this.interactionSequence = interactionSequence;
    }
}
