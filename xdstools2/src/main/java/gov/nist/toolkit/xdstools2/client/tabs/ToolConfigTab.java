package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.xdstools2.client.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.LoadGazelleConfigsClickHandler;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ToolConfigTab extends GenericQueryTab {
	
	FlexTable grid = new FlexTable();
	Map<String, String> props;
	Button rmOldSims = new Button("Delete old simulators");
	Button loadAllGazelleConfigs = new Button("Load all Gazelle Configs");
	int gridRow;

	public ToolConfigTab() {
		super(new NullSiteActorManager());
	}

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();


		container.addTab(topPanel, "ToolConfig", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Configure XDS Toolkit</h2>");
		topPanel.add(title);
		
		topPanel.add(addHTML("<h3>Properties</h3>"));
		
		buildGrid();
		
		
	}
	
	class RmOldSimsClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			toolkitService.removeOldSimulators(new AsyncCallback<Integer> () {

				public void onFailure(Throwable caught) {
					new PopupMessage("removeOldSimulators() call failed: " + caught.getMessage());
				}

				public void onSuccess(Integer result) {
					new PopupMessage(result + " simulators removed");
				}
				
			});
		}
		
	}


	// Boolean data type ignored 
	AsyncCallback<Boolean> signedInCallback = new AsyncCallback<Boolean> () {

		public void onFailure(Throwable ignored) {
		}

		public void onSuccess(Boolean ignored) {
			buildGrid();
		}

	};


	
	void buildGrid() {
		
		if (PasswordManagement.isSignedIn) {
		}
		else {
			PasswordManagement.addSignInCallback(signedInCallback);
			
			new AdminPasswordDialogBox(topPanel);
			
			return;
			
		}

		
		loadPropertyFile();

		
		Button goButton = new Button("Save");
		goButton.addClickHandler(new Saver());
		
		topPanel.add(grid);
		
		topPanel.add(goButton);	
		
		HTML separator = new HTML();
		separator.setHTML("<hl />");
		topPanel.add(separator);
		
		topPanel.add(rmOldSims);
		rmOldSims.addClickHandler(new RmOldSimsClickHandler());
		
		topPanel.add(loadAllGazelleConfigs);
		loadAllGazelleConfigs.addClickHandler(new LoadGazelleConfigsClickHandler(toolkitService, myContainer, "ALL"));
		

	}
	
	class Saver implements ClickHandler {

		public void onClick(ClickEvent event) {
			props.clear();
			for (int row=0; row<grid.getRowCount(); row++) {
				String name = grid.getText(row, 0);
				Object o = grid.getWidget(row, 1);
				String value;
				if (o instanceof TextBox)
					value = ((TextBox)o).getText();
				else {
					ListBox lb = (ListBox)o;
					int i = lb.getSelectedIndex();
					value = lb.getItemText(i);
				}
//				TextBox tb = (TextBox) grid.getWidget(row, 1);
//				String value = tb.getText();
				props.put(name, value);
			}
			savePropertyFile();
		}
		
	}
	
	void savePropertyFile() {
		toolkitService.setToolkitProperties(props, savePropertiesCallback);
	}
	
	AsyncCallback<String> savePropertiesCallback = new AsyncCallback<String> () {

		public void onFailure(Throwable caught) {
			new PopupMessage("setToolkitProperties() call failed: " + caught.getMessage());
		}

		public void onSuccess(String result) {
			new PopupMessage("Properties saved");
			Xdstools2.getInstance().loadTkProps();  // this may now be accessible if it wasn't before
		}
		
	};
	
	void loadPropertyFile() {
		toolkitService.getToolkitProperties(getToolkitPropertiesCallback);
	}
	
	AsyncCallback<Map<String, String>> getToolkitPropertiesCallback = new AsyncCallback<Map<String, String>> () {

		public void onFailure(Throwable caught) {
			new PopupMessage("getPropertyFile() call failed: " + caught.getMessage());
		}

		public void onSuccess(Map<String, String> result) {
			props = result;
			loadPropertyGrid();
		}
		
	};
	
	
	int getIndex(ListBox lb, String value) {
		for (int i=0; i<lb.getItemCount(); i++) {
			String lbVal = lb.getItemText(i);
			if (value.equals(lbVal)) 
				return i;
		}
		return -1;
	}
	
	void addToPropertyGrid(String key, Widget w) {
		grid.setText(gridRow, 0, key);
		grid.setWidget(gridRow, 1, w);
		gridRow++;
	}
	
	void loadPropertyGrid() {
		grid.clear();
		gridRow = 0;
		for (String key : props.keySet()) {
			String value = props.get(key);
			grid.setText(gridRow, 0, key);
			TextBox tb = new TextBox();
			tb.setWidth("600px");
			tb.setText(value);
			grid.setWidget(gridRow, 1, tb);
			gridRow++;
		}
	}
	

	public String getWindowShortName() {
		return "toolconfig";
	}

}
