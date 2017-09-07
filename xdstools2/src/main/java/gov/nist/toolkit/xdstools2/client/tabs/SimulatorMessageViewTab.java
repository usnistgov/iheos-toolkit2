package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.TransactionInstance;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.services.shared.Message;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.Panel1;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.inspector.MetadataInspectorTab;
import gov.nist.toolkit.xdstools2.client.util.ToolkitLink;
import gov.nist.toolkit.xdstools2.client.util.activitiesAndPlaces.SimLog;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.widgets.RadioButtonGroup;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimIdsForUserRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimulatorEventRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTransactionRequest;

import java.util.ArrayList;
import java.util.List;

public class SimulatorMessageViewTab extends ToolWindow {
	private HorizontalPanel simDisplayPanel = new HorizontalPanel();
	private VerticalPanel simControlPanel = new VerticalPanel();
	private VerticalPanel detailPanel = new VerticalPanel();
	private HorizontalPanel inOutPanel = new HorizontalPanel();
	private VerticalPanel transInPanel = new VerticalPanel();
	private VerticalPanel transOutPanel = new VerticalPanel();
	private VerticalPanel logPanel = new VerticalPanel();
	private ScrollPanel scrollInPanel = new ScrollPanel();
	private ScrollPanel scrollOutPanel = new ScrollPanel();
	private ScrollPanel scrollLogPanel = new ScrollPanel();
	private HorizontalFlowPanel simSelectionDisplayPanel = new HorizontalFlowPanel();
	private FlowPanel simSelectionPanel = new FlowPanel();
	private HorizontalFlowPanel eventLinkPanel = new HorizontalFlowPanel();
	private HorizontalFlowPanel filterPanel = new HorizontalFlowPanel();
	private TextBox filterField = new TextBox();

	private SimId simid = null;//new SimId("");
	private String currentActor;
	private String currentTransaction;
	private String currentEvent;


	private List<TransactionInstance> transactionInstances = new ArrayList<>();
	private TransactionInstance currentTransactionInstance = null;

	private VerticalPanel transactionDisplayPanel = new VerticalPanel();
	private VerticalPanel transactionNamesPanel = new VerticalPanel();
	private ListBox transInstanceListBox = new ListBox();

	//	Only one of these will be displayed depending on whether simId is set
	private ListBox simulatorNamesListBox = new ListBox();
	private Label simulatorNameLabel = new Label();

	private FlowPanel simNameOrNamesPanel = new FlowPanel();

	private Button refreshButton = new Button("Refresh");
	private Button inspectRequestButton = new Button("Inspect Request");
	private Button inspectResponseButton = new Button("Inspect Response");
	private Button deleteButton = new Button("Delete");

	private HTML download = new HTML();
	private SimulatorMessageViewTab instance;


	public SimulatorMessageViewTab() {
		instance = this;
		simSelectionDisplayPanel.add(HtmlMarkup.html(HtmlMarkup.h2("Simulator:")));
		simSelectionDisplayPanel.add(simNameOrNamesPanel);
		simNameOrNamesPanel.add(simulatorNamesListBox);  // will be removed later is simId is set
	}

	public SimId getSimid() { return simid; }
	public void setSimId(SimId simid) {
		this.simid = simid;
		simulatorNameLabel.setText(simid.toString());
		simNameOrNamesPanel.clear();
		simNameOrNamesPanel.add(simulatorNameLabel);
		refreshButton.setEnabled(false);
	}

	public void onTabLoad(SimId simid) {
		this.simid = simid;
		onTabLoad(true, simid.toString());
	}

	private HorizontalFlowPanel linkPanel = new HorizontalFlowPanel();

