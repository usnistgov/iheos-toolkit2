package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;

import java.util.List;

/**
 * Data model for a workflow activity or a task.
 */
public class ActivityItem {
    private String id;
    private String transaction;
    /**
     * Parent workflow activity that this activity was based on. If empty, this is the parent workflow activity.
     */
    private ActivityItem input;
    /**
     * The result of this workflow activity.
     */
    private Result output;
    /**
     * The result out of the actions based on the current activity item.
     * For example, a FindDocuments activity can have a GetDocuments action. In this case, the GetDocuments activity will be a child of the FindDocuments parent activity.
     */
    private List<ActivityItem> actionItems;

    public ActivityItem(Result output) {
        this.output = output;
        if (output!=null) {
            if (output.stepResults!=null) {
                StepResult firstStepResult = output.stepResults.get(0);
                if (firstStepResult!=null) {
                    transaction = firstStepResult.transaction;
                    id = firstStepResult.section + "_" + firstStepResult.stepName + "_" + firstStepResult.transaction;
                }
            }
        } else {
            transaction = "Undefined.";
        }
    }

    public String getId() {
        return id;
    }

    public String getTransaction() {
        return transaction;
    }

    public Result getOutput() {
        return output;
    }

    public void setOutput(Result output) {
        this.output = output;
    }

    public ActivityItem getInput() {
        return input;
    }

    public void setInput(ActivityItem input) {
        this.input = input;
    }

    public List<ActivityItem> getActionItems() {
        return actionItems;
    }

    public void setActionItems(List<ActivityItem> actionItems) {
        this.actionItems = actionItems;
    }
}
