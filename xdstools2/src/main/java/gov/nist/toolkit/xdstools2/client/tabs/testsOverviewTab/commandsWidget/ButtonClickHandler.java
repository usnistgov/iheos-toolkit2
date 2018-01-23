package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.commandsWidget;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.results.shared.Test;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteAllTestResultsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.ReloadAllTestResultsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.RunAllTestsCommand;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.ReloadAllTestResultsCallback;
import gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab.Updater;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.shared.command.request.AllTestRequest;

import java.util.List;


/**
 * Created by Diane Azais local on 10/20/2015.
 */
public class ButtonClickHandler implements ClickHandler {
    Updater updater;
    CommandsWidget commandsWidget;


    public ButtonClickHandler(CommandsWidget _commandsWidget){
        commandsWidget = _commandsWidget;
    }


    @Override
    public void onClick(ClickEvent event) {
        Button source = (Button) event.getSource();


        if (source == commandsWidget.getPlayAllButton()){
            //TODO replace bogus site with actual site selected by user
            new RunAllTestsCommand(){
                @Override
                public void onComplete(List<Test> result) {
                    updater.updateAll(result);
                }
            }.run(new AllTestRequest(ClientUtils.INSTANCE.getCommandContext(),new Site(new TestSession("testEHR"))));
        }
        else if (source == commandsWidget.getRemoveAllButton()){
            //TODO replace bogus site with actual site selected by user
            new DeleteAllTestResultsCommand(){
                @Override
                public void onComplete(List<Test> result) {
                    updater.updateAll(result);
                }
            }.run(new AllTestRequest(ClientUtils.INSTANCE.getCommandContext(),new Site(new TestSession("testEHR"))));
        }
        else if (source == commandsWidget.getRefreshAllButton()){
            //TODO replace bogus site with actual site selected by user
            final ReloadAllTestResultsCallback reloadAllTestResultsCallback = new ReloadAllTestResultsCallback(updater);
                new ReloadAllTestResultsCommand(){
                    @Override
                    public void onComplete(List<Test> result) {
                        reloadAllTestResultsCallback.onSuccess(result);
                    }
                }.run(ClientUtils.INSTANCE.getCommandContext());
        }
        else {
            // do nothing
        }
    }

    public void setViewUpdater(Updater _updater) {
        updater = _updater;
    }
}
