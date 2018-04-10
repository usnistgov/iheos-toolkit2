package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class QueryOrigin implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;
    String testName;
    String sectionName;
    String stepName;

    public QueryOrigin() {
    }

    public QueryOrigin(String testName, String sectionName, String stepName) {
        this.testName = testName;
        this.sectionName = sectionName;
        this.stepName = stepName;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public boolean hasValues() {
        return ((testName !=null && !"".equals(testName)) && (sectionName != null && !"".equals(sectionName)) && (stepName != null && !"".equals(stepName)));
    }

    @Override
    public String toString() {
        return "QueryOrigin{" +
                "testName='" + testName + '\'' +
                ", sectionName='" + sectionName + '\'' +
                ", stepName='" + stepName + '\'' +
                '}';
    }
}
