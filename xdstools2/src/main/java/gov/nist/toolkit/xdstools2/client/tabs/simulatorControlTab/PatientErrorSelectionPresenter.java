package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.PatientError;
import gov.nist.toolkit.configDatatypes.client.PatientErrorList;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.TransactionType;

/**
 *
 */
public class PatientErrorSelectionPresenter {
    DecoratorPanel outerPanel = new DecoratorPanel();
    Panel panel = new VerticalPanel();
    final FlexTable table = new FlexTable();
//    Image deleteImage = new Image("icons/delete-button.png");
    final PatientErrorList patientErrorList;

    public PatientErrorSelectionPresenter(PatientErrorList _patientErrorList, /*final ToolkitServiceAsync toolkitService, */final TransactionType transactionType, SaveHandler<PatientErrorList> saveHandler) {
        this.patientErrorList = _patientErrorList;

        outerPanel.add(panel);

        Panel addPanel = new HorizontalPanel();
        Anchor addButton = new Anchor();
        addButton.getElement().appendChild(new Image("icons/add-button.png").getElement());

//        panel.display(new HTML("<h2>Patient ID ==> Error mapping</h2>"));
        addPanel.add(new HTML(transactionType.getName()));
        addPanel.add(addButton);
        panel.add(addPanel);
        panel.add(table);

        refresh();

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                new PatientErrorNewEntryPresentation(/*toolkitService, */transactionType, new SaveHandler<PatientError>() {
                    @Override
                    public void onSave(PatientError var) {
                        patientErrorList.add(var);
                        addPatientErrorToDisplay(var);
                    }
                });
            }
        });
    }

    void refresh() {
        table.clear();
        for (PatientError patientError : patientErrorList.values()) {
            addPatientErrorToDisplay(patientError);
        }

    }

    void addPatientErrorToDisplay(PatientError patientError) {
        int row = table.getRowCount();
        table.setText(row, 0, patientError.getPatientId().toString());
        table.setText(row, 1, patientError.getErrorCode());
        Anchor a = new Anchor();
        a.getElement().appendChild(new Image("icons/delete-button.png").getElement());
        a.addClickHandler(new DeleteClickHandler(row, patientError.getPatientId()) {
            @Override
            public void onClick(ClickEvent clickEvent) {
                table.removeRow(row);
                for (PatientError pe : patientErrorList.values()) {
                    if (pe.getPatientId().equals(pid)) {
                        patientErrorList.remove(pe);
                        return;
                    }
                }
            }
        });
        table.setWidget(row, 2, a);
    }

    public Widget asWidget() { return outerPanel; }

    private class DeleteClickHandler implements ClickHandler {
        int row;
        Pid pid;

        DeleteClickHandler(int row, Pid pid) {
            this.row = row;
            this.pid = pid;
        }

        @Override
        public void onClick(ClickEvent clickEvent) {}
    }
}
