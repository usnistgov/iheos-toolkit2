package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.xdstools2.client.selectors.TestSessionManager;

import java.util.ArrayList;
import java.util.List;

public class TestSessionState {
	String testSessionName = null;
	List<String> testSessionNameChoices = new ArrayList<String>();
	List<TestSessionManager> managers = new ArrayList<TestSessionManager>();

	public boolean isFirstManager() { return managers.size() == 1; }
	
	public String getTestSessionName() {
		return testSessionName;
	}

	public void setTestSessionName(String testSessionName) {
		this.testSessionName = testSessionName;
		validate();
	}

	public List<String> getTestSessionNameChoices() {
		return testSessionNameChoices;
	}
	
	public void updated(TestSessionManager source) {
		for (TestSessionManager m : managers) {
			if (m != source)
				m.needsUpdating();
		}
	}
	
	public int getManagerIndex(TestSessionManager source) {
		return managers.indexOf(source);
	}

	public void setTestSessionNameChoices(List<String> testSessionNameChoices) {
		this.testSessionNameChoices = testSessionNameChoices;
		validate();
	}
	
	public void addManager(TestSessionManager testSessionManager) {
		managers.add(testSessionManager);
		System.out.println(managers.size() + " TestSessionManagers");
	}
	
	public void deleteManager(TestSessionManager testSessionManager) {
		managers.remove(testSessionManager);
	}
	
	public boolean isValid() { return !isEmpty(testSessionName); }
	
	public boolean validate() {
		if (isEmpty(testSessionName) || testSessionNameChoices == null) {
			testSessionName = null;
			return false;
		}
		for (String name : testSessionNameChoices) {
			if (testSessionName.equals(name))
				return true;
		}
		testSessionName = null;
		return false;
	}
	
	boolean isEmpty(String x) { return x == null || x.equals(""); }

}
