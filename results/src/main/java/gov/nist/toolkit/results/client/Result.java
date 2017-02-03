package gov.nist.toolkit.results.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.registrymetadata.client.MetadataCollection;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *  Result data for a test instance.
 */
public class Result  implements IsSerializable, Serializable {
	private static final long serialVersionUID = 1L;
	public TestInstance testInstance;   // test can be a single test or a test collection
	public AssertionResults assertions = new AssertionResults();
	public String timestamp;
	public TestInstance logId;
	public List<StepResult> stepResults = new ArrayList<>();
	String text = null;
	public boolean pass = true;
	transient public boolean includesMetadata = false;
	
	public Result() {
		
	}

	public Result clone() {
		Result r = new Result();
		r.testInstance = testInstance;
		r.assertions = assertions.clone();
		r.timestamp = timestamp;
		r.logId = logId;
		r.stepResults = new ArrayList<StepResult>();
		for (StepResult sr : stepResults) r.stepResults.add(sr.clone());
		r.text = text;
		r.pass = pass;
		r.includesMetadata = includesMetadata;
		return r;
	}

	public void append(Result result) throws XdsInternalException {
		if (!testInstance.getId().equals(result.testInstance.getId()))
			throw new XdsInternalException("Cannot append Result objects from different tests.");
		for (StepResult stepResult : result.getStepResults()) {
			stepResults.add(stepResult);
			if (!stepResult.status)
				pass = false;
		}
	}
	
	public Result simpleError(String err) {
		addAssertion(err, false);
		return this;
	}

	public Result simpleStatus(String status) {
		addAssertion(status, true);
		return this;
	}

	public Result(String timestamp) {
		this.timestamp = timestamp;
		assertions = new AssertionResults();
		stepResults = new ArrayList<StepResult>();
		testInstance = new TestInstance("Metadata");
	}
	
	static public Result RESULT(TestInstance testInstance) {
		Result r = new Result("");
		r.testInstance = testInstance;
		return r;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		buf.append(testInstance).append("\n");
        for (StepResult sr : stepResults)
            if (sr.hasContent())
                buf.append(sr.toString());
		buf.append(assertions.toString());
		
		return buf.toString();
	}
	
	public boolean passed() {
		return pass && stepsPassed() && (assertions == null || !assertions.isFailed());
	}

	private boolean stepsPassed() {
		for (StepResult sr : stepResults) {
			if (!sr.status)
				return false;
		}
		return true;
	}
	
	public void setTestInstance(TestInstance testInstance) { this.testInstance = testInstance; }
	
	public Result(AssertionResults assertions) {
		this.assertions = assertions;
	}
	
	public boolean hasContent() {
		for (StepResult sr : stepResults) {
			if (sr.hasContent())
				return true;
		}
		return false;
	}
	
	public StepResult findStep(String stepName)  {
		for (StepResult res : stepResults) {
			if (stepName.equals(res.stepName))
				return res;
		}
		return null;
	}

    public List<MetadataCollection> getMetadataContent() {
        List<MetadataCollection> content = new ArrayList<>();
        for (StepResult sr : stepResults) {
            if (sr.hasContent()) {
                content.add(sr.getMetadata());
            }
        }
        return content;
    }
	
	public void addAssertion(String text, boolean ok) {
		if (assertions == null)
			assertions = new AssertionResults();

		assertions.add(text, ok);
		if (!ok)
			pass = false;
	}
	
	public List<StepResult> getStepResults() {
		return stepResults;
	}
	
	public void addStepResult(StepResult sr) {
		stepResults.add(sr);
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public String getTimestamp() {
		return timestamp;
	}
}
