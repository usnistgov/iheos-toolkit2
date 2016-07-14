package gov.nist.toolkit.session.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SectionOverviewDTO implements Serializable, IsSerializable {
    String name;
    boolean pass;
    List<String> stepNames = new ArrayList<>();
    Map<String, StepOverviewDTO> steps = new HashMap<>();

    public SectionOverviewDTO() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getStepNames() {
        return stepNames;
    }

    public void setStepNames(List<String> stepNames) {
        this.stepNames = stepNames;
    }

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public Map<String, StepOverviewDTO> getSteps() {
        return steps;
    }

    public void setSteps(Map<String, StepOverviewDTO> steps) {
        this.steps = steps;
    }

    public void addStep(String name, StepOverviewDTO stepOverview) {
        stepNames.add(name);
        steps.put(name, stepOverview);
    }
}
