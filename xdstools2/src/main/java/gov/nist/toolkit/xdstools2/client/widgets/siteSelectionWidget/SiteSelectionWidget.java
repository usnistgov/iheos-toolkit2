package gov.nist.toolkit.xdstools2.client.widgets.siteSelectionWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.QueryBoilerplate;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.TestsOverviewTab;

import java.util.*;

/**
 * Created by Diane Azais local on 11/1/2015.
 */
public class SiteSelectionWidget extends HorizontalPanel {
	final String allSelection = "-- All --";
	final String chooseSelection = "-- Choose --";

	ListBox selectActorList = new ListBox();
	ListBox selectTestList = new ListBox();
	final protected ToolkitServiceAsync toolkitService = GWT.create(ToolkitService.class);
	Map<String, String> actorCollectionMap;  // name => description
	Map<String, String> testCollectionMap;   // name => description for selected actor
	String selectedActor;
	protected QueryBoilerplate queryBoilerplate = null;
	public VerticalPanel resultPanel = new VerticalPanel();
	GenericQueryTab parent;
	String selectedTest;
	String selectedSection = allSelection;





	public SiteSelectionWidget(GenericQueryTab _parent){
		parent = _parent;

		// Load and display the actor types
		add(selectActorList);
		loadActorNames();
		selectActorList.addChangeHandler(new ActorSelectionChangeHandler());
    }

	/**
	 * Loads the list of actor types from the back-end and populates the display on the UI
	 */
	private void loadActorNames() {
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

    /**
     * Loads the list of actors for the type of actor selected
     */
	class ActorSelectionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			// Retrieve the type of actor chosen by the user
			int selectedI = selectActorList.getSelectedIndex();
			selectedActor = selectActorList.getValue(selectedI);

			if (selectedActor == null)
				return;
			if ("".equals(selectedActor))
				return;
			loadTestsForActor();

			// Find a match in the system for that category of actor
			ActorType act = ActorType.findActor(selectedActor); // should also work with selectedActor
			if (act == null)
				return;

			// Populate the list of transaction types
			List<TransactionType> transactionTypes = act.getTransactions();

			queryBoilerplate = parent.addQueryBoilerplate(
					new Runner(),
					transactionTypes,
					new CoupledTransactions(), //TestsOverviewTab.couplings,
					false); // not using a PID in this tab, should be false

			parent.setQueryBoilerPlate(queryBoilerplate);
		}
	}


	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
/*
			resultPanel.clear();

			if (!verifySiteProvided()) return;

			List<String> selectedSections = new ArrayList<String>();
			if (selectedSection.equals(allSelection)) {
				selectedSections.addAll(sections);
			} else
				selectedSections.add(selectedSection);

			Map<String, String> parms = new HashMap<>();
			parms.put("$patientid$", pidTextBox.getValue().trim());

//			String pid = patientIdBox.getText();
//			if (pid != null && !pid.equals("")) {
//				pid = pid.trim();
//				parms.put("$patientid$", pid);
//			}

			String altPid = altPatientIdBox.getText();
			if (altPid != null && !altPid.equals("")) {
				altPid = altPid.trim();
				parms.put("$altpatientid$", altPid);
			}
*/
			//rigForRunning();
			//toolkitService.runMesaTest(getCurrentTestSession(), getSiteSelection(), new TestInstance(selectedTest), selectedSections, parms, true, queryCallback);

		}

		}

	protected SiteSpec getSiteSelection() { return queryBoilerplate.getSiteSelection(); }

	private boolean verifySiteProvided() {
		SiteSpec siteSpec = getSiteSelection();
		if (siteSpec == null) {
			new PopupMessage("You must select a site first");
			return false;
		}
		return true;
	}

	/**
	 * Loads the list of available tests from the backend for a given actor type
	 */
	void loadTestsForActor() {
		toolkitService.getCollection("actorcollections", selectedActor, new AsyncCallback<Map<String, String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getCollection(actorcollections): " + selectedActor + " -----  " + caught.getMessage());
			}

			public void onSuccess(Map<String, String> result) {
				testCollectionMap = result;
				//selectTestList.clear();
				//selectTestList.addItem(chooseSelection, "");

				Set<String> testNumsSet = testCollectionMap.keySet();
				List<String> testNums = new ArrayList<String>();
				testNums.addAll(testNumsSet);
				testNums = new StringSort().sort(testNums);

				/*
				for (String name : testNums) {
					String description = testCollectionMap.get(name);
					selectTestList.addItem(name + " - " + description, name);
				}
				*/
			}
		});
	}


	class TestSelectionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectedI = selectTestList.getSelectedIndex();
			selectedTest = selectTestList.getValue(selectedI);
			if ("".equals(selectedTest))
				return;
			//loadTestReadme();
			//loadSectionNames();
			selectedSection = allSelection;
		}

	}

}
