package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.ErrorHandler;
import gov.nist.toolkit.xdstools2.client.StringSort;
import gov.nist.toolkit.xdstools2.client.ToolWindow;
import gov.nist.toolkit.xdstools2.client.command.command.*;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;
import gov.nist.toolkit.xdstools2.shared.command.request.GetSiteNamesRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.SetAssignedSiteForTestSessionRequest;

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
    private SiteSelectionValidator siteSelectionValidator;


    TestContextDialog(ToolWindow toolWindow, SiteManager siteManager, SiteSelectionValidator siteSelectionValidator, String message) {
        super(true, true);
        this.toolWindow = toolWindow;
        this.siteManager = siteManager;
        this.siteSelectionValidator = siteSelectionValidator;

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
        new GetSitesForTestSessionCommand(){

            @Override
            public void onComplete(Collection<String> result) {
                sitesForTestSessionPanel.clear();
                sitesForTestSessionPanel.add(clearTestSessionButton);
                sitesForTestSessionPanel.add(new HTML("Contains results for systems:"));
                List<String> sortedResult = StringSort.sort(result);
                for (String s : sortedResult) {
                    sitesForTestSessionPanel.add(new HTML(s));
                }
                if (result.size() == 0) {
                    sitesForTestSessionPanel.add(new HTML("None"));
                }
            }
        }.run(ClientUtils.INSTANCE.getCommandContext());
    }


    private class ClearTestSessionButtonClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            new ClearTestSessionCommand(){

                @Override
                public void onComplete(String result) {
                    loadTestSessions(toolWindow.getCurrentTestSession());
                    loadSitesForTestSession(toolWindow.getCurrentTestSession());
                    loadSites();
                    siteManager.setSiteName(TestContext.NONE);
                }
            }.run(ClientUtils.INSTANCE.getCommandContext());
        }
    }

    private class AcceptButtonClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            String selectedSite = getSelectedSite();
            if (TestContext.NONE.equals(selectedSite))
                selectedSite = null;
            if (siteSelectionValidator != null)
                siteSelectionValidator.validate(new SiteSpec(selectedSite));
            siteManager.setSiteName(selectedSite);
            toolWindow.setCurrentTestSession(getSelectedTestSession());
            siteManager.update();
            new SetAssignedSiteForTestSessionCommand().run(new SetAssignedSiteForTestSessionRequest(ClientUtils.INSTANCE.getCommandContext(),getSelectedTestSession(),selectedSite));
        }
    }

    private class TestSessionChangeHandler implements ChangeHandler {

        @Override
        public void onChange(ChangeEvent changeEvent) {
            validateButton.setVisible(true);

            String newTestSession = testSessionListBox.getSelectedItemText();
            toolWindow.setCurrentTestSession(newTestSession);

            loadSitesForTestSession(newTestSession);

            new GetAssignedSiteForTestSessionCommand(){

                @Override
                public void onComplete(String result) {
                    siteManager.setSiteName(result);
                    selectSite(result);
                }
            }.run(ClientUtils.INSTANCE.getCommandContext());
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
            if (newItem == null || "".equals(newItem)) return;
            new AddMesaTestSessionCommand(){

                @Override
                public void onComplete(Boolean result) {
                    testSessionListBox.addItem(newItem);
                    testSessionListBox.setSelectedIndex(testSessionListBox.getItemCount() - 1);
                    toolWindow.setCurrentTestSession(newItem);
                }
            }.run(new CommandContext(ClientUtils.INSTANCE.getEnvironmentState().getEnvironmentName(),newItem));
        }
    }
    private void loadTestSessions(final String initialSelection) {
        new GetTestSessionNamesCommand() {

            @Override
            public void onComplete(List<String> var1) {
                var1 = StringSort.sort(var1);
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
        }.run(ClientUtils.INSTANCE.getCommandContext());
    }


    private void loadSites() {
        new GetSiteNamesCommand(){
            @Override
            public void onComplete(List<String> result) {
                List<String> contents = new ArrayList<>();
                contents.add(TestContext.NONE);
                contents.addAll(StringSort.sort(result));
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
        }.run(new GetSiteNamesRequest(ClientUtils.INSTANCE.getCommandContext(),true,true));
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
