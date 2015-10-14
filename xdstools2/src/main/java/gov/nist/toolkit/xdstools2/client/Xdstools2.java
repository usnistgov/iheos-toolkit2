package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.SimpleEventBus;
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


public class Xdstools2 implements EntryPoint, TabContainer {

	public static final int TRAY_SIZE = 190;
	public static final int TRAY_CTL_BTN_SIZE = 23;
	private SplitLayoutPanel mainSplitPanel = new SplitLayoutPanel(3);
//	HorizontalPanel tabPanelWrapper = new HorizontalPanel();

	VerticalPanel mainMenuPanel = new VerticalPanel();
	static TabPanel tabPanel = new TabPanel();

	boolean UIDebug = false;
	HorizontalPanel uiDebugPanel = new HorizontalPanel();

	TabContainer getTabContainer() { return this;}

	static TkProps props = new TkProps();

	static EventBus eventBus = new SimpleEventBus();
	// This bus is used for v2 v3 integration that signals v2 launch tab event inside the v3 environment
	EventBus v2V3IntegrationEventBus = null;

	// This is as toolkit wide singleton.  See class for details.
	TestSessionManager2 testSessionManager = new TestSessionManager2();
	static public TestSessionManager2 getTestSessionManager() { return ME.testSessionManager; }

	static public void addtoMainMenu(Widget w) { ME.mainMenuPanel.add(w); }

	// Central storage for parameters shared across all
	// query type tabs
	QueryState queryState = new QueryState();
	public QueryState getQueryState() {
		return queryState;
	}

	EnvironmentState environmentState = new EnvironmentState();
	@Override public EnvironmentState getEnvironmentState() { return environmentState; }

	static public TkProps tkProps() {
		return props;
	}

	static Xdstools2 ME = null;

	static public Xdstools2 getInstance() {
		return ME;
	}

	public Xdstools2() {
		ME = this;
	}

