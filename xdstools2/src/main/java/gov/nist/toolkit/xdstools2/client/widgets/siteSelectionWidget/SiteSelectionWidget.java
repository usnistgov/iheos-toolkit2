package gov.nist.toolkit.xdstools2.client.widgets.siteSelectionWidget;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.command.command.GetCollectionNamesCommand;
import gov.nist.toolkit.xdstools2.client.initialization.XdsTools2Presenter;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.QueryBoilerplate;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.TestsOverviewTab;
import gov.nist.toolkit.xdstools2.shared.command.request.GetCollectionRequest;

import java.util.List;
import java.util.Map;


/**
 * Created by Diane Azais local on 11/1/2015.
 */
public class SiteSelectionWidget extends HorizontalPanel {
	ListBox selectActorList = new ListBox();
//	final protected ToolkitServiceAsync toolkitService = GWT.create(ToolkitService.class);
	Map<String, String> actorCollectionMap;  // name => description
	String selectedActor;
	protected QueryBoilerplate queryBoilerplate = null;
	public VerticalPanel resultPanel = new VerticalPanel();
	GenericQueryTab parent;

	final String allSelection = "-- All --";
	final String chooseSelection = "-- Choose --";



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
		new GetCollectionNamesCommand() {
			@Override
			public void onComplete(Map<String, String> result) {
				actorCollectionMap = result;
				selectActorList.clear();
				selectActorList.addItem(chooseSelection, "");

				for (String name : actorCollectionMap.keySet()) {
					String description = actorCollectionMap.get(name);
					selectActorList.addItem(description, name);
				}
			}
		}.run(new GetCollectionRequest(XdsTools2Presenter.data().getCommandContext(), "actorcollections"));
	}

    /**
     * Loads the list of actors for the current parameters
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

			// Find a match in the system for that category of actor
			ActorType act = ActorType.findActor(selectedActor);
			if (act == null)
				return;

			// Populate the list of transaction types
			List<TransactionType> transactionTypes = act.getTransactions();

			parent.addQueryBoilerplate(
					new Runner(),
					TestsOverviewTab.transactionTypes,
					TestsOverviewTab.couplings,
					false); // not using a PID in this tab
		}
	}


	protected SiteSpec getSiteSelection() { return queryBoilerplate.getSiteSelection(); }


	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			//resultPanel.clear();
			//TODO Run the clickhandler actions
		}
	}

}
