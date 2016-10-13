package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.ReloadAllTestResultsCallback;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Updater;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


/**
 * Created by Diane Azais local on 10/20/2015.
 */
public class ButtonClickHandler implements ClickHandler {
//    ToolkitServiceAsync service = (ToolkitServiceAsync) GWT.create(ToolkitService.class);
    Logger LOGGER = Logger.getLogger("ButtonClickHandler");
    Updater updater;
    CommandsWidget commandsWidget;
    private ToolkitServiceAsync toolkitService= ClientUtils.INSTANCE.getToolkitServices();


    public ButtonClickHandler(CommandsWidget _commandsWidget){
        commandsWidget = _commandsWidget;
    }


    @Override
    public void onClick(ClickEvent event) {
        Button source = (Button) event.getSource();


        if (source == commandsWidget.getPlayAllButton()){
            //TODO replace bogus site with actual site selected by user
            toolkitService.runAllTests(new Site("testEHR"), runAllTestsCallback);
        }
        else if (source == commandsWidget.getRemoveAllButton()){
            //TODO replace bogus site with actual site selected by user
            toolkitService.deleteAllTestResults(new Site("testEHR"), deleteAllLogsCallback);
        }
        else if (source == commandsWidget.getRefreshAllButton()){
            //TODO replace bogus site with actual site selected by user
            ReloadAllTestResultsCallback reloadAllTestResultsCallback = new ReloadAllTestResultsCallback(updater);
            try {
                toolkitService.reloadAllTestResults(updater.getCurrentTestSession(), reloadAllTestResultsCallback);
            } catch (Exception e) {
                LOGGER.warning(e.getMessage());
            }
        }
        else {
            // do nothing
        }
    }


    // --------------------------------------------------------------
    // -------------- Run all tests and update display --------------
    // --------------------------------------------------------------

        AsyncCallback<List<Test>> runAllTestsCallback = new AsyncCallback<List<Test>>()
        {
            @Override
            public void onFailure(Throwable caught)
            { LOGGER.warning("Failed to run all tests.");
            }

            @Override
            public void onSuccess(List<Test> result)
            {
                ArrayList<Test> array = new ArrayList<Test>();
                array.addAll(result);
                updater.updateTestData(array);
                updater.updateTestView();
            }
        };


    // --------------------------------------------------------------
    // ------------- Delete all logs and update display -------------
    // --------------------------------------------------------------

    AsyncCallback<List<Test>> deleteAllLogsCallback = new AsyncCallback<List<Test>>()
    {
        @Override
        public void onFailure(Throwable caught)
        { LOGGER.warning("Failed to delete all logs.");
        }

        @Override
        public void onSuccess(List<Test> result)
        {
            ArrayList<Test> array = new ArrayList<Test>();
            array.addAll(result);
            updater.updateTestData(array);
            updater.updateTestView();
        }
    };


    public void setViewUpdater(Updater _updater) {
        updater = _updater;
    }
}
