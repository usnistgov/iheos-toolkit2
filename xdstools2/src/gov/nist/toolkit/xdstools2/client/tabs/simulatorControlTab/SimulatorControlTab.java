package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.Collection;
import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SimulatorControlTab extends GenericQueryTab {

	public SimulatorControlTab(BaseSiteActorManager siteActorManager) {
		super(siteActorManager);
	}

	public SimulatorControlTab() {
		super(new FindDocumentsSiteActorManager());	}

	protected TabContainer myContainer;
	ListBox         actorSelectListBox = new ListBox();
	HorizontalPanel simConfigWrapperPanel = new HorizontalPanel();
	VerticalPanel   simConfigPanel = new VerticalPanel();
	TextArea        simIdsTextArea = new TextArea();
	Button          createActorSimulatorButton = new Button("Create Actor Simulator");
	Button          loadSimulatorsButton = new Button("Load Simulators");

//	final protected ToolkitServiceAsync toolkitService = GWT
//	.create(ToolkitService.class);

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();

		container.addTab(topPanel, "Sim Control", select);
		addCloseButton(container,topPanel, null);
		
		addActorReloader();
		
		runEnabled = false;
		samlEnabled = false;
		tlsEnabled = false;
		enableInspectResults = false;

		HTML title = new HTML();
		title.setHTML("<h2>Simulator Control</h2>");
		topPanel.add(title);

		HorizontalPanel actorSelectPanel = new HorizontalPanel();
		actorSelectPanel.add(HtmlMarkup.html("Select actor type"));
		actorSelectPanel.add(actorSelectListBox);
		loadActorSelectListBox();
		
		actorSelectPanel.add(createActorSimulatorButton);
		createActorSimulatorButton.addClickHandler(new CreateButtonClickHandler(this));
		
		topPanel.add(actorSelectPanel);
		
		HorizontalPanel simIdsPanel = new HorizontalPanel();
		
		simIdsTextArea.setSize("600px", "50px");
		
		loadSimulatorsFromCookies();

		simIdsTextArea.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				updateSimulatorCookies();
			}
			
		});
		
		
		simIdsPanel.add(simIdsTextArea);
		
		loadSimulatorsButton.addClickHandler(new LoadSimulatorsClickHandler(this));
		simIdsPanel.add(loadSimulatorsButton);
		
		topPanel.add(simIdsPanel);
		
		topPanel.add(HtmlMarkup.html("<br />"));
		
		topPanel.add(simConfigWrapperPanel);
		
		
		simConfigWrapperPanel.add(simConfigPanel);

		// force loading of sites in the back end
		// funny errors occur without this
		toolkitService.getAllSites(new AsyncCallback<Collection<Site>>() {

			public void onFailure(Throwable caught) {
			}

			public void onSuccess(Collection<Site> result) {
			}
			
		});


	}
	
	static final String SIMULATORCOOKIENAME = "gov.nist.registry.xdstools2.XDSSimulatorsCookie";
	
	void loadSimulatorsFromCookies() {
		String cookieString = Cookies.getCookie(SIMULATORCOOKIENAME);
		
		if (cookieString == null || cookieString.equals(""))
			return;
		
		simIdsTextArea.setText(cookieString);
		new LoadSimulatorsClickHandler(this).loadSimulators();
	}
	
	void updateSimulatorCookies() {
		updateSimulatorCookies(simIdsTextArea.getText().trim());
	}
	
	void updateSimulatorCookies(String value) {
		if (value == null) {
			if (Cookies.getCookie(SIMULATORCOOKIENAME) != null)
				Cookies.setCookie(SIMULATORCOOKIENAME, value);
		} else {
			if (!value.equals(Cookies.getCookie(SIMULATORCOOKIENAME)))
				Cookies.setCookie(SIMULATORCOOKIENAME, value);
		}
	}
	
	
	
	SimConfigSuper simConfigSuper = new SimConfigSuper(this, simConfigPanel);
	
	void getNewSimulator(String actorTypeName) {
		toolkitService.getNewSimulator(actorTypeName, new AsyncCallback<Simulator>() {

			public void onFailure(Throwable caught) {
				new PopupMessage("Error creating new simulator: " + caught.getMessage());
			}

			public void onSuccess(Simulator sconfigs) {
				for (SimulatorConfig config : sconfigs.getConfigs())
					simConfigSuper.add(config);
				simConfigSuper.reloadSimulators();
			}
			
		});
		
	}
	
	
	
	void loadActorSelectListBox() {
		toolkitService.getActorTypeNames(new AsyncCallback<List<String>>() {

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
	
	

	public String getWindowShortName() {
		return "simcontrol";
	}
}