	void buildWrapper() {
		// tabPanel = new TabPanel();
		if ("xdstools2".equals(GWT.getModuleName())) { // This RootPanel is exclusive to v2. In other words, the intention of this block is to hide this from V3 module.


			Widget decoratedTray = decorateMenuContainer();

			mainSplitPanel.addWest(decoratedTray, TRAY_SIZE);
			mainSplitPanel.setWidgetToggleDisplayAllowed(decoratedTray,true);


			tabPanel.setWidth("100%");
			tabPanel.setHeight("100%");

			// Wrap tab panel in a scroll panel
			ScrollPanel spTabPanel = new ScrollPanel(tabPanel);
			spTabPanel.setAlwaysShowScrollBars(false);
			mainSplitPanel.add(spTabPanel);

//			RootPanel.get().insert(mainSplitPanel, 0);

			RootLayoutPanel.get().add(mainSplitPanel);

			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					resizeToolkit();
				}
			});
		}

	}

	private Widget decorateMenuContainer() {

		final VerticalPanel vpCollapsible =  new VerticalPanel();

		// Set margins
		mainMenuPanel.getElement().getStyle().setMargin(3, Style.Unit.PX);



		// Make it Collapsible

		final HTML menuTrayStateToBe = new HTML("&laquo;");
		menuTrayStateToBe.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		menuTrayStateToBe.getElement().getStyle().setFontSize(14, Style.Unit.PX);
		menuTrayStateToBe.setTitle("Collapse");

		// Make it rounded
//		menuTrayStateToBe.setStyleName("roundedButton1");
		menuTrayStateToBe.setStyleName("simplePointer");
		menuTrayStateToBe.setWidth("15px");

//		vpCollapsible.setBorderWidth(1);
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
					menuTrayStateToBe.setTitle("Expand");
					mainMenuPanel.setVisible(false);
					mainSplitPanel.setWidgetSize(spMenu, TRAY_CTL_BTN_SIZE);
				} else {
					menuTrayStateToBe.setHTML("&laquo;");
					menuTrayStateToBe.setTitle("Collapse");
					mainMenuPanel.setVisible(true);
					mainSplitPanel.setWidgetSize(spMenu,TRAY_SIZE);
				}
			}
		});



		return spMenu;
	}



	private void resizeToolkit() {
		long containerWidth =  mainSplitPanel.getParent().getElement().getClientWidth();
		long containerHeight = mainSplitPanel.getParent().getElement().getClientHeight() - 42; /* compensate for tab bar height, which is a fixed size */


		int selectedTabIndex  = tabPanel.getTabBar().getSelectedTab();

		if (selectedTabIndex>=0) {
//			tabPanel.setWidth("100%");
//			tabPanel.getWidget(selectedTabIndex).setWidth("100%");
			tabPanel.getWidget(selectedTabIndex).setHeight(containerHeight + "px");
		}

	}

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


		tabPanel.add(wrapper, title);

		int index = tabPanel.getWidgetCount() - 1;

		if (select)
			tabPanel.selectTab(index);
		resizeToolkit();


		try {
			if (getIntegrationEventBus()!=null && index>0) {
				getIntegrationEventBus().fireEvent(new V2TabOpenedEvent(null,title /* this will be the dynamic tab code */,index));
			}

		} catch (Throwable t) {
			Window.alert("V2TabOpenedEvent error: " +t.toString());
		}
	}

	HomeTab ht = null;

	/**
	 * This is the entry point method.
	 */
	@SuppressWarnings("deprecation")
	public void onModuleLoad() {
		loadTkProps();
		testSessionManager.load();
	}

	static boolean newHomeTab = false;

	public void loadTkProps() {
		if (ht == null) {
			ht = new HomeTab();
			newHomeTab = true;
		} else {
			newHomeTab = false;
		}

		ht.toolkitService.getTkProps(new AsyncCallback<TkProps>() {

			@Override
			public void onFailure(Throwable arg0) {
				new PopupMessage("Load of TkProps failed");
				if (newHomeTab)
					onModuleLoad2(); // continue so admin can fix the config
			}

			@Override
			public void onSuccess(TkProps arg0) {
				props = arg0;
//				if (props.isEmpty())
//					new PopupMessage("Load of TkProps failed");
				if (newHomeTab) {
					onModuleLoad2();
					ht.toolkitService.getDefaultEnvironment(new AsyncCallback<String>() {
						@Override
						public void onFailure(Throwable throwable) {

						}

						@Override
						public void onSuccess(String s) {
							ht.toolkitService.setEnvironment(s, new AsyncCallback() {
								@Override
								public void onFailure(Throwable throwable) {

								}

								@Override
								public void onSuccess(Object o) {

								}
							});
						}
					});

				}
			}

		});
//		if (newHomeTab)
//			onModuleLoad2();

	}

	final ListBox debugMessages = new ListBox();
	static public void DEBUG(String msg) {
		ME.debugMessages.addItem(msg);
		ME.debugMessages.setSelectedIndex(ME.debugMessages.getItemCount()-1);
	}

	private void onModuleLoad2() {
		buildWrapper();

		if (UIDebug) {
			RootPanel.get().insert(uiDebugPanel, 0);
			uiDebugPanel.add(new HTML("<b>DEBUG</b>"));
			debugMessages.setVisibleItemCount(7);
			debugMessages.setWidth("1000px");
			uiDebugPanel.add(debugMessages);
			Button debugClearButton = new Button("Clear", new ClickHandler() {
				@Override
				public void onClick(ClickEvent clickEvent) {
					debugMessages.clear();
				}
			});
			uiDebugPanel.add(debugClearButton);
		}

		ht.onTabLoad(this, false, null);

		//		new MessageValidatorTab().onTabLoad(this, false);


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
		/*

	        public void onSelection(SelectionEvent<Integer> event) {
//	          History.newItem("page" + event.getSelectedItem());
	        // this was to do startup via the history mechanism
	        }});

		 */

		History.addValueChangeHandler(new ValueChangeHandler<String>() {
			public void onValueChange(ValueChangeEvent<String> event) {
				String historyToken = event.getValue();

				// Parse the history token
				try {
					if (historyToken.equals("mv")) {
						new MessageValidatorTab().onTabLoad(getTabContainer(), true, null);
						//	            	tabPanel.selectTab(0);
						//	              String tabIndexToken = historyToken.substring(4, 5);
						//	              int tabIndex = Integer.parseInt(tabIndexToken);
						//	              // Select the specified tab panel
						//	              tabPanel.selectTab(tabIndex);
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
		return eventBus;
	}

	// To force an error when I merge with Sunil. We need
	// to reconcile the initialization.
	public void setIntegrationEventBus(EventBus eventBus) {
		v2V3IntegrationEventBus = eventBus;
	}
	public EventBus getIntegrationEventBus() {
		return v2V3IntegrationEventBus;
	}



}
