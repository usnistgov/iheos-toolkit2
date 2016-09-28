package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.LoadGazelleConfigsClickHandler;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.TestkitConfigTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolConfigTab extends GenericQueryTab {
    // Special handling for these properties
    private static List<String> specialProperties = new ArrayList<>();
    static {
        specialProperties.add("External_Cache");
        specialProperties.add("Toolkit_Host");
        specialProperties.add("Toolkit_Port");
        specialProperties.add("Toolkit_TLS_Port");
    }
	
	private FlexTable grid = new FlexTable();
    private Map<String, String> props;
    private Button loadAllGazelleConfigsBtn = new Button("Load all Gazelle configs");
    private int gridRow;

	public ToolConfigTab() {
		super(new NullSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		FlowPanel container=new FlowPanel();
		HTML title = new HTML();
		title.setHTML("<h2>Configure XDS Toolkit</h2>");
		container.add(title);

		if (PasswordManagement.isSignedIn) {
		}
		else {
			PasswordManagement.addSignInCallback(signedInCallback);

			new AdminPasswordDialogBox(container);

			return null;
		}

		HTML subtitle1 = new HTML();
		subtitle1.setHTML("<h3>Properties</h3>");
		container.add(subtitle1);

		container.add(grid);

		Button goButton = new Button("Save");
		goButton.addClickHandler(new Saver());
		container.add(goButton);

		HTML separator = new HTML();
		separator.setHTML("<br/>");
		container.add(separator);

		HTML subtitle2 = new HTML();
		subtitle2.setHTML("<br/><br/>");
		container.add(subtitle2);

		container.add(loadAllGazelleConfigsBtn);
		loadAllGazelleConfigsBtn.addClickHandler(new LoadGazelleConfigsClickHandler(getTabContainer(), "ALL"));

		container.add(new HTML("<hr />"));
		/* new code for testkit update */
		TestkitConfigTool tkconf=new TestkitConfigTool(getTabContainer());
		container.add(tkconf);

		return container;
	}

	@Override
	protected void bindUI() {
		loadPropertyFile();
	}

	@Override
	protected void configureTabView() {
        // No configured elements in that tab
	}

    @Override
    public String getWindowShortName() {
        return "toolconfig";
    }

    void addPropertyToGrid(String key) {
        // create the label for each row
        String formattedKey = key.trim().replace('_', ' ');
        grid.setText(gridRow, 0, formattedKey);

        // create the boxed value for each row
        TextBox tb = new TextBox();
        tb.setWidth("600px");
        String value = props.get(key);
        tb.setText(value);
        grid.setWidget(gridRow, 1, tb);

        gridRow++;
    }

    /**
     * Build the grid of toolkit properties for display. The property names (keys) will be correctly formatted for
     * display here as long as underscores are used in the toolkit properties file.
     */
    void loadPropertyGrid() {
        grid.clear();
        gridRow = 0;
		for (String key : specialProperties) {
			addPropertyToGrid(key);
		}
        for (String key : props.keySet()) {
			if (specialProperties.contains(key)) continue;
			addPropertyToGrid(key);
        }
    }

    void loadPropertyFile() {
        getToolkitServices().getToolkitProperties(getToolkitPropertiesCallback);
    }

    void savePropertyFile() {
        getToolkitServices().setToolkitProperties(props, savePropertiesCallback);
    }

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


    // Boolean data type ignored
    AsyncCallback<Boolean> signedInCallback = new AsyncCallback<Boolean> () {

        public void onFailure(Throwable ignored) {
        }

        public void onSuccess(Boolean ignored) {
            buildView();
        }

    };


    AsyncCallback<String> savePropertiesCallback = new AsyncCallback<String> () {

        public void onFailure(Throwable caught) {
            new PopupMessage("setToolkitProperties() call failed: " + caught.getMessage());
        }

        public void onSuccess(String result) {
            new PopupMessage("Properties saved");
        }

    };

    AsyncCallback<Map<String, String>> getToolkitPropertiesCallback = new AsyncCallback<Map<String, String>> () {

        public void onFailure(Throwable caught) {
            new PopupMessage("getPropertyFile() call failed: " + caught.getMessage());
        }

        public void onSuccess(Map<String, String> result) {
            props = result;
            loadPropertyGrid();
        }

    };

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
                name = name.replace(' ', '_');
                props.put(name, value);
            }
            savePropertyFile();
        }

    }

	class RmOldSimsClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			getToolkitServices().removeOldSimulators(new AsyncCallback<Integer> () {

				public void onFailure(Throwable caught) {
					new PopupMessage("removeOldSimulators() call failed: " + caught.getMessage());
				}

				public void onSuccess(Integer result) {
					new PopupMessage(result + " simulators removed");
				}

			});
		}

	}
}
