package gov.nist.toolkit.xdstools2.client.tabs.simulatorControlTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.xdstools2.client.command.command.AddPatientIdsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.DeletePatientIdsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetPatientIdsCommand;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.PatientIdsRequest;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PidEditTab extends GenericQueryTab {
    SimulatorConfig config;
    SimId simId;
    ListBox pidList = new ListBox();
    TextArea pidBox = new TextArea();
    Anchor reload = null;

    public PidEditTab(SimulatorConfig config) {
        super(new FindDocumentsSiteActorManager());
        this.config = config;
        simId = config.getId();
    }

    @Override
    public String getWindowShortName() {
        return "PidEditTab";
    }

    @Override
    protected Widget buildUI() {
        FlowPanel flowPanel=new FlowPanel();
        flowPanel.add(new HTML("<h2>Registry Simulator Patient ID Display/Edit</h2>"));
        flowPanel.add(new HTML("<h3>Simulator " + simId.toString() + "</h3>"));

        mainGrid=new FlexTable();
        flowPanel.add(mainGrid);

        addReloader();

        HorizontalPanel panel = new HorizontalPanel();
        flowPanel.add(panel);

        VerticalPanel listPanel = new VerticalPanel();
        panel.add(listPanel);
        listPanel.add(new HTML("Registered Patient IDs"));

        pidList.setVisibleItemCount(25);
        pidList.setMultipleSelect(true);
        listPanel.add(pidList);

        Button deleteButton = new Button("Delete", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                deletePid();
            }
        });
        listPanel.add(deleteButton);

        VerticalPanel pidPanel = new VerticalPanel();
        panel.add(pidPanel);
        pidPanel.add(new HTML("Patient ID(s) to Add..."));
        pidBox.setCharacterWidth(50);
        pidBox.setVisibleLines(20);
        pidPanel.add(pidBox);


        pidPanel.add(new Button("Add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                addPid();
            }
        }));

        return flowPanel;
    }

    @Override
    protected void bindUI() {
        loadPids();
    }

    @Override
    protected void configureTabView() {

    }

    boolean containsPid(String pidString) {
        for (int i=0; i<pidList.getItemCount(); i++) {
            if (pidString.equals(pidList.getItemText(i))) return true;
        }
        return false;
    }

    private void addPid() {
        final String value = pidBox.getValue();
        List<String> stringValues = formatIds(value);
        final List<Pid> pids = new ArrayList<>();
        List<String> badPidList = new ArrayList<>();
        for (String stringValue : stringValues) {
            Pid pid = PidBuilder.createPid(stringValue);
            if (pid == null) {
                badPidList.add(stringValue);
            } else {
                pids.add(pid);
            }
        }
        if (badPidList.size() > 0)
            new PopupMessage("These are not properly formatted Patient IDs, they will be ignored - " + badPidList);
        if (pids.size() == 0) {
            new PopupMessage("Enter some Patient IDs");
            // clear pid input field
            pidBox.setText("");
            return;
        }
        new AddPatientIdsCommand(){
            @Override
            public void onComplete(String o) {
                int dups = 0;
                int added = 0;
                for (Pid pid : pids) {
                    String s = pid.asString();
                    if (containsPid(s)) { dups++; continue; }
                    added++;
                    pidList.insertItem(s, 0);
                }
                // clear pid input field
                pidBox.setText("");
                if (dups == 0) new PopupMessage(added + " Patient IDs added");
                else if (dups == 1) new PopupMessage("1 Patient ID added, others were duplicates");
                else new PopupMessage(added + " Patient IDs added, " + dups + "  were duplicates");
            }
        }.run(new PatientIdsRequest(getCommandContext(),simId,pids));
    }

    private void deletePid() {
        List<String> toDelete = new ArrayList<>();
        for (int i=0; i<pidList.getItemCount(); i++) {
            if (pidList.isItemSelected(i)) {
                toDelete.add(pidList.getItemText(i));
            }
        }
        if (toDelete.size() == 0) new PopupMessage("Nothing selected to delete");
        else {
            List<Pid> pidsToDelete = new ArrayList<>();
            for (String pidString : toDelete) {
                Pid p = PidBuilder.createPid(pidString);
                if (p != null) pidsToDelete.add(p);
            }
            new DeletePatientIdsCommand(){
                @Override
                public void onComplete(Boolean result) {
                    loadPids();
                }
            }.run(new PatientIdsRequest(getCommandContext(),simId,pidsToDelete));
        }
    }

    private void loadPids() {
        new GetPatientIdsCommand(){
            @Override
            public void onComplete(List<Pid> pids) {
                pidList.clear();
                for (Pid pid : pids) pidList.addItem(pid.asString());
            }
        }.run(new PatientIdsRequest(getCommandContext(),simId));
    }

    public void addReloader() {
        if (reload == null) {
            reload = new Anchor();
            reload.setTitle("Reload Patient ID data");
            reload.setText("[reload]");
            addToMenu(reload);

            reload.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    loadPids();
                }

            });
            reload.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    onReload();
                }

            });
        }
    }

}
