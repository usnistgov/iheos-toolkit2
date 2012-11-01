package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class OnBoardingTestTab extends GenericQueryTab {
	final protected ToolkitServiceAsync toolkitService = GWT
	.create(ToolkitService.class);

	final String allSelection = "-- All --";
	final String chooseSelection = "-- Choose --";

	ListBox selectActorList = new ListBox();
	ListBox selectTestList = new ListBox();
	ListBox selectSectionList = new ListBox();
	Button selectSectionViewButton = new Button("View testplan");
	ScrollPanel readmeBox = new ScrollPanel();
	TextBox patientIdBox = new TextBox();
	Map<String, String> actorCollectionMap;  // name => description
	String selectedActor;
	String selectedTest;
	String selectedSection = allSelection;
	Map<String, String> testCollectionMap;  // name => description for selected actor
	List<String> sections = new ArrayList<String>();
	int row = 0;
	HorizontalPanel testSessionPanel = new HorizontalPanel();
	ListBox testSessionList = new ListBox();
	String testSession = null;
	TextBox testSessionTextBox = new TextBox();
	boolean isPrivateTesting = false;
	HorizontalPanel selectSectionPanel = new HorizontalPanel();


	public OnBoardingTestTab() {
		super(new GetDocumentsSiteActorManager());
	}

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();


		container.addTab(topPanel, "Pre-OnBoarding Tests", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Pre-OnBoarding Tests</h2>");
		topPanel.add(title);
		
		// test session
		testSessionPanel.setVisible(false);
		topPanel.add(testSessionPanel);
		
		HTML testSessionLabel = new HTML();
		testSessionLabel.setText("Test Session: ");
		testSessionPanel.add(testSessionLabel);
		
		testSessionPanel.add(testSessionList);
		testSessionList.addChangeHandler(new TestSessionChangeHandler());
		loadTestSessionEnabled();
		
		testSessionPanel.add(testSessionTextBox);
		Button addTestSessionButton = new Button("Add");
		testSessionPanel.add(addTestSessionButton);
		addTestSessionButton.addClickHandler(new AddTestSessionClickHandler());
		
		// Actor Selection
		HorizontalPanel selectActorPanel = new HorizontalPanel();
		topPanel.add(selectActorPanel);
		
		HTML selectTestCollectionLabel = new HTML();
		selectTestCollectionLabel.setText("Select Actor Name: ");
		selectActorPanel.add(selectTestCollectionLabel);
		
		selectActorPanel.add(selectActorList);
		loadActorNames();
		selectActorList.addChangeHandler(new ActorSelectionChangeHandler());
		
		// test selection
		HorizontalPanel selectTestPanel = new HorizontalPanel();
		topPanel.add(selectTestPanel);
		
		HTML selectTestLabel = new HTML();
		selectTestLabel.setText("Select Test: ");
		selectTestPanel.add(selectTestLabel);
		
		selectTestPanel.add(selectTestList);
		selectTestList.addChangeHandler(new TestSelectionChangeHandler());
		
		HTML readmeBefore = new HTML();
		readmeBefore.setHTML("<hr />");
		topPanel.add(readmeBefore);
		
		// readme box
		
		readmeBox.setSize("600", "200");
		topPanel.add(readmeBox);
		
		HTML readmeAfter = new HTML();
		readmeAfter.setHTML("<hr />");
		topPanel.add(readmeAfter);

		// section selection
		topPanel.add(selectSectionPanel);
		
		HTML selectSectionLabel = new HTML();
		selectSectionLabel.setText("Select Section: ");
		selectSectionPanel.add(selectSectionLabel);
		
		selectSectionPanel.add(selectSectionList);
		
		topPanel.add(selectSectionViewButton);
		selectSectionViewButton.addClickHandler(new SelectSectionViewButtonClickHandler());

		
		// Patient ID
		HorizontalPanel patientIdPanel = new HorizontalPanel();
		topPanel.add(patientIdPanel);
		
		HTML patientIdLabel = new HTML();
		patientIdLabel.setText("Patient ID");
		patientIdPanel.add(patientIdLabel);

		patientIdBox.setWidth("400");
		patientIdPanel.add(patientIdBox);
	
		mainGrid = new FlexTable();
		
		topPanel.add(mainGrid);


	}
	
	class SelectSectionViewButtonClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			toolkitService.getTestplanAsText(selectedTest, selectedSection, new AsyncCallback<String>() {

				public void onFailure(Throwable caught) {
					new PopupMessage("getTestplanAsText: " + caught.getMessage());
				}

				public void onSuccess(String result) {
					new TextViewerTab().onTabLoad(myContainer, true, result, selectedTest + "#" + selectedSection);
				}
				
			});
		}
		
	}
	
	class TestSessionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectionI = testSessionList.getSelectedIndex();
			testSession = testSessionList.getItemText(selectionI);
			if ("".equals(testSession))
				testSession = null;
		}
		
	}
	
	class AddTestSessionClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			toolkitService.addMesaTestSession(testSessionTextBox.getText(), new AsyncCallback<Boolean>() {

				public void onFailure(Throwable caught) {
					new PopupMessage("addMesaTestSession: " + caught.getMessage());
				}

				public void onSuccess(Boolean result) {
					testSession = testSessionTextBox.getText();
					loadTestSessionNames();
					testSession = testSessionTextBox.getText();
				}
				
			});
		}
		
	}
	
	void loadTestSessionEnabled() {
		toolkitService.isPrivateMesaTesting(new AsyncCallback<Boolean>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("isPrivateMesaTesting: " + caught.getMessage());
			}

			public void onSuccess(Boolean result) {
				isPrivateTesting = result;
				if (!result)
					return;
				loadTestSessionNames();
			}
			
		});
	}
	
	void loadTestSessionNames() {
		toolkitService.getMesaTestSessionNames(new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getMesaTestSessionNames: " + caught.getMessage());
			}

			public void onSuccess(List<String> result) {
				testSessionList.clear();
				testSessionList.addItem("-- Choose --", "");
				for (String val : result)
					testSessionList.addItem(val);
				
				testSessionPanel.setVisible(true);
				testSessionTextBox.setText("");
			}
			
		});
	}
	
	void loadSectionNames() {
		toolkitService.getTestIndex(selectedTest, new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestIndex: " + caught.getMessage());
			}

			public void onSuccess(List<String> result) {
				sections.clear();
				if (result == null) {
					// no index.idx - so sections
					selectSectionPanel.setVisible(false);
				} else {
					sections.addAll(result);
					selectSectionList.clear();
					selectSectionList.addItem(allSelection, allSelection);
					for (String section : result) {
						selectSectionList.addItem(section, section);
					}
					selectSectionList.addChangeHandler(new SectionSelectionChangeHandler());
					selectSectionPanel.setVisible(true);
				}
			}

		});
	}
	
	class SectionSelectionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int i = selectSectionList.getSelectedIndex();
			selectedSection  = selectSectionList.getValue(i);
			if ("".equals(selectedSection))
				return;
		}
		
	}
	
	class TestSelectionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectedI = selectTestList.getSelectedIndex();
			selectedTest = selectTestList.getValue(selectedI);
			if ("".equals(selectedTest))
				return;
			loadTestReadme();
			loadSectionNames();
		}
		
	}
	
	void loadTestReadme() {
		toolkitService.getTestReadme(selectedTest, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getTestReadme: " + caught.getMessage());
			}

			public void onSuccess(String result) {
				Widget w = readmeBox.getWidget();
				if (w != null)
					readmeBox.remove(w);
				readmeBox.add(htmlize("README", result));
			}
			
		});
	}
	
	class ActorSelectionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectedI = selectActorList.getSelectedIndex();
			selectedActor = selectActorList.getValue(selectedI);
			if (selectedActor == null)
				return;
			if ("".equals(selectedActor))
				return;
			String sel = selectedActor;
			loadTestsForActor();
			
			readmeBox.clear();
			
			// these names are found in war/toolkit/testkit/actorcollections/xxxx.tc

			ActorType act = ActorType.findActor(sel);
			if (act == null)
				return;
			
			List<TransactionType> tt = act.getTransactions();
			
			
			queryBoilerplate = addQueryBoilerplate( 
					new Runner(), 
					tt,
					new CoupledTransactions()); 

		}

		
	}
	
	void loadTestsForActor() {
		toolkitService.getCollection("actorcollections", selectedActor, new AsyncCallback<Map<String, String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getCollection(actorcollections): " + selectedActor + " -----  " + caught.getMessage());
			}

			public void onSuccess(Map<String, String> result) {
				testCollectionMap = result;
				selectTestList.clear();
				selectTestList.addItem(chooseSelection, "");

				for (String name : testCollectionMap.keySet()) {
					String description = testCollectionMap.get(name);
					selectTestList.addItem(name + " - " + description, name);
				}
}
		});
	}
	
	void loadActorNames() {
		toolkitService.getCollectionNames("actorcollections", new AsyncCallback<Map<String, String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getCollectionNames: " + caught.getMessage());
			}

			public void onSuccess(Map<String, String> result) {
				actorCollectionMap = result;
				selectActorList.clear();
				selectActorList.addItem(chooseSelection, "");
				
				for (String name : actorCollectionMap.keySet()) {
					String description = actorCollectionMap.get(name);
					selectActorList.addItem(description, name);
				}
				
				
			}
		});
	}
	
	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();
			
			if (isPrivateTesting && testSession == null) {
				new PopupMessage("Test Session must be selected");
				return;
			}

			SiteSpec siteSpec = queryBoilerplate.getSiteSelection();
			if (siteSpec == null)
				return;

			addStatusBox();
			getGoButton().setEnabled(false);
			getInspectButton().setEnabled(false);

			List<String> selectedSections = new ArrayList<String>();
			if (selectedSection.equals(allSelection)) {
				selectedSections.addAll(sections);
			} else
				selectedSections.add(selectedSection);
			
			Map<String, String> parms = new HashMap<String, String>();
			String pid = patientIdBox.getText();
			if (pid != null && !pid.equals("")) { 
				pid = pid.trim();
				parms.put("$patientid$", pid);
			}
			
			toolkitService.runMesaTest(testSession, siteSpec, selectedTest, selectedSections, parms, true, queryCallback);
			
		}
		
	}


	HTML htmlize(String header, String in) {
		HTML h = new HTML(
				"<br />" +
				"<b>" + header + "</b><br /><br />" +

				in.replaceAll("<", "&lt;")
				.replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
				.replaceAll(" ", "&nbsp;")
				.replaceAll("\n", "<br />")
		);
		return h;
	}


	public String getWindowShortName() {
		return "mesatest";
	}

}
