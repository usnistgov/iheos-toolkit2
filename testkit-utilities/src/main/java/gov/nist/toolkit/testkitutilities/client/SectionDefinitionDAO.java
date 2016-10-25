package gov.nist.toolkit.testkitutilities.client;

import java.util.*;

/**
 *
 */
public class SectionDefinitionDAO {
    private Map<String, StepDefinitionDAO> steps = new HashMap<>();
    private List<String> names = new ArrayList<String>();
    private Set<String> sectionDependencies = new HashSet<String>();
    private boolean sutInitiated = false;
    private String sectionName;

    public SectionDefinitionDAO(String sectionName) {
        this.sectionName = sectionName;
    }

    public StepDefinitionDAO getStep(String name) {
        return steps.get(name);
    }

    public void addStep(StepDefinitionDAO step) {
        this.steps.put(step.getId(), step);
        names.add(step.getId());
    }

    public List<String> getStepNames() {
        return names;
    }

    public Collection<String> getSectionDependencies() {
        return sectionDependencies;
    }

    public void addTestDependency(String testId) {
        sectionDependencies.add(testId);
    }

    public void sutInitiated() { sutInitiated = true; }

    public boolean isSutInitiated() { return sutInitiated; }

    public String getSectionName() {
        return sectionName;
    }
}
