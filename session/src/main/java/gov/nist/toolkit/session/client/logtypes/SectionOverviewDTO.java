package gov.nist.toolkit.session.client.logtypes;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.testkitutilities.client.Gather;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SectionOverviewDTO implements BasicSectionOverview, Serializable, IsSerializable {
    private String name;
    private String description;
    private boolean pass;
    String site = "";
    String hl7Time = "";
    private boolean run = true;
    private List<String> stepNames = new ArrayList<>();
    private Map<String, StepOverviewDTO> steps = new HashMap<>();
    private boolean sutInitiated = false;
    private List<Gather> gathers = null;

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

    public StepOverviewDTO getStep(String stepName) {
        return steps.get(stepName);
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getHl7Time() {
        return hl7Time;
    }

    public void setHl7Time(String hl7Time) {
        this.hl7Time = hl7Time;
    }

    // 2016 08 28 1429 54
    public String getDisplayableTime() {
        try {
            return hl7Time.substring(8, 10) + ":" + hl7Time.substring(10, 12) + " " +
                    asMonth(hl7Time.substring(4, 6)) + " " +
                    hl7Time.substring(6, 8) + ", " +
                    hl7Time.substring(0, 4);
        } catch (Exception e) {
            return "";
        }
    }

    private String[] months = { "", "Jan", "Feb", "Mar", "April", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec"};

    private String asMonth(String in) {
        int ini = Integer.parseInt(in);
        try {
            return months[ini];
        } catch (Exception e) {
            return "__";
        }
    }

    public boolean isSutInitiated() {
        return sutInitiated;
    }

    public void setSutInitiated(boolean sutInitiated) {
        this.sutInitiated = sutInitiated;
    }
}
