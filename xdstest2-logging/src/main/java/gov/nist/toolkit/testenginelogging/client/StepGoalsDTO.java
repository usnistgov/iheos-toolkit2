package gov.nist.toolkit.testenginelogging.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class StepGoalsDTO implements Serializable, IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5885436255799603133L;
	public String stepName;
	public List<String> goals;

	public StepGoalsDTO() {}
	
	public StepGoalsDTO(String stepName) {
		this.stepName = stepName;
		goals = new ArrayList<>();
	}

	public String getStepName() {
		return stepName;
	}

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public List<String> getGoals() {
		return goals;
	}

	public void setGoals(List<String> goals) {
		this.goals = goals;
	}
}
