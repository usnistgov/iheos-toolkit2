package gov.nist.toolkit.xdstools2.client.inspector.mvp;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.StepResult;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.abstracts.AbstractPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class InspectorPresenter extends AbstractPresenter<InspectorView> {
    private List<Result> results;
    private SiteSpec siteSpec;

    @Override
    public void init() {
        GWT.log("Init InspectorPresenter");


        setData();

        // The provided EventBus doesn't seem to work ?

        new Timer() {
            @Override
            public void run() {
                resize();
            }
        }.schedule(1000);
    }

    void setData() {
        GWT.log("Calling setData");
        view.objectRefTable.setData(composeTableData());
        view.objectRefTable.resizeTable();
        GWT.log("row el is null? ");
    }

    public void resize() {
        GWT.log("Resizing objectRef tale");
        getView().objectRefTable.resizeTable();
    }

    public List<ObjectRef> composeTableData() {
        List<ObjectRef> objectRefs = new ArrayList<>();

        if (results!=null && !results.isEmpty()) {
            for (Result result : results)  {
               for (StepResult stepResult : result.stepResults)  {
                    if (stepResult.toBeRetrieved!=null && !stepResult.toBeRetrieved.isEmpty())  {
                       objectRefs.addAll(stepResult.toBeRetrieved);
                    }
                }
            }
        }
        return objectRefs;
    }


    public void setResults(List<Result> results) {
        this.results = results;
    }


    public void setSiteSpec(SiteSpec siteSpec) {
        this.siteSpec = siteSpec;
    }
}
