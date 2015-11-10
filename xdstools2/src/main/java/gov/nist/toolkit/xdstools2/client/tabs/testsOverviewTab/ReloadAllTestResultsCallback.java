package gov.nist.toolkit.xdstools2.client.tabs.testsOverviewTab;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.results.shared.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by Diane Azais local on 10/21/2015.
 *
 * This callback is called in two places, the Tests View and the Commands Widget, so it needs to stay a separate class.
 */
public class ReloadAllTestResultsCallback implements AsyncCallback<List<Test>> {

    Logger LOGGER = Logger.getLogger("ReloadAllTestResultsCallback");
    Updater updater;


    @Override
    public void onFailure(Throwable caught)
    { LOGGER.warning("Failed to load the test results for current site and session, in the Tests Overview tab.");
    }

    @Override
    public void onSuccess(List<Test> result) {
        ArrayList<Test> res = new ArrayList<Test>();
        res.addAll(result);
        updater.updateTestData(res);
        updater.updateTestView();
    }

  public ReloadAllTestResultsCallback(Updater _updater){
      updater = _updater;
  }
};



