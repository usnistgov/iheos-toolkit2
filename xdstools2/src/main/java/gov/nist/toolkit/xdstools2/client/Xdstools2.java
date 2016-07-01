package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.*;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import com.google.web.bindery.event.shared.EventBus;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.event.tabContainer.V2TabOpenedEvent;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionManager2;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;
import gov.nist.toolkit.xdstools2.client.tabs.HomeTab;
import gov.nist.toolkit.xdstools2.client.tabs.QueryState;
import gov.nist.toolkit.xdstools2.client.tabs.TabManager;
import gov.nist.toolkit.xdstools2.client.tabs.messageValidator.MessageValidatorTab;
import gov.nist.toolkit.xdstools2.client.util.ClientFactory;

import java.util.logging.Logger;


public class Xdstools2  implements TabContainer, AcceptsOneWidget, IsWidget {
	public static final int TRAY_SIZE = 190;
	public static final int TRAY_CTL_BTN_SIZE = 9; // 23

    private static Xdstools2 ME = null;
    private static final ClientFactory clientFactory = GWT.create(ClientFactory.class);
	private final static Logger logger=Logger.getLogger(Xdstools2.class.getName());

	public SplitLayoutPanel mainSplitPanel = new SplitLayoutPanel(3);
	private VerticalPanel mainMenuPanel = new VerticalPanel();
	HomeTab ht = null;

	private static TabPanel tabPanel = new TabPanel();
	private HorizontalPanel uiDebugPanel = new HorizontalPanel();
	boolean UIDebug = false;

	private static TkProps props = new TkProps();

    public Xdstools2() {
		ME = this;
	}

    static public Xdstools2 getInstance() {
        if (ME==null){
            ME=new Xdstools2();
        }
		return ME;
	}

	// This bus is used for v2 v3 integration that signals v2 launch tab event inside the v3 environment
	EventBus v2V3IntegrationEventBus = null;

	// This is as toolkit wide singleton.  See class for details.
	TestSessionManager2 testSessionManager = new TestSessionManager2();
	static public TestSessionManager2 getTestSessionManager() { return ME.testSessionManager; }

	EnvironmentState environmentState = new EnvironmentState();
	@Override
    public EnvironmentState getEnvironmentState() { return environmentState; }

	// Central storage for parameters shared across all
	// query type tabs
	QueryState queryState = new QueryState();
	public QueryState getQueryState() {
		return queryState;
	}

	void buildWrapper() {
		// tabPanel = new TabPanel();
//		if ("xdstools2".equals(GWT.getModuleName())) { // This RootPanel is exclusive to v2. In other words, the intention of this block is to hide this from V3 module.

			Widget decoratedTray = decorateMenuContainer();

			mainSplitPanel.addWest(decoratedTray, TRAY_SIZE);
			mainSplitPanel.setWidgetToggleDisplayAllowed(decoratedTray,true);


			tabPanel.setWidth("100%");
			tabPanel.setHeight("100%");

			// Wrap tab panel in a scroll panel
			ScrollPanel spTabPanel = new ScrollPanel(tabPanel);
			spTabPanel.setAlwaysShowScrollBars(false);
			mainSplitPanel.add(spTabPanel);

			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					resizeToolkit();
				}

			});
		}

