package gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab;

import java.util.List;


public class TestDetailsModel {
		
	enum Status  {Passed, Failed, NA};
	
	String testId;
	String testDescription;
	boolean isInitiator;  
	String timestamp;
	boolean hasBeenRun;
	Status passed;
	List<String> sections;
	
	int displayRow;
	boolean isGrey;
}
