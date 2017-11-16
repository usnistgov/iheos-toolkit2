package gov.nist.toolkit.session.client.logtypes;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class StepOverviewDTO implements BasicStepOverview, Serializable, IsSerializable {
    String name;
    boolean pass;
    boolean expectedSuccess;
    String transaction;
    List<String> errors = null;
    List<String> details;
    String goals = null;  // preformatted as HTML by server
    List<InteractingEntity> interactionSequence;

    public StepOverviewDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addErrors(List<String> errors) {
        if (errors == null) return;
        if (this.errors == null)
            this.errors = new ArrayList<>();
        this.errors.addAll(errors);
    }

    public void addError(String error) {
        if (this.errors == null)
            this.errors = new ArrayList<>();
        this.errors.add(error);
    }

    public List<String> getDetails() {
        return details;
    }

    public void setDetails(List<String> details) {
        this.details = details;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    public String getGoals() {
        return goals;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public boolean isExpectedSuccess() {
        return expectedSuccess;
    }

    public void setExpectedSuccess(boolean expectedSuccess) {
        this.expectedSuccess = expectedSuccess;
    }

    public List<InteractingEntity> getInteractionSequence() {
        return interactionSequence;
    }

    public void setInteractionSequence(List<InteractingEntity> interactionSequence) {
        this.interactionSequence = interactionSequence;
    }
}
