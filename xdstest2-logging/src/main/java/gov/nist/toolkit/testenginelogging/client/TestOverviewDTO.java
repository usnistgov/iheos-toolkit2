package gov.nist.toolkit.testenginelogging.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class TestOverviewDTO implements Serializable, IsSerializable {
    String name;
    boolean pass;
    List<String> sectionNames = new ArrayList<>();
    Map<String, SectionOverviewDTO> sections = new HashMap<>();

    public TestOverviewDTO() {}

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

    public List<String> getSectionNames() {
        return sectionNames;
    }

    public void setSectionNames(List<String> sectionNames) {
        this.sectionNames = sectionNames;
    }

    public Map<String, SectionOverviewDTO> getSections() {
        return sections;
    }

    public void setSections(Map<String, SectionOverviewDTO> sections) {
        this.sections = sections;
    }

    public void addSection(SectionOverviewDTO sectionOverview) {
        sectionNames.add(sectionOverview.getName());
        sections.put(sectionOverview.getName(), sectionOverview);
    }

    public SectionOverviewDTO getSectionOverview(String sectionName) {
        return sections.get(sectionName);
    }
}
