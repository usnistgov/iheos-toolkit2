package gov.nist.toolkit.desktop.client;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.desktop.client.events.ResizeToolkitEvent;
import gov.nist.toolkit.desktop.client.events.ToolkitEventBus;
import gov.nist.toolkit.desktop.client.home.WelcomePlace;
import gov.nist.toolkit.desktop.client.injection.ToolkitGinInjector;
import gov.nist.toolkit.desktop.client.tools.ToolMenu;

/**
 *
 */
public class DesktopApp implements IsWidget {
    private static final ToolkitGinInjector INJECTOR = ToolkitGinInjector.INSTANCE;
    private final ToolkitEventBus eventBus = INJECTOR.getEventBus();

    // wrapper for all of toolkit
    private SimpleLayoutPanel activityPanel = new SimpleLayoutPanel();

    private SplitLayoutPanel mainSplitPanel = new SplitLayoutPanel(3);

    // Main menu (left side)
    private static final int TRAY_SIZE = 190;
    private static final int TRAY_CTL_BTN_SIZE = 9; // 23
    private final FlowPanel mainMenuPanel = new FlowPanel();

    private TabContainer tabContainer = INJECTOR.getTabContainer();

    private ToolMenu toolMenu = INJECTOR.getToolMenu();

    private ToolkitAppView appView;

    public DesktopApp() {
        GWT.log("In DesktopApp");

        appView = INJECTOR.getToolkitAppView();

        PlaceController placeController = INJECTOR.getPlaceController();

        ToolkitActivityMapper activityMapper = new ToolkitActivityMapper();
        ActivityManager activityManager = new ActivityManager(activityMapper, eventBus);
        activityManager.setDisplay(activityPanel);

        ToolkitAppPlaceHistoryMapper historyMapper = GWT.create(ToolkitAppPlaceHistoryMapper.class);
        PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(historyMapper);

        // Don't know why this is done - WelcomePlace is never displayed
        historyHandler.register(placeController, eventBus, new WelcomePlace("Welcome"));

        GWT.log("adding view to activity panel");

        // add main toolkit display to display wrapper

        mainMenuPanel.add(toolMenu);

        buildTabsWrapper();



        activityPanel.add(mainSplitPanel);
//        activityPanel.add(appView.getWidget());

        // Goes to the place represented on URL else default place
        historyHandler.handleCurrentHistory();

        GWT.log("end of app");
    }

    private void buildTabsWrapper() {
        HorizontalPanel menuPanel = new HorizontalPanel();

        Widget decoratedTray = decorateMenuContainer();

        mainSplitPanel.addWest(decoratedTray, TRAY_SIZE);
        mainSplitPanel.setWidgetToggleDisplayAllowed(decoratedTray,true);


        TabContainer.setWidth("100%");
        TabContainer.setHeight("100%");

//        menuPanel.add(environmentManager);
//        menuPanel.setSpacing(10);
//		menuPanel.add(new TestSessionSelector(testSessionManager.getTestSessions(), testSessionManager.getCurrentTestSession()).asWidget());

        DockLayoutPanel mainPanel = new DockLayoutPanel(Style.Unit.EM);

        // Menu?  Is this the tab bar?
        mainPanel.addNorth(menuPanel, 4);
		mainPanel.add(tabContainer.getTabPanel());
        mainSplitPanel.add(mainPanel);

//        mainPanel.add(new Label("Hello World"));

        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                eventBus.fireEvent(new ResizeToolkitEvent());
            }

        });
    }


    private Widget decorateMenuContainer() {
        final FlowPanel vpCollapsible =  new FlowPanel();

        // Set margins
        mainMenuPanel.getElement().getStyle().setMargin(3, Style.Unit.PX);

        // Make it Collapsible

        final HTML menuTrayStateToBe = new HTML("&laquo;");
        menuTrayStateToBe.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT); // Right alignment causes a problem where it is cut off (non-intuitively hidden over current view but still accessible by the horizontal scrollbar) by longer menu item labels that wrap in a smaller-sized browser window
        menuTrayStateToBe.getElement().getStyle().setFontSize(14, Style.Unit.PX);
        menuTrayStateToBe.setTitle("Minimize Toolkit Menu");

        // Make it rounded
        menuTrayStateToBe.setStyleName("menuCollapse");
        menuTrayStateToBe.setWidth("15px");
        menuTrayStateToBe.setWidth("100%");

        vpCollapsible.add(menuTrayStateToBe);
        vpCollapsible.add(mainMenuPanel);

        // Wrap menu in a scroll panel
        final ScrollPanel spMenu = new ScrollPanel(vpCollapsible);

        menuTrayStateToBe.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (menuTrayStateToBe.getHTML().hashCode()==171) {
                    menuTrayStateToBe.setHTML("&raquo;");
                    menuTrayStateToBe.setTitle("Expand Toolkit Menu");
                    mainMenuPanel.setVisible(false);
                    menuTrayStateToBe.setWidth("");
                    menuTrayStateToBe.setHeight(mainSplitPanel.getElement().getClientHeight()+"px");
                    mainSplitPanel.setWidgetSize(spMenu, TRAY_CTL_BTN_SIZE);
                } else {
                    menuTrayStateToBe.setHTML("&laquo;");
                    menuTrayStateToBe.setTitle("Collapse");
                    mainMenuPanel.setVisible(true);
                    menuTrayStateToBe.setWidth("100%");
                    menuTrayStateToBe.setHeight("");
                    mainSplitPanel.setWidgetSize(spMenu,TRAY_SIZE);
                }
            }
        });

        return spMenu;
    }


    @Override
    public Widget asWidget() {
        return activityPanel;
    }

}
