package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.configDatatypes.client.PidSet;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.*;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.*;

/**
 *
 */
public class PidFavoritesTab  extends GenericQueryTab {
    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.REGISTER);
    }

    static CoupledTransactions couplings = new CoupledTransactions();

    public PidFavoritesTab() {
        super(new GetDocumentsSiteActorManager());
    }

    ListBox favoritesListBox = new ListBox();
    TextArea pidBox = new TextArea();
    VerticalPanel assigningAuthorityPanel = new VerticalPanel();
    HTML selectedPids = new HTML();

    // model
    Set<Pid> favoritePids = new HashSet<>();  // the database of values
    List<String> assigningAuthorities = null;

    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        topPanel = new VerticalPanel();


        container.addTab(topPanel, "Patient IDs", select);
        addCloseButton(container, topPanel, null);

        topPanel.add(new HTML("<h2>Manage Patient IDs</h2>"));

        mainGrid = new FlexTable();

        topPanel.add(mainGrid);

        HorizontalPanel panel = new HorizontalPanel();
        topPanel.add(panel);

        VerticalPanel favoritesListPanel = new VerticalPanel();
        panel.add(favoritesListPanel);

        favoritesListPanel.add(new HTML("Favorite Patient IDs"));
        favoritesListPanel.add(favoritesListBox);
        favoritesListBox.setVisibleItemCount(20);
        favoritesListBox.setMultipleSelect(true);
        favoritesListBox.setWidth("600px");
        favoritesListBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                updatePidsSelected(getSelectedPids());
            }
        });

        VerticalPanel pidPanel = new VerticalPanel();
        panel.add(pidPanel);

        pidPanel.add(new HTML("<h3>Add existing Patient ID(s)</h3>"));
        pidPanel.add(new HTML("Patient IDs (paste then Add to Favorites)"));
        pidBox.setCharacterWidth(50);
        pidBox.setVisibleLines(10);
        pidPanel.add(pidBox);

        HorizontalPanel pidButtonPanel = new HorizontalPanel();
        pidPanel.add(pidButtonPanel);

        HorizontalPanel favoritiesButtonPanel = new HorizontalPanel();
        favoritesListPanel.add(favoritiesButtonPanel);

        pidPanel.add(new HTML("<br />"));
        pidPanel.add(new HTML("<h3>Generate new Patient ID</h3>"
        ));
        pidPanel.add(assigningAuthorityPanel);

        favoritiesButtonPanel.add(new HTML("Select Patient ID(s) then: "));

        final Button deleteButton = new Button("Delete from Favorites", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                deleteFromFavorites(getSelectedPids());
            }
        });
        favoritiesButtonPanel.add(deleteButton);

        Button addToFavoritesButton = new Button("Add to Favorites", new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                addToFavorites(getInputPids());
            }
        });
        pidButtonPanel.add(addToFavoritesButton);

        setRunButtonText("Send Patient Identity Feed");
        setTlsEnabled(false);
        setSamlEnabled(false);
        setShowInspectButton(false);
        topPanel.add(new HTML("<h3>Generate V2 Patient Identity Feed</h3><br />(From selection in Favorites)" +
                        "<p>Note that this is NOT integrated with Gazelle Patient Management.  It should be used " +
                "for private testing only.</p>" ));
        queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, false);

        panel.add(selectedPids);

        fromCookie();
        updateFavoritesFromModel();
        loadAssigningAuthorities();
        addToFavorites(favoritePids);
    }

    @Override
    public String getWindowShortName() {
        return "pidfavorites";
    }

    // Model

    void toCookie() {
        Cookies.setCookie(CookieManager.FAVORITEPIDSCOOKIENAME, new PidSet(favoritePids).asParsableString());
}

    void fromCookie() {
        favoritePids = new PidSet(Cookies.getCookie(CookieManager.FAVORITEPIDSCOOKIENAME)).get();
    }

    void addToFavorites(Set<Pid> pids) {
        favoritePids.addAll(pids);
        updateFavoritesFromModel();
    }

    void addToFavorities(Pid pid) {
        favoritePids.add(pid);
        updateFavoritesFromModel();
    }

    void updatePidsSelected(Collection<Pid> pids) {
        StringBuilder buf = new StringBuilder();

        buf.append("<b>Selected Patient IDs</b><br />");
        for (Pid pid : pids) {
            buf.append(pid.asString()).append("<br />");
        }
        selectedPids.setHTML(buf.toString());
    }

    void updateFavoritesFromModel() {
        toCookie();
        favoritesListBox.clear();
        for (Pid pid : favoritePids) {
            // first is display value
            // second is parsable value. may contain more than just the id, like patient name
            //   it can be used to generate a Pid object later
            favoritesListBox.addItem(pid.asParsableString(), pid.asParsableString());
        }
    }

    Map<Button, String> authorityButtons = new HashMap<>();

    void updateAssigningAuthorities() {
        assigningAuthorityPanel.clear();
        authorityButtons.clear();
        for (String aa : assigningAuthorities) {
            final Button button = new Button("Generate Patient ID");
            button.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    String authority = authorityButtons.get(button);
                    generatePid(authority);
                }
            });
            authorityButtons.put(button, aa);
            assigningAuthorityPanel.add(new HTML("For AssigningAuthority " + aa));
            assigningAuthorityPanel.add(button);
        }
    }

    // and add it to favorites
    void generatePid(String assigningAuthority) {
        try {
            toolkitService.createPid(assigningAuthority, new AsyncCallback<Pid>() {
                @Override
                public void onFailure(Throwable throwable) {
                    new PopupMessage(throwable.getMessage());
                }

                @Override
                public void onSuccess(Pid pid) {
                    addToFavorities(pid);
                }
            });
        } catch (NoServletSessionException e) {
            new PopupMessage(e.getMessage());
        }
    }

    void deleteFromFavorites(List<Pid> pids) {
        List<Pid> deletables = new ArrayList<>();
        for (Pid pid : pids) {
            if (favoritePids.contains(pid)) deletables.add(pid);
        }
        favoritePids.removeAll(deletables);
        updateFavoritesFromModel();
    }

    void loadAssigningAuthorities() {
        try {
            toolkitService.getAssigningAuthorities(new AsyncCallback<List<String>>() {
                @Override
                public void onFailure(Throwable e) {
                    new PopupMessage("Error loading Assigning Authorities - usually caused by session timeout - " + e.getMessage());
                }

                @Override
                public void onSuccess(List<String> s) {
                    assigningAuthorities = s;
                    updateAssigningAuthorities();
                }
            });
        } catch (Exception e) {
            new PopupMessage(e.getMessage());
        }
    }

    List<Pid> getSelectedPids() {
        List<Pid> pids = new ArrayList<>();

        for (int i=0; i<favoritesListBox.getItemCount(); i++) {
            if (favoritesListBox.isItemSelected(i)) {
                String value = favoritesListBox.getValue(i);
                pids.add(PidBuilder.createPid(value));
            }
        }
        return pids;
    }

    Pid getSelectedPid() {
        List<Pid> pids = getSelectedPids();
        if (pids.size() == 0) {
            new PopupMessage("Must select a Patient ID");
            return null;
        }
        if (pids.size() > 1) {
            new PopupMessage("Must select only one Patient ID");
            return null;
        }
        return pids.get(0);
    }

    Set<Pid> getInputPids() {
        List<String> idStrings = formatIds(pidBox.getValue());
        Set<Pid> pids = new HashSet<>();
        for (String id : idStrings) {
            Pid pid = PidBuilder.createPid(id);
            if (pid != null) pids.add(pid);
        }
        return pids;
    }

    class Runner implements ClickHandler {

        public void onClick(ClickEvent event) {
            resultPanel.clear();

            if (!verifySiteProvided()) return;

            Pid pid = getSelectedPid();
            if (pid == null) return;

            rigForRunning();

            try {
                toolkitService.sendPidToRegistry(getSiteSelection(), pid, queryCallback);
            } catch (NoServletSessionException e) {
                e.printStackTrace();
            }
        }

    }



}
