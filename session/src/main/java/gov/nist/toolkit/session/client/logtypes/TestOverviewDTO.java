package gov.nist.toolkit.session.client.logtypes;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.client.LogMapDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Should be renamed TestResultsDTO!!!!
 */
public class TestOverviewDTO implements BasicTestOverview, Serializable, IsSerializable, Comparable {
    private String name;
    private TestInstance testInstance;
    private String title;
    private String description;
    private boolean pass;
    private boolean run = true;
    private List<String> sectionNames = new ArrayList<>();
    private Map<String, SectionOverviewDTO> sections = new HashMap<>();
    private LogMapDTO logMapDTO = null;
    private Collection<String> dependencies;
    private String testKitSource;
    private String testKitSection;

    public TestOverviewDTO() {}

    public TestInstance getTestInstance() { return testInstance; }

    public TestOverviewDTO(TestInstance testInstance) {
        setTestInstance(testInstance);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.testInstance = new TestInstance(name);
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
        this.name = testInstance.getId();
    }

    public boolean isTls() {
        return logMapDTO != null && logMapDTO.isTls();
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRun() {
        return run;
    }

    public void setRun(boolean run) {
        this.run = run;
    }

    public LogMapDTO getLogMapDTO() {
        return logMapDTO;
    }

    public void setLogMapDTO(LogMapDTO logMapDTO) {
        this.logMapDTO = logMapDTO;
    }

    private SectionOverviewDTO latestSection() {
        String latest = "0";
        SectionOverviewDTO latestSection = null;
        try {
            for (String name : sections.keySet()) {
                SectionOverviewDTO section = sections.get(name);
                try {
                    if (section.hl7Time.compareTo(latest) > 0) {
                        latest = section.hl7Time;
                        latestSection = section;
                    }
                } catch (Exception e) {
                    //
                }
            }
        } catch (Exception e) {
            return null;
        }
        return latestSection;
    }

    public String getLatestSectionTime() {
        SectionOverviewDTO section = latestSection();
        if (section  == null) return "";
        return section.getDisplayableTime();
    }

    public Collection<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Collection<String> dependencies) {
        this.dependencies = dependencies;
    }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   @Override
   public int compareTo(Object o) {
      return name.compareTo(((TestOverviewDTO)o).name);
   }

    public String getTestKitSource() {
        return testKitSource;
    }

    public void setTestKitSource(String testKitSource) {
        this.testKitSource = testKitSource;
    }

    public String getTestKitSection() {
        return testKitSection;
    }

    public void setTestKitSection(String testKitSection) {
        this.testKitSection = testKitSection;
    }
}
