package gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab;

import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab.TestRunnerSectionsView.ViewLogButton;

import java.util.ArrayList;
import java.util.List;

public class TestRunnerSectionsPresenter {
	
	interface SectionsDisplay {
		void initTestGrid(List<TestDetailsModel> details);
		public ViewLogButton updateTestRow(TestDetailsModel model);
	}
	
	SectionsDisplay display;
	ToolkitServiceAsync toolkitService;
	TabContainer container;
	String selectedTest;
	List<String> sections = new ArrayList<String>();
	
	TestRunnerSectionsPresenter(TabContainer container, SectionsDisplay d, ToolkitServiceAsync toolkitService, String selectedTest) {
		display = d;
		this.toolkitService = toolkitService;
		this.container = container;
		this.selectedTest = selectedTest;
		
		bind();
		
		
	}
	
	void bind() {
		
	}



}
