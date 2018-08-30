package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.results.client.TestInstance;

import java.io.Serializable;

public class QueryOrigin implements IsSerializable, Serializable {
    private static final long serialVersionUID = 1L;
    TestInstance testInstance;
    String sectionName;
    String stepName;

    public QueryOrigin() {
    }

    public QueryOrigin(TestInstance testInstance, String sectionName, String stepName) {
        this.testInstance = testInstance;
        this.sectionName = sectionName;
        this.stepName = stepName;
    }


    public String getSectionName() {
        return sectionName;
    }


    public String getStepName() {
        return stepName;
    }

    public TestInstance getTestInstance() {
        return testInstance;
    }

    public void setTestInstance(TestInstance testInstance) {
        this.testInstance = testInstance;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public boolean hasValues() {
        return ((testInstance!=null && !"".equals(testInstance)) && (sectionName != null && !"".equals(sectionName)) && (stepName != null && !"".equals(stepName)));
    }

    @Override
    public String toString() {
        return "QueryOrigin{" +
                "testInstance=" + testInstance +
                ", sectionName='" + sectionName + '\'' +
                ", stepName='" + stepName + '\'' +
                '}';
    }
}
