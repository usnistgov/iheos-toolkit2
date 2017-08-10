package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CompositeCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.HasCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.ClickHandlerData;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.StringSort;
import gov.nist.toolkit.xdstools2.client.command.command.GetActorTypeNamesCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllSimConfigsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllSitesCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetNewSimulatorCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetSimulatorStatsCommand;
import gov.nist.toolkit.xdstools2.client.event.TabSelectedEvent;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEventHandler;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.SimulatorMessageViewTab;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab.od.OddsEditTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.GetAllSimConfigsRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetNewSimulatorRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimulatorStatsRequest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class SimulatorControlTab extends GenericQueryTab {

    ListBox         actorSelectListBox = new ListBox();
    private HorizontalPanel simConfigWrapperPanel = new HorizontalPanel();
    private FlowPanel   simConfigPanel = new FlowPanel();
    TextArea        simIdsTextArea = new TextArea();
    TextBox         newSimIdTextBox = new TextBox();
    private Button          createActorSimulatorButton = new Button("Create Actor Simulator");
    Button          loadSimulatorsButton = new Button("Load Simulators");
    private FlexTable       table = new FlexTable();
    // Cell table
    CellTable<SimInfo> newSimTable = new CellTable<SimInfo>();
    // Create a data provider.
    ListDataProvider<SimInfo> dataProvider = new ListDataProvider<SimInfo>();
    private FlowPanel simCtrlContainer;

    SimConfigSuper simConfigSuper;
    private SimulatorControlTab self;

    public SimulatorControlTab(BaseSiteActorManager siteActorManager) {
        super(siteActorManager);
    }

    public SimulatorControlTab() {  super(new FindDocumentsSiteActorManager());	}


    interface ScTabResources extends ClientBundle {
        public static final ScTabResources INSTANCE = GWT.create(ScTabResources.class);

        @Source("icons/log-file-format-symbol.png")
        ImageResource getLogIcon();
    }

//    ScTabResources scTabResources = GWT.create(ScTabResources.class);


    public abstract class AbstractActionButton<SimInfo> implements HasCell<SimInfo, SimInfo> {

        @Override
        public Cell<SimInfo> getCell() {
            return new ActionCell<SimInfo>("Button title", new ActionCell.Delegate<SimInfo>() {
                @Override
                public void execute(SimInfo object) {
                    AbstractActionButton.this.execute(object);
                }
            }) {
                @Override
                public void render(Context context, SimInfo data, SafeHtmlBuilder sb) {
                    AbstractActionButton.this.render(context, data, sb);
                }
            };
        }

        //Replaced by delegate but still need to be overriden
        @Override
        public FieldUpdater<SimInfo, SimInfo> getFieldUpdater() {
            return null;
        }

        @Override
        public SimInfo getValue(SimInfo object) {
            return object;
        }

        /**
         * You can override this method to render your button differently. Not mandatory
         * @param context
         * @param data
         * @param sb
         */
        public abstract void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb);

        /**
         * Called when the button is clicked
         * @param object
         */
        public abstract void execute(SimInfo object);
    }

    private Column<SimInfo, SimInfo> getButtonColumn() {
        return new Column<SimInfo, SimInfo>(getButtonsCell()) {
            @Override
            public SimInfo getValue(SimInfo object) {
                return object;
            }
        };
    }


    @Override
	protected Widget buildUI() {
        simCtrlContainer = new FlowPanel();
        self = this;

        simConfigSuper = new SimConfigSuper(this, simConfigPanel, getCurrentTestSession());

		addActorReloader();

        runEnabled = false;
        samlEnabled = false;
        tlsEnabled = false;
        enableInspectResults = false;

		simCtrlContainer.add(new HTML("<h2>Simulator Manager</h2>"));

		simCtrlContainer.add(new HTML("<h3>Add new simulator to this test session</h3>"));

        HorizontalPanel actorSelectPanel = new HorizontalPanel();
        actorSelectPanel.add(HtmlMarkup.html("Select actor type"));
        actorSelectPanel.add(actorSelectListBox);
        loadActorSelectListBox();

        actorSelectPanel.add(HtmlMarkup.html("Simulator ID"));
        actorSelectPanel.add(newSimIdTextBox);

        actorSelectPanel.add(createActorSimulatorButton);
        createActorSimulatorButton.addClickHandler(new CreateButtonClickHandler(this, testSessionManager));

		simCtrlContainer.add(actorSelectPanel);

		simCtrlContainer.add(HtmlMarkup.html("<br />"));

        VerticalPanel tableWrapper = new VerticalPanel();
//		table.setBorderWidth(1);
        HTML tableTitle = new HTML();
        tableTitle.setHTML("<h2>Simulators for this test session</h2>");
        tableWrapper.add(tableTitle);
        tableWrapper.add(table);

		simCtrlContainer.add(tableWrapper);

        simConfigWrapperPanel.add(simConfigPanel);


        simCtrlContainer.add(newSimTable);
        SimplePager simplePager = new SimplePager();
        simplePager.setDisplay(newSimTable);
        simplePager.setPageSize(50);
        simCtrlContainer.add(simplePager);

        TextColumn<SimInfo> nameColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return simInfo.getSimulatorConfig().getDefaultName();
            }
        };

        TextColumn<SimInfo> idColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return simInfo.getSimulatorConfig().getId().toString();
            }
        };

        TextColumn<SimInfo> createdDtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return simInfo.getSimulatorConfig().get(SimulatorProperties.creationTime).asString();
            }
        };

        TextColumn<SimInfo> lastTranColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return simInfo.getLastTransaction();
            }
        };

        TextColumn<SimInfo> lastAccessedDtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return simInfo.getLastAccessedDt();
            }
        };

        TextColumn<SimInfo> typeColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                return ActorType.findActor(simInfo.getSimulatorConfig().getActorType()).getName();
            }
        };

        TextColumn<SimInfo> pifPortColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {

                SimulatorConfigElement portConfig = simInfo.getSimulatorConfig().get(SimulatorProperties.PIF_PORT);
                if (portConfig!=null)
                    return portConfig.asString();
                return null;
            }
        };

        TextColumn<SimInfo> ssCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.SUBMISSION_SET_COUNT);
                }
                return null;
            }
        };

        TextColumn<SimInfo> desCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.DOCUMENT_ENTRY_COUNT);
                }
                return null;
            }
        };

        TextColumn<SimInfo> foldersCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.FOLDER_COUNT);
                }
                return null;
            }
        };

        TextColumn<SimInfo> docsCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.DOCUMENT_COUNT);
                }
                return null;
            }
        };

        TextColumn<SimInfo> pidsCtColumn = new TextColumn<SimInfo>() {
            @Override
            public String getValue(SimInfo simInfo) {
                if (simInfo.getSimulatorStats()!=null) {
                    return simInfo.getSimulatorStats().getStats().get(SimulatorStats.PATIENT_ID_COUNT);
                }
                return null;
            }
        };


        /*
        Column<SimInfo, ImageResource> logImgColumn =
                new Column<SimInfo, ImageResource>(new ImageResourceCell()) {
                    @Override
                    public ImageResource getValue(SimInfo object) {
                        return ScTabResources.INSTANCE.getLogIcon();
                    }

                };
        */

        SafeHtmlBuilder logIconImgHtml = new SafeHtmlBuilder();
        logIconImgHtml.appendHtmlConstant("<img src=\"icons2/log-file-format-symbol.png\">");
        Column<SimInfo, SimInfo> logActionCol =
        new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>(logIconImgHtml.toSafeHtml(), new ActionCell.Delegate<SimInfo>() {
            @Override
            public void execute(SimInfo simInfo) {
                SimulatorConfig config = simInfo.getSimulatorConfig();
                SimulatorMessageViewTab viewTab = new SimulatorMessageViewTab();
                viewTab.onTabLoad(config.getId());
            }
        })) {
            @Override
            public SimInfo getValue(SimInfo simInfo) {
                return simInfo;
            }
        };

        SafeHtmlBuilder pidIconImgHtml = new SafeHtmlBuilder();
        pidIconImgHtml.appendHtmlConstant("<img src=\"icons2/id.png\">");
        Column<SimInfo, SimInfo> idActionCol =
        new Column<SimInfo, SimInfo>(new ActionCell<SimInfo>(pidIconImgHtml.toSafeHtml(), new ActionCell.Delegate<SimInfo>() {
            @Override
            public void execute(SimInfo simInfo) {
                SimulatorConfig config = simInfo.getSimulatorConfig();
                PidEditTab editTab = new PidEditTab(config);
                editTab.onTabLoad(true, "PIDEdit");
            }
        })) {
            @Override
            public SimInfo getValue(SimInfo simInfo) {
                return simInfo;
            }
        };


        Column<SimInfo, SimInfo> editActionCol = getButtonColumn();






        /*
         * Columns
         * Name	ID	[created dt] [last tran] [last accessed dt == filter on date],  Type	Patient Feed Port	SubmissionSets	DocumentEntries	Folders	Documents	PatientIds	Action
         *
         * Sorting
         * Use the Hl7 date values in the comparator
         */

        newSimTable.addColumn(nameColumn, "Name");
        newSimTable.addColumn(idColumn, "ID");
        newSimTable.addColumn(createdDtColumn,"Created Date");
        newSimTable.addColumn(lastTranColumn, "Last Tran");
        newSimTable.addColumn(lastAccessedDtColumn, "Last Tran Date");
        newSimTable.addColumn(typeColumn, "Type");
        newSimTable.addColumn(pifPortColumn, "Patient Feed Port");
        newSimTable.addColumn(ssCtColumn, "SubmissionSets");
        newSimTable.addColumn(desCtColumn, "DocumentEntries");
        newSimTable.addColumn(foldersCtColumn, "Folders");
        newSimTable.addColumn(docsCtColumn, "Documents");
        newSimTable.addColumn(pidsCtColumn, "PatientIds");
        // TODO add image cells
