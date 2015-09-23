package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.actortransaction.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.FindDocumentsSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;
import gov.nist.toolkit.xdstools2.client.widgets.queryFilter.GetAllParams;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diane Azais local on 9/23/2015.
 */
public class FindAllDocumentsTab extends GenericQueryTab {


    static List<TransactionType> transactionTypes = new ArrayList<TransactionType>();
    static {
        transactionTypes.add(TransactionType.STORED_QUERY);
        transactionTypes.add(TransactionType.IG_QUERY);
        transactionTypes.add(TransactionType.XC_QUERY);
    }
    static CoupledTransactions couplings = new CoupledTransactions();

    GenericQueryTab genericQueryTab;
    GetAllParams sqParams;

    public FindAllDocumentsTab() {
        super(new FindDocumentsSiteActorManager());
    }

    // Tab initialization
    @Override
    public void onTabLoad(TabContainer container, boolean select, String eventName) {
        myContainer = container;
        // Panel to build inside of
        topPanel = new VerticalPanel();

        genericQueryTab = this;   // share with other methods


        container.addTab(topPanel, "FindAllDocuments", select);  // link into container/tab management
        addCloseButton(container, topPanel, null);   // add the close button

        HTML title = new HTML();
        title.setHTML("<h2>Find Documents (all criteria)</h2>");
        topPanel.add(title);

        // Generate the composite widget that allows selection of all the GetAll query parameters. Below is the call
        // sqParams.asWidget() which gets the actual Widget.
        sqParams = new GetAllParams(toolkitService, genericQueryTab);

        mainGrid = new FlexTable();  // this is important in some tabs, not this one.  This init should be moved to definition
        topPanel.add(sqParams.asWidget());
        topPanel.add(new HTML("<hr/>"));

        topPanel.add(mainGrid);

        // add below-the-line-stuff (PatientId, site selection etc.)
        // Also link in the Runner class (shown below) which is called when the user clicks on the Run button.
        // Since this call organizes the site selection grid, it needs the transactionTypes and couplings config
        addQueryBoilerplate(new Runner(), transactionTypes, couplings, true);
    }

    @Override
    public String getWindowShortName() {
        return null;
    }
}

class Runner implements ClickHandler {
    @Override
    public void onClick(ClickEvent clickEvent) {

    }
}