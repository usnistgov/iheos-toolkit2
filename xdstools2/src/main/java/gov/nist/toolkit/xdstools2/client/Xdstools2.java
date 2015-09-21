package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import gov.nist.toolkit.xdstools2.client.tabs.*;
import gov.nist.toolkit.xdstools2.client.tabs.messageValidator.MessageValidatorTab;


public class Xdstools2 implements EntryPoint, TabContainer {

	HorizontalPanel tabPanelWrapper = new HorizontalPanel();
	VerticalPanel mainMenuPanel = new VerticalPanel();
	static TabPanel tabPanel = new TabPanel();

	boolean UIDebug = true;
	HorizontalPanel uiDebugPanel = new HorizontalPanel();

	TabContainer getTabContainer() { return this;}

	static TkProps props = new TkProps();

	// This is probably a conflic with Sunil's code and we
	// will have to reconcile the initialization.  This is done
	// here because the initialization of testSessionManager,
	// immediately following depends on it.
	static EventBus eventBus = new SimpleEventBus();

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
		if ("xdstools2".equals(GWT.getModuleName())) { // Hide this from V3 module
			HorizontalPanel mainMenuWrapper = new HorizontalPanel();
			mainMenuWrapper.setBorderWidth(1);
			mainMenuWrapper.add(mainMenuPanel);
			tabPanelWrapper.add(mainMenuWrapper);
			tabPanelWrapper.add(tabPanel);
			RootPanel.get().insert(tabPanelWrapper, 0);
		}

		tabPanel.setWidth("100%");
		tabPanel.setHeight("100%");
		tabPanelWrapper.setWidth("100%");
		tabPanelWrapper.setHeight("100%");
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

		try {
			if (getEventBus()!=null && index>0) {
				getEventBus().fireEvent(new V2TabOpenedEvent(null,title /* this will be the dynamic tab code */,index));
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
	private void setEventBus(EventBus eventBus) {
		this.eventBus = eventBus;
	}



}
