package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.cell.client.*;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.SelectionChangeEvent;
import gov.nist.toolkit.configDatatypes.client.Pid;
import gov.nist.toolkit.configDatatypes.client.PidBuilder;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.command.command.GeneratePidCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GetAssigningAuthoritiesCommand;
import gov.nist.toolkit.xdstools2.client.command.command.RetrieveFavPidsCommand;
import gov.nist.toolkit.xdstools2.client.command.command.SendPidToRegistryCommand;
import gov.nist.toolkit.xdstools2.client.widgets.ScrollingPager;
import gov.nist.toolkit.xdstools2.shared.command.request.GeneratePidRequest;
import gov.nist.toolkit.xdstools2.shared.command.request.SendPidToRegistryRequest;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.GetDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.CookiesServices;

import java.io.IOException;
import java.util.*;

/**
 *
 */
public class PidFavoritesTab extends GenericQueryTab {
    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {transactionTypes.add(TransactionType.REGISTER);}
    static CoupledTransactions couplings = new CoupledTransactions();

    // table selection tool
    final MultiSelectionModel<Pid> selectionModel = new MultiSelectionModel<Pid>();
    // actual table widget
    CellTable<Pid> favoritesListBox = new CellTable<>();
    // table data model
    ListDataProvider<Pid> model = new ListDataProvider<Pid>();

    private TextArea pidBox = new TextArea();
    private HTML selectedPids = new HTML();
    private VerticalPanel assigningAuthorityPanel = new VerticalPanel();
    Map<Button, String> authorityButtons = new HashMap<>();

    private List<String> assigningAuthorities = null;
    private Set<Pid> configuredPids=new HashSet<Pid>();

    private Button deleteButton;

    public PidFavoritesTab(String tabName) {
        super(new GetDocumentsSiteActorManager());
    }

    @Override
    protected Widget buildUI() {
        FlowPanel tpanel=new FlowPanel();
        tpanel.add(new HTML("<h2>Manage Patient IDs</h2>"));

        mainGrid = new FlexTable();

        tpanel.add(mainGrid);

        HorizontalPanel panel = new HorizontalPanel();
        tpanel.add(panel);

        VerticalPanel favoritesListPanel = new VerticalPanel();
        panel.add(favoritesListPanel);

        // this links the data model with the actual table widget
        model.addDataDisplay(favoritesListBox);

        // this is the definition of the table "Patient ID" column (EditTextCell makes edition possible)
        Column<Pid, String> id = new Column<Pid, String>(new PidEditTextCell()) {
            @Override
            public String getValue(Pid pid) {
                return pid.toString();
            }
        };
        // this is what applies (/saves) the changes made to the pid when exiting edition mode
        id.setFieldUpdater(new FieldUpdater<Pid, String>() {
            @Override
            public void update(int i, Pid pid, String s) {
                pid.setId(s.split("\\^\\^\\^&" + pid.getAd())[0]);
                model.getList().get(i).setId(s.split("\\^\\^\\^&" + pid.getAd())[0]);
                toCookie();
            }
        });
        favoritesListBox.setColumnWidth(id, 65.0, Unit.PCT);
        favoritesListBox.addColumn(id, "Patient ID");

        // this is the definition of the table "name" column (EditTextCell makes edition possible)
        Column<Pid, String> name = new Column<Pid, String>(new PidEditTextCell()) {
            @Override
            public String getValue(Pid pid) {
                if (pid.getExtra() == null || pid.getExtra().isEmpty()) {
                    return "   ";
                }
                return pid.getExtra();
            }
        };
        // this is what applies(/saves) the changes made to "name" when exiting edition mode
        name.setFieldUpdater(new FieldUpdater<Pid, String>() {
            @Override
            public void update(int i, Pid pid, String s) {
                pid.setExtra(s);
                toCookie();
            }
        });
        favoritesListBox.setColumnWidth(name, 35.0, Unit.PCT);
        favoritesListBox.addColumn(name, "Name");

        favoritesListBox.setRowCount(10);
        favoritesListBox.setWidth("600px");

        // this handle selection change in the grid (MultiSelectionModel<Pid>)
        favoritesListBox.setSelectionModel(selectionModel);

        favoritesListPanel.add(new HTML("Favorite Patient IDs"));
        ScrollingPager p = new ScrollingPager();
        p.setSize("600px","400px");
        p.setDisplay(favoritesListBox);
        favoritesListPanel.add(p);
        favoritesListPanel.setWidth("600px");

        VerticalPanel pidPanel = new VerticalPanel();
        panel.add(pidPanel);
        pidPanel.add(selectedPids);

        pidPanel.addStyleName("paddedHorizontalPanel");

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

        deleteButton = new Button("Delete from Favorites", new ClickHandler() {
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

        return tpanel;
    }

    @Override
    protected void bindUI() {
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent selectionChangeEvent) {
                updatePidsSelected(selectionModel.getSelectedSet());
                refreshDeleteButton(selectionModel.getSelectedSet());
            }
        });
        try {
            retrieveAndInitFavPids();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadAssigningAuthorities();
    }

