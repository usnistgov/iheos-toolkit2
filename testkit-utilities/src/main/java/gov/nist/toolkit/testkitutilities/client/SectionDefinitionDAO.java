package gov.nist.toolkit.testkitutilities.client;

import java.util.*;

/**
 *
 */
public class SectionDefinitionDAO {
    private Map<String, StepDefinitionDAO> steps = new HashMap<>();
    private List<String> names = new ArrayList<String>();
    private Set<String> sectionDependencies = new HashSet<String>();

    public SectionDefinitionDAO() {
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
}
