package gov.nist.toolkit.xdstools2.client;

import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.tabs.EnvironmentState;
import gov.nist.toolkit.xdstools2.client.tabs.HomeTab;
import gov.nist.toolkit.xdstools2.client.tabs.QueryState;
import gov.nist.toolkit.xdstools2.client.tabs.TabManager;
import gov.nist.toolkit.xdstools2.client.tabs.TestSessionState;
import gov.nist.toolkit.xdstools2.client.tabs.messageValidator.MessageValidatorTab;
import gov.nist.toolkit.xdstools2.client.tabs.testRunnerTab.TestRunnerTab;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Xdstools2 implements EntryPoint, TabContainer {


	TabPanel tabPanel;

	TabContainer getTabContainer() { return this;}

	static TkProps props = new TkProps();
	static public boolean showEnvironment = true;

	// Central storage for parameters shared across all
	// query type tabs
	QueryState queryState = new QueryState();
	public QueryState getQueryState() { 
		return queryState; 
	}

	EnvironmentState environmentState = new EnvironmentState();
	@Override public EnvironmentState getEnvironmentState() { return environmentState; }

	TestSessionState testSessionState = new TestSessionState();
	@Override public TestSessionState getTestSessionState() { return testSessionState; }

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
		tabPanel = new TabPanel();
		RootPanel.get().insert(tabPanel, 0);
//		RootPanel.get().add(tabPanel);


		tabPanel.setWidth("100%");

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

		if (select)
			tabPanel.selectTab(tabPanel.getWidgetCount() - 1);
	}

	HomeTab ht = null;

	/**
	 * This is the entry point method.
	 */
	@SuppressWarnings("deprecation")
	public void onModuleLoad() {
		loadTkProps();
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

	private void onModuleLoad2() {

//		if (tkProps() == null) {
//			
//		}
//		else if ("direct".equals(tkProps().get("toolkit.home", null)))
//			showEnvironment = false;

		buildWrapper();

		ht.onTabLoad(this, false, null);

		//		new MessageValidatorTab().onTabLoad(this, false);


		new TabManager().reset();

		// only one panel, it's all done in tabs
		tabPanel.selectTab(0);

		tabPanel.addSelectionHandler(new SelectionHandler<Integer>(){

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
					else if (historyToken.equals("conf")) {
						new TestRunnerTab().onTabLoad(getTabContainer(), true);
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




}