    private void refreshDeleteButton(Set<Pid> selectedPids) {
        deleteButton.setEnabled(true);
        for(Pid pid:selectedPids){
            if (configuredPids.contains(pid)){
                deleteButton.setEnabled(false);
                break;
            }
        }
    }

    @Override
    protected void configureTabView() {
        setRunButtonText("Send Patient Identity Feed");
        setTlsEnabled(false);
        setSamlEnabled(false);
        setShowInspectButton(false);
        tabTopPanel.add(new HTML("<h3>Generate V2 Patient Identity Feed</h3><br />(From selection in Favorites)" +
                "<p>Note that this is NOT integrated with Gazelle Patient Management.  It should be used " +
                "for private testing only.</p>"));
        queryBoilerplate = addQueryBoilerplate(new Runner(), transactionTypes, couplings, false);
    }

    @Override
    public String getWindowShortName() {
        return "pidfavorites";
    }

    void toCookie() {
        List<Pid> pids = new ArrayList<Pid>();
        for (Pid pid:model.getList()){
            if (!configuredPids.contains(pid)){
                pids.add(pid);
            }
        }
        CookiesServices.savePidFavoritesToCookies(pids);
        ((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireFavoritePidsUpdateEvent();
    }

    void retrieveAndInitFavPids() throws IOException {
        final Set<Pid> pidsList=new HashSet<Pid>();
        new RetrieveFavPidsCommand() {
            @Override
            public void onComplete(List<Pid> result) {
                pidsList.addAll(result);
                configuredPids.addAll(pidsList);
                pidsList.addAll(CookiesServices.retrievePidFavoritesFromCookies());
                List<Pid> pids =new LinkedList<Pid>(pidsList);
                Collections.sort(pids, new Comparator<Pid>() {
                    @Override
                    public int compare(Pid o1, Pid o2) {
                        return o1.getExtra().compareTo(o2.getExtra());
                    }
                });
                model.setList(pids);
            }
        }.run(getCommandContext());
    }

    void addToFavorites(Set<Pid> pids) {
        model.getList().addAll(pids);
        updateFavoritesFromModel();
    }

    void addToFavorities(Pid pid) {
        model.getList().add(pid);
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
        model.refresh();
        favoritesListBox.redraw();
        toCookie();
    }

    private void updateAssigningAuthorities() {
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

    // and addTest it to favorites
    private void generatePid(String assigningAuthority) {
        new GeneratePidCommand() {

            @Override
            public void onComplete(Pid pid) {
                addToFavorities(pid);
            }
        }.run(new GeneratePidRequest(getCommandContext(), assigningAuthority));

    }

    void deleteFromFavorites(List<Pid> pids) {
        for (Pid pid : pids) {
            model.getList().remove(pid);
        }
        selectedPids.setHTML("");
        selectionModel.clear();
        updateFavoritesFromModel();
    }

    private void loadAssigningAuthorities() {
        new GetAssigningAuthoritiesCommand() {
            @Override
            public void onComplete(List<String> var1) {
                assigningAuthorities = var1;
                updateAssigningAuthorities();
            }
        }.run(getCommandContext());
    }

    List<Pid> getSelectedPids() {
        return new ArrayList<Pid>(selectionModel.getSelectedSet());
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

            new SendPidToRegistryCommand() {

                @Override
                public void onComplete(List<Result> var1) {
                    queryCallback.onSuccess(var1);
                }
            }.run(new SendPidToRegistryRequest(getCommandContext(), getSiteSelection(), pid));
        }

    }

    class PidEditTextCell extends EditTextCell{
        @Override
        public void onBrowserEvent(Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
            Pid pid=((Pid) context.getKey());
            if(!PidFavoritesTab.this.configuredPids.contains(pid)){
                super.onBrowserEvent(context, parent, value,event,valueUpdater);
            }
        }
    }
}
