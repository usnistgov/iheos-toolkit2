package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.command.command.GetTestSessionNamesCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 *
 */
class TestContextDialog extends DialogBox {
    private ToolWindow toolWindow;
    private ListBox testSessionListBox = new ListBox();
    private TextBox textBox = new TextBox();
    private ListBox siteListBox = new ListBox();
    private SiteManager siteManager;
    private HTML validationMessage = new HTML();
    private Button validateButton = new Button("Validate");
    private Button acceptButton = new Button("Assign System for Test Session");
    private Button clearTestSessionButton = new Button("Clear Test Session");
    private FlowPanel sitesForTestSessionPanel = new FlowPanel();


    TestContextDialog(ToolWindow toolWindow, SiteManager siteManager, String message) {
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

        header.add(new HTML("<h2>Conformance test context</h2>"));

        panel.add(new HTML("<hr />"));

        if (message != null) {
            ErrorHandler.handleError(panel, message);
            panel.add(new HTML("<hr />"));
        }

        panel.add(new HTML("Selection of codes and certificates"));
        panel.add(new HTML("Environment: " + toolWindow.getEnvironmentSelection()));


        panel.add(new HTML("<hr />"));

        panel.add(new HTML("A Test Session holds the test results for a single system under test."));

        HorizontalFlowPanel testSessionEdit = new HorizontalFlowPanel();
        testSessionEdit.add(new HTML("Test Session"));
        testSessionListBox.setVisibleItemCount(10);
        testSessionListBox.addChangeHandler(new TestSessionChangeHandler());
        loadTestSessions(toolWindow.getCurrentTestSession());
        testSessionEdit.add(testSessionListBox);

        FlowPanel testSessionEast = new FlowPanel();

        testSessionEast.add(sitesForTestSessionPanel);
        testSessionEdit.add(testSessionEast);
        testSessionEast.add(textBox);
        Button addButton = new Button("Add");
        addButton.addClickHandler(new NewTestSessionClickHandler());
        testSessionEast.add(addButton);

        loadSitesForTestSession(toolWindow.getCurrentTestSession());

        panel.add(testSessionEdit);
        panel.add(new HTML("<hr />"));

        panel.add(new HTML("System under test for this Test Session"));

        HorizontalFlowPanel siteSelection = new HorizontalFlowPanel();
        siteSelection.add(new HTML("System under test"));
        siteSelection.add(siteListBox);
        siteListBox.setVisibleItemCount(10);
        siteListBox.addChangeHandler(new SiteSelectionChangeHandler());
        loadSites();

        panel.add(siteSelection);
        panel.add(validationMessage);
//        validateButton.setVisible(false);
//        validateButton.addClickHandler(new ValidateClickHandler());
//        panel.build(validateButton);
        acceptButton.addClickHandler(new AcceptButtonClickHandler());
        panel.add(acceptButton);

        clearTestSessionButton.addClickHandler(new ClearTestSessionButtonClickHandler());

        this.add(panel);
    }

    private void loadSitesForTestSession(String testSession) {
        if (testSession == null || testSession.equals("")) {
            sitesForTestSessionPanel.clear();
            return;
        }
        ClientUtils.INSTANCE.getToolkitServices().getSitesForTestSession(testSession, new AsyncCallback<Collection<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("getSitesForTestSession failed: " + throwable.getMessage());
            }

            @Override
            public void onSuccess(Collection<String> strings) {
                sitesForTestSessionPanel.clear();
                sitesForTestSessionPanel.add(clearTestSessionButton);
                sitesForTestSessionPanel.add(new HTML("Contains results for sites:"));
                for (String s : strings) {
                    sitesForTestSessionPanel.add(new HTML(s));
                }
                if (strings.size() == 0) {
                    sitesForTestSessionPanel.add(new HTML("None"));
                }
            }
        });
    }

    private class ClearTestSessionButtonClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            ClientUtils.INSTANCE.getToolkitServices().clearTestSession(getSelectedTestSession(), new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("Clear Test Session failed: " + throwable.getMessage());
                }

                @Override
                public void onSuccess(String s) {
                    loadTestSessions(toolWindow.getCurrentTestSession());
                    loadSitesForTestSession(toolWindow.getCurrentTestSession());
                    loadSites();
                    siteManager.setSiteName(TestContext.NONE);
                }
            });
        }
    }

    private class AcceptButtonClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            String selectedSite = getSelectedSite();
            if (TestContext.NONE.equals(selectedSite))
                selectedSite = null;
            siteManager.setSiteName(selectedSite);
            toolWindow.setCurrentTestSession(getSelectedTestSession());
            siteManager.update();
