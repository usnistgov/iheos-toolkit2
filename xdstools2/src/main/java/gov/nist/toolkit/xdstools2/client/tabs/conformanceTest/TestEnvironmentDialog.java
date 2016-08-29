package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestSessionNamesCommand;

import java.util.List;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
class TestEnvironmentDialog extends DialogBox {
    private ToolWindow toolWindow;
    private ListBox testSessionListBox = new ListBox();
    private TextBox textBox = new TextBox();
    private ListBox siteListBox = new ListBox();
    private SiteManager siteManager;

    TestEnvironmentDialog(ToolWindow toolWindow, SiteManager siteManager) {
        super(true, true);
        this.toolWindow = toolWindow;
        this.siteManager = siteManager;

        FlowPanel panel = new FlowPanel();
        setGlassEnabled(true);

        HorizontalFlowPanel header = new HorizontalFlowPanel();
        panel.add(header);

        Image close = new Image("icons2/cancel-16.png");
        close.addClickHandler(new CloseClickHandler());
        header.add(close);

        header.add(new HTML("<h2>Conformance test environment</h2>"));

        panel.add(new HTML("<hr />"));

        HTML environment = new HTML("Environment: " + toolWindow.getEnvironmentSelection());
        panel.add(environment);


        panel.add(new HTML("<hr />"));

        HorizontalFlowPanel testSessionEdit = new HorizontalFlowPanel();
        testSessionEdit.add(new HTML("Test Session"));
        testSessionListBox.setVisibleItemCount(10);
        loadTestSessions(toolWindow.getCurrentTestSession());
        testSessionEdit.add(testSessionListBox);
        testSessionEdit.add(textBox);

        Button addButton = new Button("Add");
        addButton.addClickHandler(new NewTestSessionClickHandler());
        testSessionEdit.add(addButton);

        panel.add(testSessionEdit);
        panel.add(new HTML("<hr />"));

        HorizontalFlowPanel siteSelection = new HorizontalFlowPanel();
        siteSelection.add(new HTML("Site under test"));
        siteSelection.add(siteListBox);
        siteListBox.setVisibleItemCount(10);
        loadSites();

        panel.add(siteSelection);

        this.add(panel);
    }

    private String getSelectedTestSession() {
        return testSessionListBox.getSelectedItemText();
    }

    private String getSelectedSite() {
        return siteListBox.getSelectedItemText();
    }

    private class NewTestSessionClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            final String newItem = textBox.getText();
            if (newItem == null || newItem.equals("")) return;

            toolkitService.addMesaTestSession(newItem, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("Cannot add test session - " + throwable.getMessage());
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    testSessionListBox.addItem(newItem);
                    testSessionListBox.setSelectedIndex(testSessionListBox.getItemCount() - 1);
                    toolWindow.setCurrentTestSession(newItem);
                }
            });

        }
    }
    private void loadTestSessions(final String initialSelection) {
        new GetTestSessionNamesCommand(Xdstools2.getHomeTab()) {

            @Override
            public void onComplete(List<String> var1) {
                testSessionListBox.clear();
                for (String ts : var1) {
                    testSessionListBox.addItem(ts);
                }
                if (initialSelection != null && !initialSelection.equals("")) {
                    int selectedIndex = var1.indexOf(initialSelection);
                    if (selectedIndex != -1)
                        testSessionListBox.setSelectedIndex(selectedIndex);
                }
            }
        }.run(Xdstools2.getHomeTab().getCommandContext());
    }

    private void loadSites() {
        toolkitService.getSiteNames(true, true, new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Cannot load sites.");
            }

            @Override
            public void onSuccess(List<String> strings) {
                siteListBox.clear();
                for (String site : strings) {
                    siteListBox.addItem(site);
                }
                if (siteSelected()) {
                    int index = strings.indexOf(siteManager.getSite());
                    if (index != -1)
                        siteListBox.setSelectedIndex(index);
                }
            }
        });
    }

    private boolean siteSelected() {
        return siteManager.getSite() != null && !siteManager.getSite().equals("");
    }

    private class CloseClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            String session = getSelectedTestSession();
            if (session != null && !session.equals(""))
                toolWindow.setCurrentTestSession(session);

            siteManager.setSite(getSelectedSite());
            siteManager.update();
            hide();
        }
    }
}
