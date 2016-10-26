package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.List;

/**
 *
 */
public abstract class AbstractTool extends GenericQueryTab {
    private AbstractTool me = null;
    private boolean checkSite = false;
    private List<TransactionType> transactionTypes = null;
    private CoupledTransactions couplings = new CoupledTransactions();

    /**
     * @return Short name to be displayed in tool tab
     */
    abstract public String getTabTitle();

    /**
     * @return Full title to be displayed at top of tool.
     */
    public abstract String getToolTitle();

    /**
     * @return a short name for tool that corresponds to its documentation in
     * site/tools
     */
    abstract public String getWindowShortName();

    public AbstractTool() {
        super(null);
    }

    /**
     * Tool specific initialization.
     */
    abstract public void initTool();

    /**
     *
     */
    abstract public void run();

    @Override
    protected Widget buildUI() {
        FlowPanel fp=new FlowPanel();
        HTML title = new HTML();
        title.setHTML("<h2>" + getToolTitle() + "</h2>");
        fp.add(title);

        mainGrid = new FlexTable();
        fp.add(mainGrid);

        initTool();

        // TODO - throw error
        if (transactionTypes == null && checkSite) {
            new PopupMessage("Tool " + getToolTitle() + " does not declare any transactionTypes");
//            return;
        }

        return fp;
    }

    @Override
    protected void configureTabView() {
        addQueryBoilerplate(new Runner(), transactionTypes, couplings, hasPatientIdParam);
    }

    class Runner implements ClickHandler {

        public void onClick(ClickEvent event) {
            resultPanel.clear();

            if (checkSite && !verifySiteProvided()) return;
            if (hasPatientIdParam && !verifyPidProvided()) return;

            prepareToRun();

            run();
        }

    }

    /**
     * If true then declareTransactionTypes must be called declaring the transactions used.  This call
     * must be made in initTool().  If true then run() will only be allowed to proceed if a site
     * is selected. CheckSite is set to true by declareTransactionTypes - if you display
     * selectable site then the validation that one is selected will be done when you hit the
     * run button.
     * @param checkSite
     */
    public void setCheckSite(boolean checkSite) {
        this.checkSite = checkSite;
    }

    /**
     * Display patient id input and validate it is filled in on run.
     */
    public void requirePatientId() {
        hasPatientIdParam = true;
    }

    public void declareTransactionTypes(List<TransactionType> tt) {
        transactionTypes = tt;
        checkSite = true;
    }

    public void declareTransactionCouplings(CoupledTransactions ct) {
        couplings = ct;
    }
}