//	}

	static public void addtoMainMenu(Widget w) { ME.mainMenuPanel.add(w); }

	private Widget decorateMenuContainer() {
		final VerticalPanel vpCollapsible =  new VerticalPanel();

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

		vpCollapsible.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		vpCollapsible.add(menuTrayStateToBe);
		vpCollapsible.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
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

	public void resizeToolkit() {
        try {
			if (mainSplitPanel.getParent()!=null) {
				long containerWidth = mainSplitPanel.getParent().getElement().getClientWidth();
				long containerHeight = mainSplitPanel.getParent().getElement().getClientHeight() - 42; /* compensate for tab bar height, which is a fixed size */

				int selectedTabIndex = tabPanel.getTabBar().getSelectedTab();

				if (selectedTabIndex >= 0) {
					tabPanel.getWidget(selectedTabIndex).setHeight(containerHeight + "px");
				}
			}else{
				logger.fine("mainSplitPanel parent is null");
			}
        }catch (Throwable t) {
			logger.warning("Window resize failed:" + t.toString());
		}
	}

    public TabContainer getTabContainer() { return this;}

	public void addTab(VerticalPanel w, String title, boolean select) {
		HTML left = new HTML();
		left.setHTML("&nbsp");

		HTML right = new HTML();
		right.setHTML("&nbsp");

		HorizontalPanel wrapper = new HorizontalPanel();

		wrapper.add(left);
		wrapper.add(w);
		wrapper.add(right);
		wrapper.setCellWidth(left, "1%");
		wrapper.setCellWidth(right, "1%");

		tabPanel.add(wrapper, buildTabWidget(title, w));


		if (select)
			selectLastTab();
		resizeToolkit();

		try {
			int index = tabPanel.getWidgetCount() - 1;
			if (getIntegrationEventBus()!=null && index>0) {
				getIntegrationEventBus().fireEvent(new V2TabOpenedEvent(null,title /* this will be the dynamic tab code */,index));
			}
		} catch (Throwable t) {
			Window.alert("V2TabOpenedEvent error: " +t.toString());
		}
	}

	static boolean newHomeTab = false;

	private void selectLastTab() {
		int index = tabPanel.getWidgetCount() - 1;
		if (index > -1)
			tabPanel.selectTab(index);
	}

	Widget buildTabWidget(String title, final Widget content) {
		HorizontalPanel panel = new HorizontalPanel();
		Anchor x = new Anchor("X");
		x.setStyleName("roundedButton1");
		x.addClickHandler(new ClickHandler() {
							  @Override
							  public void onClick(ClickEvent clickEvent) {
								  content.getParent().removeFromParent();
								  selectLastTab();
							  }
						  }
		);
		panel.add(x);
		panel.add(new HTML(title));
		return panel;
	}

	@Deprecated
    static public TkProps tkProps() {
        return props;
    }

	/**
	 * This is the old entry point method.
	 * It's now being used as an initialization method the GUI and the environment
	 */
	void run() {
		ht = new HomeTab();
		onModuleLoad2();

		testSessionManager.load();
		loadServletContext();
	}

	public String toolkitName;

	private void loadServletContext() {
		ht.toolkitService.getServletContextName(new AsyncCallback<String>() {
			@Override
			public void onFailure(Throwable throwable) {
				new PopupMessage("Failed to load servletContextName - " + throwable.getMessage());
			}

			@Override
			public void onSuccess(String s) {
				toolkitName = s;
			}
		});
    }

	private void onModuleLoad2() {
		buildWrapper();

		ht.onTabLoad(this, false, null);

		new TabManager().reset();

		// only one panel, it's all done in tabs
		tabPanel.selectTab(0);

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				System.out.println("Tab " + event.getSelectedItem() + " selected");
				new TabManager().notifyTabSelected(event.getSelectedItem());
			}

		});

		History.addValueChangeHandler(new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
                String historyToken = event.getValue();

                // Parse the history token
                try {
                    if (historyToken.equals("mv")) {
                        new MessageValidatorTab().onTabLoad(getTabContainer(), true, null);
                    }

                } catch (IndexOutOfBoundsException e) {
                    tabPanel.selectTab(0);
                }
            }
        });


	}


	public TabPanel getTabPanel() {
		return tabPanel;
	}


	static public EventBus getEventBus() {
		return clientFactory.getEventBus();
	}

	// To force an error when I merge with Sunil. We need
	// to reconcile the initialization.
	public void setIntegrationEventBus(EventBus eventBus) {
		v2V3IntegrationEventBus = eventBus;
	}
	public EventBus getIntegrationEventBus() {
		return v2V3IntegrationEventBus;
	}



    @Override
    public void setWidget(IsWidget isWidget) {
        mainSplitPanel.add(isWidget.asWidget());
    }

    @Override
    public Widget asWidget() {
        return mainSplitPanel;
    }

}