	// If eventName is null then display list of simulators.  If non-null then it is
	// the simulator id. In this case do not allow simulator selection.
	@Override
	public void onTabLoad(boolean select, String eventName) {

		registerTab(select, eventName);

		tabTopPanel.add(linkPanel);

		tabTopPanel.add(simDisplayPanel);
		simDisplayPanel.add(simControlPanel);
		simDisplayPanel.add(detailPanel);

		detailPanel.add(inOutPanel);
		detailPanel.add(logPanel);

		scrollInPanel.setWidth("500px");
		scrollInPanel.setHeight("300px");
		transInPanel.setBorderWidth(1);
		transInPanel.add(scrollInPanel);

		scrollOutPanel.setWidth("500px");
		scrollOutPanel.setHeight("300px");
		transOutPanel.setBorderWidth(1);
		transOutPanel.add(scrollOutPanel);

		scrollLogPanel.setWidth("1000px");
		scrollLogPanel.setHeight("300px");
		logPanel.setBorderWidth(1);
		logPanel.add(scrollLogPanel);

		inOutPanel.add(transInPanel);
		inOutPanel.add(transOutPanel);

		simControlPanel.add(HtmlMarkup.html(HtmlMarkup.h2("Transaction Log")));
		simControlPanel.add(simSelectionDisplayPanel);

		if (simid != null) {   // not moved
			loadTransactionNames(simid);
		}
		loadSimulatorNamesListBox();
		simulatorNamesListBox.addChangeHandler(new SimulatorNameChangeHandler());

		// continue here
		simControlPanel.add(transactionDisplayPanel);

		transactionDisplayPanel.add(transactionNamesPanel);

		filterPanel.add(filterField);
		Button filterButton = new Button("Filter");
		filterButton.addClickHandler(new FilterClickHandler());
		filterField.addKeyDownHandler(new FilterKeyDownHandler());
		filterPanel.add(filterButton);
		transactionDisplayPanel.add(filterPanel);

		transactionDisplayPanel.add(HtmlMarkup.html(HtmlMarkup.bold("Messages")));
		transInstanceListBox.setVisibleItemCount(20);
		transactionDisplayPanel.add(transInstanceListBox);

		transInstanceListBox.addChangeHandler(transactionInstanceChoiceChanged);

		transactionDisplayPanel.add(eventLinkPanel);

		refreshButton.addClickHandler(refreshClickHandler);
		transactionDisplayPanel.add(refreshButton);

		inspectRequestButton.addClickHandler(inspectRequestClickHandler);
		transactionDisplayPanel.add(inspectRequestButton);

		inspectResponseButton.addClickHandler(inspectResponseClickHandler);
		transactionDisplayPanel.add(inspectResponseButton);

		transactionDisplayPanel.add(download);
	}

	class FilterClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent clickEvent) {
			updateTransactionsDisplay();
		}
	}

	class FilterKeyDownHandler implements KeyDownHandler {

		@Override
		public void onKeyDown(KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				updateTransactionsDisplay();
			}
		}
	}


	class SimulatorNameChangeHandler implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectedI = simulatorNamesListBox.getSelectedIndex();
			String simName = simulatorNamesListBox.getItemText(selectedI);
			if (simName.equals(""))
				return;

			String[] parts = simName.split("\\.");
			if (parts.length == 2)
				currentActor = parts[1];

			loadTransactionNames(getServerSimId(new SimId(simName)));
		}
	}

	/**
	 * this simIds returned from the server have full content so they should be used in all
	 * calls to the server.  This call looks up the SimId
	 * @param simId - minimal SimId (only needs user and id attributes)
	 * @return - fully configured SimId instance loaded from server
	 */
	private SimId getServerSimId(SimId simId) {
		for (SimId sid : simIds) {
			if (sid.equals(simId)) return sid;
		}
		return null;
	}

	/**
	 * SimIds loaded from server - fully loaded with actor type and isFhir status
	 */
	private List<SimId> simIds;

	private void loadSimulatorNamesListBox() {
		simulatorNamesListBox.clear();
		new GetSimIdForUser(){
			@Override
			public void onComplete(List<SimId> result) {
				simulatorNamesListBox.addItem("");
				simIds = result;
				for (SimId simId : result) {
					simulatorNamesListBox.addItem(simId.toString());
				}
				// Auto-load if there is only one entry
				if (result.size()==1) {
					simulatorNamesListBox.setSelectedIndex(0);
					simid = new SimId(simulatorNamesListBox.getSelectedValue());
					loadTransactionNames(simid);
				}
			}
		}.run(new GetSimIdsForUserRequest(getCommandContext(), null));
	}

	public void loadTransactionNames(SimId simid) {
		simidFinal = simid;
		transInstanceListBox.clear();
		//getSimulatorTransactionNames
		new GetTransactionsForSimulatorCommand(){
			@Override
			public void onComplete(List<String> result) {
				transactionNamesPanel.clear();
				transactionChosen(simidFinal, "all");
				// Auto-load if there is only one entry
			 	if (result!=null && result.size()==1) {
			 		transInstanceListBox.setSelectedIndex(0);
			 		transactionInstanceSelected();
				}
			}
		}.run(new GetTransactionRequest(getCommandContext(),simid));
	}

	private TransactionInstance findTransactionInstance(String label) {
		if (label == null) return null;
		for (TransactionInstance ti : transactionInstances) {
			if (label.equals(ti.messageId)) return ti;
			if (label.equals(ti.labelInterpretedAsDate)) return ti;
		}
		return null;
	}


	public void transactionChosen(SimId simid, String transName) {
		currentTransaction = transName;
		clear();
		transInstanceListBox.clear();
		currentEvent = null;

		if ("all".equalsIgnoreCase(transName))
			transName = null;
		new GetTransactionInstancesCommand(){
			@Override
			public void onComplete(List<TransactionInstance> result) {
				transactionInstances = result;
				updateTransactionsDisplay();
			}
		}.run(new GetTransactionRequest(getCommandContext(),simid,"",transName));
	}

	private void updateTransactionsDisplay() {
		String filterText = filterField.getText().trim().toLowerCase();
		transInstanceListBox.clear();
		currentTransactionInstance = null;
		updateEventLink();

		for (TransactionInstance ti : transactionInstances) {
			String displayText = ti.toString();
			String displayTextForComparison = displayText.toLowerCase();
			if (selectedMessageId == null || selectedMessageId.equals("all")) { // no message selected
				if (filterText.isEmpty() || displayTextForComparison.contains(filterText)) {
					transInstanceListBox.addItem(displayText, ti.messageId);
					currentTransactionInstance = ti;
				}
			}
			else {
				if (selectedMessageId.equals(ti.messageId)) { // this is the selected message
					if (filterText.isEmpty() || displayTextForComparison.contains(filterText)) {
						transInstanceListBox.addItem(displayText, ti.messageId);
						currentTransactionInstance = ti;
						updateEventLink();
					}
				}
			}
		}
	}

