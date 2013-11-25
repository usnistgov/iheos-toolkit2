package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.xdstools2.client.Panel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.RadioButtonGroup;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.TabbedWindow;
import gov.nist.toolkit.xdstools2.client.ToolkitService;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimulatorMessageViewTab extends TabbedWindow {
	protected TabContainer myContainer;

	final protected ToolkitServiceAsync toolkitService = GWT
	.create(ToolkitService.class);

	HorizontalPanel simDisplayPanel = new HorizontalPanel();
	VerticalPanel simControlPanel = new VerticalPanel();
	VerticalPanel detailPanel = new VerticalPanel();
	HorizontalPanel inOutPanel = new HorizontalPanel();
	VerticalPanel transInPanel = new VerticalPanel();
	VerticalPanel transOutPanel = new VerticalPanel();
	VerticalPanel logPanel = new VerticalPanel();
	ScrollPanel scrollInPanel = new ScrollPanel();
	ScrollPanel scrollOutPanel = new ScrollPanel();
	ScrollPanel scrollLogPanel = new ScrollPanel();

	String simid = "";
	String currentActor;
	String currentTransaction;
	String currentEvent;

//	ActorNamesRadioButtonGroup simRadButtons;
	TransactionNamesRadioButtonGroup transactionRadButtons;

	//	HorizontalPanel actorNamesPanel = new HorizontalPanel();
	VerticalPanel transactionDisplayPanel = new VerticalPanel();
	VerticalPanel transactionNamesPanel = new VerticalPanel();
	ListBox transInstanceListBox = new ListBox();
	//	HTML ipAddressHTML = new HTML();
	ListBox simulatorNamesListBox = new ListBox();

	Button refreshButton = new Button("Refresh");
	Button deleteButton = new Button("Delete");
	
	HTML download = new HTML();
	
	public String getSimid() { return simid; }
	public String getCurrentActor() { return currentActor; }
	public String getCurrentTransaction() { return currentTransaction; }
	public String getCurrentEvent() { return currentEvent; }

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();

		container.addTab(topPanel, "Sim Msgs", select);
		addCloseButton(container,topPanel, null);

		topPanel.add(simDisplayPanel);
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

		simControlPanel.add(HtmlMarkup.html(HtmlMarkup.h2("Simulator Msg View")));
		simControlPanel.add(HtmlMarkup.html(HtmlMarkup.h2("Simulator:")));

		simControlPanel.add(simulatorNamesListBox);
		loadSimulatorNamesListBox();
		simulatorNamesListBox.addChangeHandler(new SimulatorNameChangeHandler());

		//		simControlPanel.add(actorNamesPanel);
		//		loadActorNames();

		simControlPanel.add(transactionDisplayPanel);

		transactionDisplayPanel.add(transactionNamesPanel);

		transactionDisplayPanel.add(HtmlMarkup.html(HtmlMarkup.bold("Messages")));
		transInstanceListBox.setVisibleItemCount(20);
		transactionDisplayPanel.add(transInstanceListBox);

		transInstanceListBox.addChangeHandler(transactionInstanceChoiceChanged);

		refreshButton.addClickHandler(refreshClickHandler);
		transactionDisplayPanel.add(refreshButton);

//		deleteButton.addClickHandler(deleteClickHandler);
//		transactionDisplayPanel.add(deleteButton);
		
		
		transactionDisplayPanel.add(download);
		
		
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
			
			simid = simNameToIdMap.get(simName);
			loadTransactionNames(simid);
		}

	}
	
	Map<String, String> simNameToIdMap;

	void loadSimulatorNamesListBox() {
		simulatorNamesListBox.clear();
		toolkitService.getActorSimulatorNameMap(new AsyncCallback<Map<String, String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getActorSimulatorNameMap: " + caught.getMessage());
			}

			public void onSuccess(Map<String, String> map) {
				simulatorNamesListBox.addItem("");
				simNameToIdMap = map;
				for (String name : map.keySet()) {
					simulatorNamesListBox.addItem(name);
				}
			}

		});
	}

//	final String simidFinal = simid;

	void loadTransactionNames(String simid) {
		simidFinal = simid;
		transInstanceListBox.clear();
		//getSimulatorTransactionNames
		toolkitService.getTransactionsForSimulator(simid, new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getSimulatorTransactionNames: " + caught.getMessage());
			}

			public void onSuccess(List<String> transNames) {
				transactionNamesPanel.clear();
				transactionNamesPanel.add(HtmlMarkup.html(HtmlMarkup.bold("Transaction: ")));


				transactionRadButtons = new TransactionNamesRadioButtonGroup(new Panel(transactionNamesPanel), simidFinal);
				transactionRadButtons.addButton("All");
				transactionRadButtons.buttons.get(0).setValue(true);
				transactionRadButtons.addButtons(transNames);
				
				transactionChosen(simidFinal, "all");
			}
			
		});

