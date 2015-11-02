package gov.nist.toolkit.xdstools2.client.widgets.siteSelectionWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.QueryBoilerplate;
import org.junit.runner.Runner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 11/1/2015.
 */
public class SiteSelectionWidget extends HorizontalPanel {
	ListBox selectActorList = new ListBox();
	final protected ToolkitServiceAsync toolkitService = GWT.create(ToolkitService.class);
	Map<String, String> actorCollectionMap;  // name => description
	String selectedActor;
	protected QueryBoilerplate queryBoilerplate = null;
	public VerticalPanel resultPanel = new VerticalPanel();
	GenericQueryTab parent;

	final String allSelection = "-- All --";
	final String chooseSelection = "-- Choose --";



	public SiteSelectionWidget(GenericQueryTab _parent){
		parent = _parent;

		add(selectActorList);
		loadActorNames();
		selectActorList.addChangeHandler(new ActorSelectionChangeHandler());
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

    /**
     * Loads the list of actors for the current parameters
     */
	class ActorSelectionChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectedI = selectActorList.getSelectedIndex();
			selectedActor = selectActorList.getValue(selectedI);
			if (selectedActor == null)
				return;
			if ("".equals(selectedActor))
				return;
			String sel = selectedActor;

			// these names are found in war/toolkit/testkit/actorcollections/xxxx.tc
			// list all sites
			ActorType act = ActorType.findActor(sel);
			if (act == null)
				return;

			List<TransactionType> tt = act.getTransactions();

			parent.addQueryBoilerplate(
					new Runner(),
					tt,
					new CoupledTransactions(),
					true);
		}
	}


	protected SiteSpec getSiteSelection() { return queryBoilerplate.getSiteSelection(); }


	class Runner implements ClickHandler {

		public void onClick(ClickEvent event) {
			resultPanel.clear();
			new PopupMessage("Run the clickhandler actions");
		}
	}

}
