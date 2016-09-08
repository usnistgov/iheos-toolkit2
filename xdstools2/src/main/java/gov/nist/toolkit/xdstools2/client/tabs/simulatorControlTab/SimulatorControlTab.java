package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actorfactory.client.SimulatorStats;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.ClickHandlerData;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllSimConfigsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllSitesCommand;
import gov.nist.toolkit.xdstools2.client.command.request.GetAllSimConfigsRequest;
import gov.nist.toolkit.xdstools2.client.event.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.SimulatorMessageViewTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od.OddsEditTab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimulatorControlTab extends GenericQueryTab {

	public SimulatorControlTab(BaseSiteActorManager siteActorManager) {
		super(siteActorManager,"");
	}

	public SimulatorControlTab() {
		super(new FindDocumentsSiteActorManager(),"");	}

	ListBox         actorSelectListBox = new ListBox();
	HorizontalPanel simConfigWrapperPanel = new HorizontalPanel();
	FlowPanel   simConfigPanel = new FlowPanel();
	TextArea        simIdsTextArea = new TextArea();
	TextBox         newSimIdTextBox = new TextBox();
	Button          createActorSimulatorButton = new Button("Create Actor Simulator");
	Button          loadSimulatorsButton = new Button("Load Simulators");
	FlexTable       table = new FlexTable();

	SimConfigSuper simConfigSuper;
	SimulatorControlTab self;

	@Override
	protected Widget buildUI() {
		return null;
	}

	@Override
	protected void bindUI() {

	}

	@Override
	protected void configureTabView() {

	}

	@Override
	public void onTabLoad(boolean select, String eventName) {
		self = this;

		simConfigSuper = new SimConfigSuper(this, simConfigPanel, getCurrentTestSession());

		registerTab(select, eventName);

		addActorReloader();
		
		runEnabled = false;
		samlEnabled = false;
		tlsEnabled = false;
		enableInspectResults = false;

		tabTopPanel.add(new HTML("<h2>Simulator Manager</h2>"));

		tabTopPanel.add(new HTML("<h3>Add new simulator to this test session</h3>"));

		HorizontalPanel actorSelectPanel = new HorizontalPanel();
		actorSelectPanel.add(HtmlMarkup.html("Select actor type"));
		actorSelectPanel.add(actorSelectListBox);
		loadActorSelectListBox();

		actorSelectPanel.add(HtmlMarkup.html("Simulator ID"));
		actorSelectPanel.add(newSimIdTextBox);

		actorSelectPanel.add(createActorSimulatorButton);
		createActorSimulatorButton.addClickHandler(new CreateButtonClickHandler(this, testSessionManager));

		tabTopPanel.add(actorSelectPanel);

		tabTopPanel.add(HtmlMarkup.html("<br />"));

		VerticalPanel tableWrapper = new VerticalPanel();
		table.setBorderWidth(1);
		HTML tableTitle = new HTML();
		tableTitle.setHTML("<h3>Simulators for this test session</h3>");
		tableWrapper.add(tableTitle);
		tableWrapper.add(table);

		tabTopPanel.add(tableWrapper);


		simConfigWrapperPanel.add(simConfigPanel);

		// force loading of sites in the back end
		// funny errors occur without this
		new GetAllSitesCommand() {

			@Override
			public void onComplete(Collection<Site> var1) {

			}
		}.run(getCommandContext());

		Xdstools2.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
			@Override
			public void onTestSessionChanged(TestSessionChangedEvent event) {
				loadSimStatus(event.value);
			}
		});

		loadSimStatus();
	}

	@Override
	public void onReload() {
		loadSimStatus();
	}


	void createNewSimulator(String actorTypeName, SimId simId) {
		if(!simId.isValid()) {
			new PopupMessage("SimId " + simId + " is not valid");
			return;
		}
		getToolkitServices().getNewSimulator(actorTypeName, simId, new AsyncCallback<Simulator>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("Error creating new simulator: " + caught.getMessage());
			}

			public void onSuccess(Simulator sconfigs) {
				for (SimulatorConfig config : sconfigs.getConfigs())
					simConfigSuper.add(config);
				simConfigSuper.reloadSimulators();
				loadSimStatus(getCurrentTestSession());
			}
		});
	} // createNewSimulator
	
	
	
	void loadActorSelectListBox() {
		getToolkitServices().getActorTypeNames(new AsyncCallback<List<String>>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("getActorTypeNames:" + caught.getMessage());
			}

			public void onSuccess(List<String> result) {
				actorSelectListBox.clear();
				if (result == null)
					return;
				actorSelectListBox.addItem("");
				for (String name : result)
					actorSelectListBox.addItem(name);
			}
		});
	}

	// columns
	int nameColumn = 0;
	int idColumn = 1;
	int typeColumn = 2;
	int pidPortColumn = 3;
	int statsColumn = 4;


	void buildTableHeader() {
		table.removeAllRows();
		table.clear();

		int row = 0;
		table.setText(row, nameColumn, "Name");
		table.setText(row, idColumn, "ID");
		table.setText(row, typeColumn, "Type");
		table.setText(row, pidPortColumn, "Patient Feed Port");
	}

	void loadSimStatus() {
		loadSimStatus(getCurrentTestSession());
	}

	void loadSimStatus(String user)  {
		new GetAllSimConfigsCommand() {

			@Override
			public void onComplete(List<SimulatorConfig> var1) {
				final List<SimulatorConfig> configs = var1;
				List<SimId> simIds = new ArrayList<>();
				for (SimulatorConfig config : configs)
					simIds.add(config.getId());
				try {
					getToolkitServices().getSimulatorStats(simIds, new AsyncCallback<List<SimulatorStats>>() {
						@Override
						public void onFailure(Throwable throwable) {
							new PopupMessage("Cannot load simulator stats - " + throwable.getMessage());
						}

						@Override
						public void onSuccess(List<SimulatorStats> simulatorStatses) {
							buildTable(configs, simulatorStatses);
						}
					});
				} catch (Exception e) {}

			}
		}.run(new GetAllSimConfigsRequest(getCommandContext(), user));
	}

	private void buildTable(List<SimulatorConfig> configs, List<SimulatorStats> stats) {
		buildTableHeader();
		int row = 1;
		for (SimulatorConfig config : configs) {
			table.setText(row, nameColumn, config.getDefaultName());
			table.setText(row, idColumn, config.getId().toString());
			table.setText(row, typeColumn, ActorType.findActor(config.getActorType()).getName());
			SimulatorConfigElement portConfig = config.get(SimulatorProperties.PIF_PORT);
			if (portConfig != null) {
				String pifPort = portConfig.asString();
				table.setText(row, pidPortColumn, pifPort);
			}
			row++;
		}
		// add the variable width stats columns and keep track of max column used
		int column = addSimStats(statsColumn, configs, stats);

		// now we know width of statsColumn so we can add button column after it
		row = 1;
		for (SimulatorConfig config : configs) {
			addButtonPanel(row, column, config);
			row++;
		}
	}

	// returns next available column
	private int addSimStats(int column, List<SimulatorConfig> configs, List<SimulatorStats> statss) {
		for (int colOffset=0; colOffset<SimulatorStats.displayOrder.size(); colOffset++) {
			String statType = SimulatorStats.displayOrder.get(colOffset);
			table.setText(0, column+colOffset, statType);
		}
		int row = 1;
		for (SimulatorConfig config : configs) {
			SimulatorStats stats = findSimulatorStats(statss, config.getId());
			if (stats == null) continue;
			for (int colOffset=0; colOffset<SimulatorStats.displayOrder.size(); colOffset++) {
				String statType = SimulatorStats.displayOrder.get(colOffset);
				String value = stats.stats.get(statType);
				if (value == null) value = "";
				table.setText(row, column+colOffset, value);
			}
			row++;
		}
		return column + SimulatorStats.displayOrder.size();
	}

	private SimulatorStats findSimulatorStats(List<SimulatorStats> statss, SimId simId) {
		for (SimulatorStats ss : statss) {
			if (ss.simId.equals(simId)) return ss;
		}
		return null;
	}

	private void addButtonPanel(int row, int maxColumn, final SimulatorConfig config) {
		SimId simId = config.getId();
		HorizontalPanel buttonPanel = new HorizontalPanel();
		table.setWidget(row, maxColumn, buttonPanel);

		Button logButton = new Button("Transaction Log");
		logButton.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
			@Override
			public void onClick(ClickEvent clickEvent) {
				SimulatorConfig config = getData();
				SimulatorMessageViewTab viewTab = new SimulatorMessageViewTab();
				viewTab.onTabLoad(true, config.getId().toString());
			}
		});
		buttonPanel.add(logButton);

		Button pidButton = new Button("Patient ID Feed");
		pidButton.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
			@Override
			public void onClick(ClickEvent clickEvent) {
				SimulatorConfig config = getData();
				PidEditTab editTab = new PidEditTab(config);
				editTab.onTabLoad(true, "PID-Edit");
			}
		});
		buttonPanel.add(pidButton);

		Button editButton = new Button("Configure");
		editButton.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
			@Override
			public void onClick(ClickEvent clickEvent) {
				SimulatorConfig config = getData();

//							GenericQueryTab editTab;
				if (ActorType.ONDEMAND_DOCUMENT_SOURCE.getShortName().equals(config.getActorType())) {
					// This simulator requires content state initialization
					OddsEditTab editTab;
					editTab = new OddsEditTab(self, config);
					editTab.onTabLoad(true, "ODDS");
				} else {
					// Generic state-less type simulators
					GenericQueryTab editTab = new EditTab(self, config);
					editTab.onTabLoad(true, "SimConfig");
				}


			}
		});
		buttonPanel.add(editButton);

		Button deleteButton = new Button("Delete");
		deleteButton.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
			@Override
			public void onClick(ClickEvent clickEvent) {
				SimulatorConfig config = getData();
				DeleteButtonClickHandler handler = new DeleteButtonClickHandler(self, config);
				handler.delete();
			}
		});
		buttonPanel.add(deleteButton);

		String u = "<a href=\"" +
				"siteconfig/" + simId.toString() + "\"" +
				" target=\"_blank\"" +
				">Download Site File</a>";
		HTML siteDownload = new HTML(u);

		buttonPanel.add(siteDownload);
	}

	public String getWindowShortName() {
		return "simmgr";
	}
}