//	static private Label eventLinkLabel = new Label("SimResource Link: ");
	private void updateEventLink() {
//		linkPanel.clear();
//		linkPanel.add(eventLinkLabel);
//		if (currentTransactionInstance != null) {
//			SimLog simLog = new SimLog(currentTransactionInstance);
//			Label eventLink = new Label(Xdstools2.toolkitBaseUrl + "#SimLog:" + (new SimLog.Tokenizer()).getToken(simLog));
//			linkPanel.add(eventLink);
//		}

		linkPanel.clear();
		if (currentTransactionInstance != null) {
			SimLog simLog = new SimLog(currentTransactionInstance);
			linkPanel.add(new ToolkitLink("SimResource Link: ", "#SimLog:" + (new SimLog.Tokenizer()).getToken(simLog)));
		}
	}

	private ChangeHandler transactionInstanceChoiceChanged = new ChangeHandler() {

		public void onChange(ChangeEvent event) {
			transactionInstanceSelected();
		}
	};

	private void transactionInstanceSelected() {
		int selectedItem = transInstanceListBox.getSelectedIndex();
		String value = transInstanceListBox.getValue(selectedItem);
		TransactionInstance ti = findTransactionInstance(value);
		if (ti == null) return;
		currentTransactionInstance = ti;
		updateEventLink();
		loadTransactionInstanceDetails(ti);

		String messageId = getMessageIdFromLabel(value);
		currentTransaction = getTransactionFromLabel(value);

		String u = "<a href=\"" +
                "message/" + simid + "/" + currentActor + "/" + currentTransaction + "/" + messageId + "\"" +
//			" target=\"_blank\"" +
                ">Download Message</a>";
		download.setHTML(u);
	}

	private HTML htmlize(String header, String in) {
		HTML h = new HTML(
				(header == null) ? "" : "<b>" + header + "</b><br /><br />" +

						in.replaceAll("<", "&lt;")
								.replaceAll("\n\n", "\n")
								.replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
								.replaceAll(" ", "&nbsp;")
								.replaceAll("\n", "<br />")
		);
		return h;
	}

	private String getMessageIdFromLabel(String label) {
		String[] parts = label.split(" ");
		if (parts.length == 2)
			return parts[0];
		return label;
	}

	private String getTransactionFromLabel(String label) {
		String[] parts = label.split(" ");
		if (parts.length == 2)
			return parts[1];
		return label;
	}

	private String selectedMessageId = null;

	public void selectByMessageId(String messageId) {
		List<String> values = new ArrayList<>();
		values.add(messageId);
		for (int i=0; i<transInstanceListBox.getItemCount(); i++) {
			String id = transInstanceListBox.getValue(i);
			values.add(id);
			if (messageId.equals(id)) {
				transInstanceListBox.setSelectedIndex(i);
				return;
			}
		}
		selectedMessageId = messageId;  // not loaded yet - check this after loaded
	}

	public void loadTransactionInstanceDetails(TransactionInstance ti) {
		SimId simid = this.simidFinal;
		if (ti.actorType == null) return;
		String actor = ti.actorType.getShortName();
		String trans = ti.trans;
		String messageId = ti.messageId;

		scrollInPanel.clear();
		scrollOutPanel.clear();
		scrollLogPanel.clear();

		new GetTransactionRequestCommand(){
			@Override
			public void onComplete(Message message) {
				FlowPanel panel = new FlowPanel();
				panel.add(htmlize("Request Message<br />", message.getParts().get(0)));
				scrollInPanel.add(panel);
			}
		}.run(new GetTransactionRequest(getCommandContext(),simid,actor,trans,messageId));

		new GetTransactionResponseCommand(){
			@Override
			public void onComplete(Message message) {
				FlowPanel panel = new FlowPanel();
				panel.add(htmlize("Response Message<br />", message.getParts().get(0)));
				scrollOutPanel.add(panel);
			}
		}.run(new GetTransactionRequest(getCommandContext(),simid,actor,trans,messageId));

		new GetTransactionLogCommand(){
			@Override
			public void onComplete(String result) {
				scrollLogPanel.add(htmlize("Log", result));
			}
		}.run(new GetTransactionRequest(getCommandContext(),simid,actor,trans,messageId));
	}

	ClickHandler refreshClickHandler = new ClickHandler() {

		public void onClick(ClickEvent event) {
			loadTransactionNames(simidFinal);
			transactionChosen(simidFinal, transName);

			clear();
		}

	};

	private ClickHandler inspectRequestClickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent clickEvent) {
			new GetSimulatorEventRequestCommand(){
				@Override
				public void onComplete(Result result) {
					displayResult(result);
				}
			}.run(new GetSimulatorEventRequest(getCommandContext(),currentTransactionInstance));
		}
	};

	private void displayResult(Result result) {
		List<Result> results = new ArrayList<Result>();
		results.add(result);
		MetadataInspectorTab tab = new MetadataInspectorTab();
		tab.setResults(results);
		SiteSpec siteSpec = new SiteSpec(getSimid().toString(), currentTransactionInstance.actorType, null);
		tab.setSiteSpec(siteSpec);
		tab.onTabLoad(true, "Insp");
	}

	private ClickHandler inspectResponseClickHandler = new ClickHandler() {
		@Override
		public void onClick(ClickEvent clickEvent) {
			new GetSimulatorEventResponseCommand(){
				@Override
				public void onComplete(Result result) {
					displayResult(result);
				}
			}.run(new GetSimulatorEventRequest(getCommandContext(),currentTransactionInstance));
		}
	};

