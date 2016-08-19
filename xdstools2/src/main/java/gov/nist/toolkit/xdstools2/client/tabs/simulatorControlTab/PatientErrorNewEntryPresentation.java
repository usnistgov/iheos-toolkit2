package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actortransaction.client.Severity;
import gov.nist.toolkit.configDatatypes.client.PatientError;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import java.util.List;

import static gov.nist.toolkit.xdstools2.client.ToolWindow.toolkitService;

/**
 *
 */
public class PatientErrorNewEntryPresentation  {

    public PatientErrorNewEntryPresentation(/*ToolkitServiceAsync toolkitService, */TransactionType transactionType, SaveHandler<PatientError> saveHandler) {
        new Dialog(/*toolkitService, */transactionType, saveHandler).show();
    }

    private static class Dialog extends DialogBox {

        Dialog(/*ToolkitServiceAsync toolkitService, */TransactionType transactionType, final SaveHandler<PatientError> saveHandler) {
            Panel surroundPanel = new VerticalPanel();
            Panel panel = new HorizontalPanel();
            final TextBox patientIdTextBox = new TextBox();
            final ListBox errorListBox = new ListBox();
            final Button cancelButton = new Button("Cancel");
            final Button saveButton = new Button("Save");
            Panel buttonPanel = new HorizontalPanel();

            panel.add(new HTML("Patient ID"));
            panel.add(patientIdTextBox);
            panel.add(new HTML("Error to return"));
            panel.add(errorListBox);
            buttonPanel.add(cancelButton);
            buttonPanel.add(saveButton);
            surroundPanel.add(panel);
            surroundPanel.add(buttonPanel);

            setWidget(surroundPanel);


            toolkitService.getTransactionErrorCodeRefs(transactionType.getName(), Severity.Error, new AsyncCallback<List<String>>() {

                public void onFailure(Throwable caught) {
                    new PopupMessage("getTransactionErrorCodeRefs:" + caught.getMessage());
                }

                public void onSuccess(List<String> results) {
                    for (String err : results) errorListBox.addItem(err);
                    errorListBox.setVisibleItemCount(results.size());

                    cancelButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            Dialog.this.hide();
                        }
                    });

                    saveButton.addClickHandler(new ClickHandler() {
                        @Override
                        public void onClick(ClickEvent clickEvent) {
                            String patientId = patientIdTextBox.getText();
                            String errorToThrow = errorListBox.getSelectedItemText();
                            if (patientId == null || patientId.equals("")) {
                                new PopupMessage("Must enter Patient ID");
                                Dialog.this.hide();
                                return;
                            }
                            if (errorToThrow == null) {
                                new PopupMessage("Must select Error");
                                Dialog.this.hide();
                                return;
                            }
                            Pid pid = PidBuilder.createPid(patientId);
                            if (pid == null) {
                                new PopupMessage("Invalid Patient ID");
                                return;
                            }
                            final PatientError patientError = new PatientError();
                            patientError.setErrorCode(errorToThrow);
                            patientError.setPatientId(pid);

                            Dialog.this.hide();
                            saveHandler.onSave(patientError);
                        }
                    });

                }
            });
        }
    }

}
