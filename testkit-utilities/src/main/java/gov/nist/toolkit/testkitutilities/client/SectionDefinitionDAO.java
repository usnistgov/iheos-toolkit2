package gov.nist.toolkit.testkitutilities.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SectionDefinitionDAO {
    private Map<String, StepDefinitionDAO> steps = new HashMap<>();
    private List<String> names = new ArrayList<String>();

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
}
