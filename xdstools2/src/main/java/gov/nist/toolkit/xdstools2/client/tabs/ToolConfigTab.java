package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.session.client.TestSessionStats;
import gov.nist.toolkit.session.client.TestSessionStatsTool;
import gov.nist.toolkit.xdstools2.client.LoadGazelleConfigsClickHandler;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.InformationLink;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.TestkitConfigTool;
import gov.nist.toolkit.xdstools2.shared.command.request.GetAdminToolkitPropertiesRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.GetTestSessionStatsRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.SetToolkitPropertiesRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ToolConfigTab extends GenericQueryTab {
    // Special handling for these properties
    private static List<String> specialProperties = new ArrayList<>();
    private static String ExternalCache = "External_Cache";
    static {
        specialProperties.add(ExternalCache);
        specialProperties.add("Toolkit_Host");
        specialProperties.add("Toolkit_Port");
        specialProperties.add("Toolkit_TLS_Port");
    }
	
	private FlexTable grid = new FlexTable();
    private Map<String, String> props;
    private Button loadAllGazelleConfigsBtn = new Button("Load all Gazelle configs");
    private int gridRow;
    private int index;

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

		    new PopupMessage("You must be signed in as admin");
		    deleteMe();

//			PasswordManagement.addSignInCallback(signedInCallback);
//
//			new AdminPasswordDialogBox(container);

			return null;
		}

		HTML subtitle1 = new HTML();
		subtitle1.setHTML("<h3>Properties</h3>");
		container.add(subtitle1);

		container.add(new InformationLink("Property descriptions", "Toolkit properties help"));

		container.add(grid);

		Button goButton = new Button("Save");
		goButton.addClickHandler(new Saver());
		container.add(goButton);

		HTML separator = new HTML();
		separator.setHTML("<br/>");
		container.add(separator);

		HTML subtitle2 = new HTML();
		subtitle2.setHTML("<br/>");
		container.add(subtitle2);

		container.add(loadAllGazelleConfigsBtn);
		loadAllGazelleConfigsBtn.addClickHandler(new LoadGazelleConfigsClickHandler("ALL"));

		container.add(new HTML("<hr />"));
		/* new code for testkit update */
		TestkitConfigTool tkconf=new TestkitConfigTool(getTabContainer());
		container.add(tkconf);

        final FlowPanel tsStatsPanel = new FlowPanel();
        container.add(tsStatsPanel);
        new GetTestSessionStatsCommand() {

            @Override
            public void onComplete(List<TestSessionStats> result) {
                tsStatsPanel.add(new TestSessionStatsTool(result).asWidget());
            }
        }.run(new GetTestSessionStatsRequest(getCommandContext()));
        container.add(new HTML("<hr />"));

        addLoggingPropertiesPane(container);
        container.add(new HTML("<hr />"));

        return container;
	}

    private void addLoggingPropertiesPane(FlowPanel container) {
        HTML loggingSubtitle1 = new HTML();
        loggingSubtitle1.setHTML("<h2>Logging Properties</h2>");
        container.add(loggingSubtitle1);

        Anchor loggingPropertiesAnchor = new Anchor("Logging Properties");
        loggingPropertiesAnchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                Window.open(Xdstools2.servletContextName + "/sim/loggingProperties", "_blank","");
            }
        });
        container.add(loggingPropertiesAnchor);

        HTML loggingSeparator = new HTML();
        loggingSeparator.setHTML("<br/>");
        container.add(loggingSeparator);

        // Reload is not effective in Tomcat container, which has its own LogManager class, and does not use the Java Util Logging directly
        /*
        Button reloadLoggingPropertiesButton = new Button("Reload Logging Properties");
        reloadLoggingPropertiesButton.addClickHandler(new ReloadLoggingProperties());
        container.add(reloadLoggingPropertiesButton);
         */
        container.add(new HTML("<br/>"));
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
        new GetAdminToolkitPropertiesCommand(){
            @Override
            public void onComplete(Map<String, String> result) {
                if (result != null) {
                    props = result;
                    loadPropertyGrid();
                } else {
                    new PopupMessage("Properties could not be retrieved.");
                }
            }
        }.run(new GetAdminToolkitPropertiesRequest(getCommandContext(), PasswordManagement.hash));
    }

    void savePropertyFile() {
        if (PasswordManagement.isSignedIn) {
            new SetToolkitPropertiesCommand() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("Save properties failed.");
                }

                @Override
                public void onComplete(String result) {
                    new PopupMessage("Properties saved");
                    /*
                    FHIR Support sim is no longer needed after Asbestos was introduced.
                    Timer t = new Timer() {
                        @Override
                        public void run() {
                            fhirSupportServerStatus("isDeferred", new SimpleCallbackT<Response>() {
                                @Override
                                public void run(Response response) {
                                    if (response!=null) {
                                        if (response.getStatusCode()==Response.SC_OK) {
                                            if (response.getText()!=null) {
                                                if ("true".equals(response.getText())) {
                                                    fhirSupportServerStatus("start", new SimpleCallbackT<Response>() {
                                                        @Override
                                                        public void run(Response response) {
                                                            if (response!=null) {
                                                                if (response.getStatusCode()==Response.SC_OK) {
                                                                    new PopupMessage("Please refresh your browser for the settings to take effect!");
                                                                } else {
                                                                    new PopupMessage("FHIR init returned HTTP status code: " + response.getStatusCode());
                                                                }
                                                            }
                                                        }
                                                    });
                                                } else {
                                                    new PopupMessage("Properties saved");
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    };
                    t.schedule(1);
                     */
                }
            }.run(new SetToolkitPropertiesRequest(getCommandContext(), PasswordManagement.hash, props));
        } else {
            new PopupMessage("You must be signed in as admin");
        }
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

    class ReloadLoggingProperties implements ClickHandler {
        @Override
        public void onClick(ClickEvent clickEvent) {
            new ReloadToolkitLoggingPropertiesCommand() {
                @Override
                public void onComplete(Boolean result) {
                    if (result.booleanValue()) {
                        new PopupMessage("Reloaded.");
                    } else {
                        new PopupMessage("Failed.");
                    }
                }
            }.run(getCommandContext());
        }
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
                name = name.replace(' ', '_');
                props.put(name, value);
            }
            savePropertyFile();
        }
    }

    /*
    private void fhirSupportServerStatus(String cmd, final SimpleCallbackT<Response> callback) {
        String url = Xdstools2.servletContextName + "/init?cmd="+cmd;

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            @SuppressWarnings("unused")
            Request response = builder.sendRequest(null, new RequestCallback() {
                public void onError(Request request, Throwable exception) {
                }
                public void onResponseReceived(Request request, Response response) {
                    callback.run(response);
                }
            });
        } catch (RequestException e) {
        }
    }

     */

	class RmOldSimsClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
            new RemoveOldSimulatorsCommand(){
                @Override
                public void onComplete(Integer result) {
                    new PopupMessage(result + " simulators removed");
                }
            }.run(getCommandContext());
		}

	}
}