//        newSimTable.addColumn(logImgColumn, "Action");
        newSimTable.addColumn(logActionCol, "Action");
        newSimTable.addColumn(editActionCol,"");

//        newSimTable.addColumn(

        // Connect the table to the data provider.
        dataProvider.addDataDisplay(newSimTable);




        return simCtrlContainer;
	}


    private CompositeCell getButtonsCell() {
        List<HasCell<SimInfo, ?>> cells = new LinkedList<>();

        HasCell<SimInfo,SimInfo> normalEdit =   new AbstractActionButton<SimInfo>() {
            @Override
            public void execute(final SimInfo object) {
                //Action on button click
                Window.alert("You clicked " + object.getSimulatorConfig().getDefaultName());

            }

            @Override
            public void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb) {
                //

                if (!ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType()) ) {
                    SafeHtmlBuilder mySb = new SafeHtmlBuilder();

                    mySb.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\" src=\"icons2/edit.png\">");

                    sb.append(mySb.toSafeHtml());
                }

            }
        };

        HasCell<SimInfo,SimInfo> rgEdit = new AbstractActionButton<SimInfo>() {
            @Override
            public void execute(final SimInfo object) {
                //Action on button click
                Window.alert("You clicked RG" + object.getSimulatorConfig().getDefaultName());

            }

            @Override
            public void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb) {
                //
                SafeHtmlBuilder mySb = new SafeHtmlBuilder();


                if (ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType()) ) {
                    mySb.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\"  src=\"icons2/edit-rg.png\">");
                    sb.append(mySb.toSafeHtml());
                }

            }
        };

        HasCell<SimInfo,SimInfo> rgOdEdit = new AbstractActionButton<SimInfo>() {
            @Override
            public void execute(final SimInfo object) {
                //Action on button click
                Window.alert("You clicked RGOD" + object.getSimulatorConfig().getDefaultName());

            }

            @Override
            public void render(Cell.Context context, SimInfo data, SafeHtmlBuilder sb) {
                //
                SafeHtmlBuilder mySb = new SafeHtmlBuilder();

                if (ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(data.getSimulatorConfig().getActorType()) ) {
                    mySb.appendHtmlConstant("<img style=\"width: 24px; height: 24px;\"  src=\"icons2/edit-od.png\">");
                    sb.append(mySb.toSafeHtml());
                }

            }
        };

            cells.add(rgEdit);
            cells.add(rgOdEdit);
            cells.add(normalEdit);


        CompositeCell<SimInfo> compositeCell = new CompositeCell<>(cells);

        return compositeCell;
    }


    @Override
	protected void bindUI() {
		// force loading of sites in the back end
		// funny errors occur without this
		new GetAllSitesCommand() {

            @Override
            public void onComplete(Collection<Site> var1) {

            }
        }.run(getCommandContext());

        ClientUtils.INSTANCE.getEventBus().addHandler(TestSessionChangedEvent.TYPE, new TestSessionChangedEventHandler() {
            @Override
            public void onTestSessionChanged(TestSessionChangedEvent event) {
                loadSimStatus(event.getValue());
            }
        });

		((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).addTabSelectedEventHandler(new TabSelectedEvent.TabSelectedEventHandler() {
			@Override
			public void onTabSelection(TabSelectedEvent event) {
				if (event.getTabName().equals(tabName)){
					loadSimStatus();
				}
			}
		});

		loadSimStatus();
	}

	@Override
	protected void configureTabView() {

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
        new GetNewSimulatorCommand(){
            @Override
            public void onComplete(Simulator sconfigs) {
                for (SimulatorConfig config : sconfigs.getConfigs())
                    simConfigSuper.add(config);
                simConfigSuper.reloadSimulators();
                loadSimStatus(getCurrentTestSession());
                ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireSimulatorsUpdatedEvent();
            }
        }.run(new GetNewSimulatorRequest(getCommandContext(),actorTypeName,simId));
    } // createNewSimulator



    void loadActorSelectListBox() {
        new GetActorTypeNamesCommand(){
            @Override
            public void onComplete(List<String> result) {
                actorSelectListBox.clear();
                if (result == null)
                    return;
                actorSelectListBox.addItem("");
                for (String name : StringSort.sort(result))
                    actorSelectListBox.addItem(name);
            }
        }.run(getCommandContext());
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

        table.getElement().setId("simConfigTable");
        int row = 0;
        table.setHTML(row, nameColumn, "<b>Name</b>");
        table.getFlexCellFormatter().setStyleName(0,nameColumn,"lavenderTh");
        table.setHTML(row, idColumn, "<b>ID</b>");
        table.getFlexCellFormatter().setStyleName(0,idColumn,"lavenderTh");
        table.setHTML(row, typeColumn, "<b>Type</b>");
        table.getFlexCellFormatter().setStyleName(0,typeColumn,"lavenderTh");
        table.setHTML(row, pidPortColumn, "<b>Patient Feed Port</b>");
        table.getFlexCellFormatter().setStyleName(0,pidPortColumn,"lavenderTh");
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
                new GetSimulatorStatsCommand(){
                    @Override
                    public void onComplete(List<SimulatorStats> simulatorStatses) {
                        buildTable(configs, simulatorStatses);
                        popCellTable(configs, simulatorStatses);
                    }
                }.run(new GetSimulatorStatsRequest(getCommandContext(),simIds));

            }
        }.run(new GetAllSimConfigsRequest(getCommandContext(), user));
    }

    private void popCellTable(List<SimulatorConfig> configs, List<SimulatorStats> statsList) {
        // Add the data to the data provider, which automatically pushes it to the
        // widget.
        List<SimInfo> list = dataProvider.getList();

        int row = 0;
        for (SimulatorConfig config : configs) {
            SimInfo simInfo = new SimInfo(config, statsList.get(row));
            // TODO last trans
            list.add(simInfo);
            row++;
        }


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
        // addTest the variable width stats columns and keep track of max column used
        int column = addSimStats(statsColumn, configs, stats);

        // now we know width of statsColumn so we can addTest button column after it
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
            table.setHTML(0, column+colOffset, "<b>" + statType + "</b>");
            table.getFlexCellFormatter().setStyleName(0,column+colOffset,"lavenderTh");
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

        table.setHTML(0, maxColumn, "<b>Action</b>");
        table.getFlexCellFormatter().setStyleName(0,maxColumn,"lavenderTh");

        final SimId simId = config.getId();
        HorizontalPanel buttonPanel = new HorizontalPanel();
        buttonPanel.getElement().setId("scmButtonPanel");
        table.setWidget(row, maxColumn, buttonPanel);

        Image logImg = new Image("icons2/log-file-format-symbol.png");
        logImg.setTitle("View transaction logs");
        logImg.setAltText("A picture of a log book.");
        applyImgIconStyle(logImg);
        logImg.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
            @Override
            public void onClick(ClickEvent clickEvent) {
                SimulatorConfig config = getData();
                SimulatorMessageViewTab viewTab = new SimulatorMessageViewTab();
                viewTab.onTabLoad(config.getId());
            }
        });
        buttonPanel.add(logImg);

        Image pidImg = new Image("icons2/id.png");
        pidImg.setTitle("Patient ID Feed");
        pidImg.setAltText("An ID element.");
        applyImgIconStyle(pidImg);
        pidImg.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
            @Override
            public void onClick(ClickEvent clickEvent) {
                SimulatorConfig config = getData();
                PidEditTab editTab = new PidEditTab(config);
                editTab.onTabLoad(true, "PIDEdit");
            }
        });
        buttonPanel.add(pidImg);

        if (ActorType.OD_RESPONDING_GATEWAY.getShortName().equals(config.getActorType()) ) {
            Image editRgImg = new Image("icons2/edit-rg.png");
            editRgImg.setTitle("Edit RG Simulator Configuration");
            editRgImg.setAltText("A pencil writing.");
            applyImgIconStyle(editRgImg);
            editRgImg.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    loadSimStatus();
                    SimulatorConfig config = getData();

                        // Generic state-less type simulators
                        GenericQueryTab editTab = new EditTab(self, config);
                        editTab.onTabLoad(true, "SimConfig");
                }
            });
            Image editOdImg = new Image("icons2/edit-od.png");
            editOdImg.setTitle("Edit ODDS Simulator Configuration");
            editOdImg.setAltText("A pencil writing.");
            applyImgIconStyle(editOdImg);
            editOdImg.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    loadSimStatus();
                    SimulatorConfig config = getData();
                    // This simulator requires content state initialization
                    OddsEditTab editTab;
                    editTab = new OddsEditTab(self, config);
                    editTab.onTabLoad(true, "ODDS");
                }
            });
            buttonPanel.add(editRgImg);
            buttonPanel.add(editOdImg);

        } else {

            Image editImg = new Image("icons2/edit.png");
            editImg.setTitle("Edit Simulator Configuration");
            editImg.setAltText("A pencil writing.");
            applyImgIconStyle(editImg);
            editImg.addClickHandler(new ClickHandlerData<SimulatorConfig>(config) {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    loadSimStatus();
                    SimulatorConfig config = getData();

//							GenericQueryTab editTab;
                    if (ActorType.ONDEMAND_DOCUMENT_SOURCE.getShortName().equals(config.getActorType())
                            ) {
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
            buttonPanel.add(editImg);
        }

        Image deleteImg = new Image("icons2/garbage.png");
        deleteImg.setTitle("Delete");
        deleteImg.setAltText("A garbage can.");
        applyImgIconStyle(deleteImg);

        final ClickHandlerData<SimulatorConfig> clickHandlerData =  new ClickHandlerData<SimulatorConfig>(config) {
            @Override
            public void onClick(ClickEvent clickEvent) {
                SimulatorConfig config = getData();
                DeleteButtonClickHandler handler = new DeleteButtonClickHandler(self, config);
                handler.delete();
            }
        };

        deleteImg.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                SimulatorConfigElement ele = config.getConfigEle(SimulatorProperties.locked);
                boolean locked = (ele == null) ? false : ele.asBoolean();
                if (locked) {
                    if (PasswordManagement.isSignedIn) {
                        doDelete();
                    }
                    else {
                        PasswordManagement.addSignInCallback(signedInCallback);

                        new AdminPasswordDialogBox(simCtrlContainer);

                        return;
                    }
                } else {
                    doDelete();
                }

            }

            AsyncCallback<Boolean> signedInCallback = new AsyncCallback<Boolean> () {

                public void onFailure(Throwable ignored) {
                }

                public void onSuccess(Boolean ignored) {
                    doDelete();
                }

            };

            private void doDelete() {
                VerticalPanel body = new VerticalPanel();
                body.add(new HTML("<p>Delete " + config.getId().toString() + "?</p>"));
                Button actionButton = new Button("Yes");
                actionButton.addClickHandler(
                        clickHandlerData
                );
                SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
                safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/garbage.png\" height=\"16\" width=\"16\"/>");
                safeHtmlBuilder.appendHtmlConstant("Confirm Delete Simulator");
                new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionButton);
            }
        });


        buttonPanel.add(deleteImg);

        Image fileDownload = new Image("icons2/download.png");
        fileDownload.setTitle("Download Site File");
        fileDownload.setAltText("An XML document with a download arrow.");
        applyImgIconStyle(fileDownload);
        fileDownload.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open("siteconfig/"+simId.toString(), "_blank","");
            }
        });

        buttonPanel.add(fileDownload);

        // Flaticon credits
        // <div>Icons made by <a href="http://www.flaticon.com/authors/madebyoliver" title="Madebyoliver">Madebyoliver</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
        // <div>Icons made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
        // <div>Icons made by <a href="http://www.flaticon.com/authors/retinaicons" title="Retinaicons">Retinaicons</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
        // <div>Icons made by <a href="http://www.flaticon.com/authors/gregor-cresnar" title="Gregor Cresnar">Gregor Cresnar</a> from <a href="http://www.flaticon.com" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC 3.0 BY</a></div>
    }

    private void applyImgIconStyle(Image imgIcon) {
       applyImgIconStyle(imgIcon, 24);
    }
    private void applyImgIconStyle(Image imgIcon, int pixelSz) {
        imgIcon.setWidth(pixelSz+"px");
        imgIcon.setHeight(pixelSz+"px");
        imgIcon.getElement().getStyle().setVerticalAlign(Style.VerticalAlign.BOTTOM);
        imgIcon.getElement().getStyle().setCursor(Style.Cursor.POINTER);
        imgIcon.getElement().getStyle().setMargin((int)(pixelSz/4), Style.Unit.PX);
    }

    public String getWindowShortName() {
        return "simmgr";
    }
}