//		toolkitService.getTransactionsForActor(actorName, new AsyncCallback<List<String>>() {
//
//			public void onFailure(Throwable caught) {
//				if (caught.getMessage() != null)
//					new PopupMessage("Error: " + caught.getMessage());			
//			}
//
//			public void onSuccess(List<String> result) {
//				transactionNamesPanel.clear();
//				transactionNamesPanel.add(html(bold("Transaction: ")));
//
//
//				transactionRadButtons = new TransactionNamesRadioButtonGroup(new Panel(transactionNamesPanel), actorNameFinal);
//				transactionRadButtons.addButtons(result);
//
//			}
//
//		});
	}


	void actorChosen(String name) {
		currentActor = name;
		transactionNamesPanel.clear();
		transInstanceListBox.clear();
		clear();
		loadTransactionNames(name);
		currentEvent = null;
	}

	void transactionChosen(String simid, String transName) {
		currentTransaction = transName;
		clear();
		transInstanceListBox.clear();
		currentEvent = null;
		
		if ("all".equalsIgnoreCase(transName))
			transName = null;

		toolkitService.getTransInstances(simid, "", transName, new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				if (caught.getMessage() != null)
					new PopupMessage("Error: " + caught.getMessage());			
			}

			public void onSuccess(List<String> result) {
				transInstanceListBox.clear();
				
//				for (int i=result.size()-1; i >= 0; i--)
//					transInstanceListBox.addItem(result.get(i));
				for (String x : result) 
					transInstanceListBox.addItem(x);

			}

		});

	}

	ChangeHandler transactionInstanceChoiceChanged = new ChangeHandler() {

		public void onChange(ChangeEvent event) {
			int selectedItem = transInstanceListBox.getSelectedIndex();
			String label = transInstanceListBox.getItemText(selectedItem);
			loadTransactionInstanceDetails(label);
			
			String messageId = getMessageIdFromLabel(label);
			currentTransaction = getTransactionFromLabel(label);

			String u = "<a href=\"" +
			"/xdstools2/message/" + simid + "/" + currentActor + "/" + currentTransaction + "/" + messageId + "\"" +
//			" target=\"_blank\"" + 
			">Download Message</a>";
			download.setHTML(u);


		}
	};

	HTML htmlize(String header, String in) {
		HTML h = new HTML(
				"<b>" + header + "</b><br /><br />" +

				in/*.replaceAll("<", "&lt;")
				.replaceAll("\n\n", "\n")
				.replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
				.replaceAll(" ", "&nbsp;")
				.replaceAll("\n", "<br />")*/
		);
		return h;
	}
	
	String getMessageIdFromLabel(String label) {
		String[] parts = label.split(" ");
		if (parts.length == 2)
			return parts[0];
		return label;
	}
	
	String getTransactionFromLabel(String label) {
		String[] parts = label.split(" ");
		if (parts.length == 2)
			return parts[1];
		return label;
	}

	void loadTransactionInstanceDetails(String label) {
		String simid = this.simidFinal;
		String actor = currentActor;
		String trans = getTransactionFromLabel(label);
		String messageId = getMessageIdFromLabel(label);

		currentEvent = label;

		scrollInPanel.clear();
		scrollOutPanel.clear();
		scrollLogPanel.clear();

		toolkitService.getTransactionRequest(simid, actor, trans, messageId, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				if (caught.getMessage() != null)
					new PopupMessage("Error: " + caught.getMessage());			
			}

			public void onSuccess(String result) {
				scrollInPanel.add(htmlize("Request Message", result));
			}

		});

		toolkitService.getTransactionResponse(simid, actor, trans, messageId, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				if (caught.getMessage() != null)
					new PopupMessage("Error: " + caught.getMessage());			
			}

			public void onSuccess(String result) {
				scrollOutPanel.add(htmlize("Response Message", result));
			}

		});

		toolkitService.getTransactionLog(simid, actor, trans, messageId, new AsyncCallback<String>() {

			public void onFailure(Throwable caught) {
				if (caught.getMessage() != null)
					new PopupMessage("Error: " + caught.getMessage());			
			}

			public void onSuccess(String result) {
				scrollLogPanel.add(htmlize("Log", result));
			}

		});

	}

	ClickHandler refreshClickHandler = new ClickHandler() {

		public void onClick(ClickEvent event) {
			loadTransactionNames(simidFinal);
			transactionChosen(simidFinal, transName);

			clear();
		}

	};

	ClickHandler deleteClickHandler = new ClickHandler() {

		public void onClick(ClickEvent event) {
			String url = GWT.getModuleBaseURL() + "simulator/del" 
			+ "/" + simid
			+ "/" + currentActor
			+ "/" + getTransactionFromLabel(event.toString())
			+ "/" + getMessageIdFromLabel(event.toString());

			doDelete(url);
		}

	};

	void clear() {
		scrollInPanel.clear();
		scrollOutPanel.clear();
		scrollLogPanel.clear();
	}


	// several background operations need to make sure the 
	// SimServlet has initialized since that is where the servlet initialization
	// parameters are read.  Issue a HTTP Get for something that is known to fail
	// just to get the servlet to initialize
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

	public void doDelete(String url) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);

		try {
			@SuppressWarnings("unused")
			Request response = builder.sendRequest(null, new RequestCallback() {
				public void onError(Request request, Throwable exception) {
					if (exception.getMessage() != null)
						new PopupMessage("Error: " + exception);
				}

				public void onResponseReceived(Request request, Response response) {
					int status = response.getStatusCode();
					if (status == 200) {
						transactionChosen(currentActor, currentTransaction);					}
					else
						new PopupMessage("Failure");
				}
			});
		} catch (RequestException e) {
			// Code omitted for clarity
		}
	}


	String transName = "";
	String simidFinal = "";

	class TransactionNamesRadioButtonGroup extends RadioButtonGroup {
		String simid;

		TransactionNamesRadioButtonGroup(Panel p, String simid) {
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

	public String getWindowShortName() {
		return "simmsgview";
	}



}
