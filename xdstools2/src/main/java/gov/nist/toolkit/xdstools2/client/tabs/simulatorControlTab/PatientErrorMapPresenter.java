package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.configDatatypes.client.PatientErrorList;
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PatientErrorMapPresenter {
    Panel panel = new VerticalPanel();
    PatientErrorMap map;
    List<PatientErrorSelectionPresenter> presenters = new ArrayList<>();

    PatientErrorMapPresenter(PatientErrorMap map, ActorType actorType/*, ToolkitServiceAsync toolkitService*/) {
        this.map = map;
        if (map.isEmpty()) {
            // initialize map with all transactionTypes for this actorType
            for (TransactionType transactionType : actorType.getTransactions()) {
                map.put(transactionType.getName(), new PatientErrorList());
            }
        }

        for (String transactionTypeName : map.keySet()) {
            TransactionType transactionType = actorType.getTransaction(transactionTypeName);
            if (transactionType == null) {
                new PopupMessage("Internal error: transactionType " + transactionTypeName + "  not defined by actor type " + actorType);
                return;
            }
            PatientErrorList patientErrorList = map.get(transactionTypeName);
//            panel.addTest(new HTML("<h3>" + transactionTypeName + "</h3>"));
            PatientErrorSelectionPresenter patientErrorSelectionPresenter =
                    new PatientErrorSelectionPresenter(
                            patientErrorList,
//                            toolkitService,
                            transactionType,
                            new PatientErrorListSaveHandler(transactionTypeName));
            panel.add(patientErrorSelectionPresenter.asWidget());
            presenters.add(patientErrorSelectionPresenter);
        }
    }

    void refresh() {
        for (PatientErrorSelectionPresenter presenter : presenters)
            presenter.refresh();
    }

    private class PatientErrorListSaveHandler implements SaveHandler<PatientErrorList> {
        String transactionTypeName;

        PatientErrorListSaveHandler(String transactionTypeName) {
            this.transactionTypeName = transactionTypeName;
        }

        @Override
        public void onSave(PatientErrorList var) {
            map.put(transactionTypeName, var);
        }
    }

    Widget asWidget() { return panel; }
}
