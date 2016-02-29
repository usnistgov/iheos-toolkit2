package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.PatientError;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.List;

/**
 *
 */
public class PatientErrorSelectionPresenter {
    Panel panel = new VerticalPanel();
    final FlexTable table = new FlexTable();
    Image deleteImage = new Image("icons/delete-button.png");
    final List<PatientError> patientErrorList;

    public PatientErrorSelectionPresenter(List<PatientError> _patientErrorList, final ToolkitServiceAsync toolkitService, final TransactionType transactionType, SaveHandler<List<PatientError>> saveHandler) {
        this.patientErrorList = _patientErrorList;
        Panel addPanel = new HorizontalPanel();
        Button addButton = new Button("Add...");

        addPanel.add(addButton);
        panel.add(addPanel);
        panel.add(table);

        for (PatientError patientError : patientErrorList) {
            addPatientErrorToDisplay(patientError);
        }

        addButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                new PatientErrorNewEntryPresentation(toolkitService, transactionType, new SaveHandler<PatientError>() {
                    @Override
                    public void onSave(PatientError var) {
                        patientErrorList.add(var);
                        addPatientErrorToDisplay(var);
                    }
                });
            }
        });

    }

    void addPatientErrorToDisplay(PatientError patientError) {
        int row = table.getRowCount();
        table.setText(row, 0, patientError.getPatientId().toString());
        table.setText(row, 1, patientError.getErrorCode());
        Anchor a = new Anchor();
        a.getElement().appendChild(deleteImage.getElement());
        a.addClickHandler(new DeleteClickHandler(row, patientError.getPatientId()) {
            @Override
            public void onClick(ClickEvent clickEvent) {
                table.removeRow(row);
                for (PatientError pe : patientErrorList) {
                    if (pe.getPatientId().equals(pid)) {
                        patientErrorList.remove(pe);
                        return;
                    }
                }
            }
        });
        table.setWidget(row, 2, a);
    }

    public Widget asWidget() { return panel; }

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