//	ClickHandler deleteClickHandler = new ClickHandler() {
//
//		public void onClick(ClickEvent event) {
//			String url = GWT.getModuleBaseURL() + "simulator/del"
//			+ "/" + simid
//			+ "/" + currentActor
//			+ "/" + getTransactionFromLabel(event.toString())
//			+ "/" + getMessageIdFromLabel(event.toString());
//
//			doDelete(url);
//		}
//
//	};

	void clear() {
		scrollInPanel.clear();
		scrollOutPanel.clear();
		scrollLogPanel.clear();
	}


	// several background operations need to make sure the 
	// SimServlet has initialized since that is where the servlet initialization
	// parameters are read.  Issue a HTTP Get for something that is known to fail
	// just to getRetrievedDocumentsModel the servlet to initialize
	public void initSimServlet() {

		String url = GWT.getModuleBaseURL() + "simulator/del";

		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			@SuppressWarnings("unused")
			Request response = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
				}

				public void onResponseReceived(Request request, Response response) {
				}
			});
		} catch (RequestException e) {
			// Code omitted for clarity
		}

	}

	private String transName = "";
	private SimId simidFinal = new SimId("");

	class TransactionNamesRadioButtonGroup extends RadioButtonGroup {
		SimId simid;

		TransactionNamesRadioButtonGroup(Panel1 p, SimId simid) {
			super("TransactionNamesGroup", p);
			this.simid = simid;
			simidFinal = simid;

			choiceChangedHandler  = new ValueChangeHandler<Boolean>() {

				public void onValueChange(ValueChangeEvent<Boolean> ignored) {

					RadioButton rb = null;
					for (RadioButton r : buttons) {
						if (r.getValue()) {
							rb = r;
							break;
						}
					}

					if (rb == null)
						return;

					transName = getNameForRadioButton(rb);
					if (transName == null)
						return;

					if ("All".equals(transName))
						transName = null;

					transactionChosen(simidFinal, transName);

				}
			};
		}

	}

	public void setActor(String actor) {
		currentActor = actor;
	}

	public void setTransaction(String transaction) {
		currentTransaction = transaction;
	}

	public String getWindowShortName() {
		return "simmsgview";
	}

}