//                       hide();
            ClientUtils.INSTANCE.getToolkitServices().setAssignedSiteForTestSession(getSelectedTestSession(), selectedSite, new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("SetAssignedSiteForTestSession failed: " + throwable.getMessage());
                }

                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }

    private class TestSessionChangeHandler implements ChangeHandler {

        @Override
        public void onChange(ChangeEvent changeEvent) {
            validateButton.setVisible(true);

            String newTestSession = testSessionListBox.getSelectedItemText();
            toolWindow.setCurrentTestSession(newTestSession);

            loadSitesForTestSession(newTestSession);

            ClientUtils.INSTANCE.getToolkitServices().getAssignedSiteForTestSession(newTestSession, new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("getAssignedSiteForTestSession failed: " + throwable.getMessage());
                }

                @Override
                public void onSuccess(String s) {
                    siteManager.setSiteName(s);
                    selectSite(s);
                }
            });
        }
    }

    private class SiteSelectionChangeHandler implements ChangeHandler {

        @Override
        public void onChange(ChangeEvent changeEvent) {
            validateButton.setVisible(true);
        }
    }

//    private class ValidateClickHandler implements ClickHandler {
//
//        @Override
//        public void onClick(ClickEvent clickEvent) {
//            if (getSelectedSite() == null || getSelectedSite().equals("")) {
//                new PopupMessage("Select Site first.");
//                return;
//            }
//            if (getSelectedTestSession() == null || getSelectedTestSession().equals("")) {
//                new PopupMessage("Select Test Session first.");
//                return;
//            }
//            String site = getSelectedSite();
//            String testSession = getSelectedTestSession();
//            toolkitService.validateConformanceSession(testSession, site, new AsyncCallback<ConformanceSessionValidationStatus>() {
//                @Override
//                public void onFailure(Throwable throwable) {
//                    new PopupMessage("Validation error: " + throwable.getMessage());
//                }
//
//                @Override
//                public void onSuccess(ConformanceSessionValidationStatus testStatus) {
//                    if (testStatus.isPass()) {
//                        new PopupMessage("Validates");
//                    }
//                    else {
//                        new PopupMessage(testStatus.getMessage());
//                    }
//                }
//            });
//        }
//    }

    private String getSelectedTestSession() {
        return testSessionListBox.getSelectedItemText();
    }

    private String getSelectedSite() {
        return siteListBox.getSelectedItemText();
    }

    private boolean siteSelected() {
        return siteManager.getSiteName() != null && !siteManager.getSiteName().equals("");
    }

    private boolean testSessionSelected() {
        return getSelectedTestSession() != null && !getSelectedTestSession().equals("");
    }

    private class NewTestSessionClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            final String newItem = textBox.getText();
            if (newItem == null || newItem.equals("")) return;

            ClientUtils.INSTANCE.getToolkitServices().addMesaTestSession(newItem, new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage("Cannot build test session - " + throwable.getMessage());
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
        new GetTestSessionNamesCommand() {

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
        ClientUtils.INSTANCE.getToolkitServices().getSiteNames(true, true, new AsyncCallback<List<String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                new PopupMessage("Cannot load sites.");
            }

            @Override
            public void onSuccess(List<String> strings) {
                List<String> contents = new ArrayList<String>();
                contents.add(TestContext.NONE);
                contents.addAll(strings);
                siteListBox.clear();
                for (String site : contents) {
                    siteListBox.addItem(site);
                }
                if (siteSelected()) {
                    int index = contents.indexOf(siteManager.getSiteName());
                    if (index != -1)
                        siteListBox.setSelectedIndex(index);
                } else {
                    siteListBox.setSelectedIndex(0);
                }
            }
        });
    }

    private void selectSite(String site) {
        if (site == null) {
            siteListBox.setSelectedIndex(0); // NONE
            return;
        }
        for (int i=0; i<siteListBox.getItemCount(); i++) {
            String value = siteListBox.getValue(i);
            if (site.equals(value)) {
                siteListBox.setSelectedIndex(i);
                return;
            }
        }
    }

    private class CloseClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            String session = getSelectedTestSession();
            if (session != null && !session.equals(""))
                toolWindow.setCurrentTestSession(session);

            siteManager.setSiteName(getSelectedSite());
            siteManager.update();
            hide();
        }
    }
}
