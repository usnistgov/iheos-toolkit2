package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
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
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.SimulatorStats;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.StringSort;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.event.TabSelectedEvent;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.GetAllSimConfigsRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetNewSimulatorRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSimulatorStatsRequest;

import java.util.ArrayList;
import java.util.Collection;
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
    private SimManagerWidget2 simManagerWidget;


    public SimulatorControlTab(BaseSiteActorManager siteActorManager) {
        super(siteActorManager);
    }

    public SimulatorControlTab() {  super(new FindDocumentsSiteActorManager());	}


    @Override
	protected Widget buildUI() {
        simCtrlContainer = new FlowPanel();
        self = this;

        ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).addTabSelectedEventHandler(new TabSelectedEvent.TabSelectedEventHandler() {
            @Override
            public void onTabSelection(TabSelectedEvent event) {
                if (event.getTabName().equals(tabName)){
                    loadSimStatus();
                }
            }
        });

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
        actorSelectListBox.removeStyleName("selectActorTypeMc");
        actorSelectListBox.addStyleName("selectActorTypeMc");
        actorSelectPanel.add(actorSelectListBox);
        loadActorSelectListBox();

        newSimIdTextBox.removeStyleName("simulatorIdInputMc");
        newSimIdTextBox.addStyleName("simulatorIdInputMc");
        final CreateButtonClickHandler createButtonClickHandler = new CreateButtonClickHandler(this, testSessionManager);
        newSimIdTextBox.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent keyUpEvent) {
                if (keyUpEvent!=null && keyUpEvent.getNativeKeyCode() == KeyCodes.KEY_ENTER)
                    createButtonClickHandler.doClick();
            }
        });

        actorSelectPanel.add(HtmlMarkup.html("Simulator ID"));
        actorSelectPanel.add(newSimIdTextBox);

        actorSelectPanel.add(createActorSimulatorButton);
        createActorSimulatorButton.addClickHandler(createButtonClickHandler);

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


        simManagerWidget = new SimManagerWidget2(getCommandContext(), this);

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {

                resizeSimMgrWidget(simConfigWrapperPanel, simManagerWidget);

            }
        });
        simCtrlContainer.add(simManagerWidget);

        resizeSimMgrWidget(simConfigWrapperPanel, simManagerWidget);

        return simCtrlContainer;
	}


	protected void resizeSimMgrWidget(HorizontalPanel container, SimManagerWidget2 widget2) {

        int containerWidth;

        try {
            containerWidth = (int)(container.getParent().getElement().getClientWidth() * .80); // Window.getClientWidth())
        } catch (Exception ex) {
           containerWidth = (int)(Window.getClientWidth() * .80);
        }

//        int containerHeight;
//        try {
//            containerHeight = (int)(container.getParent().getElement().getClientHeight() * .50); // Window.getClientHeight()
//        } catch (Exception ex) {
//            containerHeight = (int)(Window.getClientHeight() * .5);
//        }

//        Window.alert(containerWidth + " height: " + containerHeight);

        widget2.resizeTable(containerWidth);

//        widget2.setHeightInPx(containerHeight);

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



    void loadSimStatus() {
        loadSimStatus(getCurrentTestSession());
    }

    void loadSimStatus(final String user)  {
        new GetAllSimConfigsCommand() {

            @Override
            public void onComplete(List<SimulatorConfig> var1) {
                final List<SimulatorConfig> configs = var1;
                final List<SimId> simIds = new ArrayList<>();
                for (SimulatorConfig config : configs)
                    simIds.add(config.getId());
                try {
                    new GetSimulatorStatsCommand() {
                        @Override
                        public void onComplete(final List<SimulatorStats> simulatorStatses) {
//                        buildTable(configs, simulatorStatses);
//                            Window.alert("Calling widget " + user);
                            simManagerWidget.popCellTable(user, configs, simulatorStatses);
                            resizeSimMgrWidget(simConfigWrapperPanel, simManagerWidget);

                        }
                    }.run(new GetSimulatorStatsRequest(getCommandContext(), simIds));

                }catch (Exception ex) {}
            }
        }.run(new GetAllSimConfigsRequest(getCommandContext(), user));
    }



    private SimulatorStats findSimulatorStats(List<SimulatorStats> statss, SimId simId) {
        for (SimulatorStats ss : statss) {
            if (ss.simId.equals(simId)) return ss;
        }
        return null;
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
