package gov.nist.toolkit.xdstools2Framework.client.framework;

import com.google.gwt.core.client.GWT;
import gov.nist.toolkit.toolkitFramework.client.environment.EnvironmentState;
import gov.nist.toolkit.toolkitFramework.client.testSession.TestSessionManager;

import javax.inject.Inject;

/**
 *
 */
public class OldFrameworkSupport implements FrameworkSupport {
    // Central storage for parameters shared across all
    // query type tabs
    private final QueryState queryState = new QueryState();
    private String toolkitName;
    private  String toolkitBaseUrl = null;
    private  String wikiBaseUrl = null;
    private boolean displayHomeTab = true;
    static private final HomeTab ht = new HomeTab();
    private static final ClientFactory clientFactory = GWT.create(ClientFactory.class);
    private static OldFrameworkSupport INSTANCE;

    @Inject
    EnvironmentState environmentState;

    @Inject
    TestSessionManager testSessionManager;



    public OldFrameworkSupport() {
        INSTANCE = this;
    }

    /*********************************************
     *
     * Implementation of FrameworkSupport
     *
     **********************************************/


    @Override
    public void setToolkitName(String name) {
        this.toolkitName = name;
    }

    @Override
    public String getToolkitName() {
        return toolkitName;
    }

    @Override
    public void setToolkitBaseUrl(String url) {
        this.toolkitBaseUrl = url;
    }

    @Override
    public String getToolkitBaseUrl() {
        return toolkitBaseUrl;
    }

    @Override
    public void setWikiBaseUrl(String url) {
        this.wikiBaseUrl = url;
    }

    @Override
    public String getWikiBaseUrl() {
        return wikiBaseUrl;
    }

    @Override
    public void displayHomeTab() {
        if (!displayHomeTab)
            ht.setDisplayTab(false);
        ht.onTabLoad(false, "Home");

    }

    @Override
    public void reloadTransactionOfferings() {
        new GetTransactionOfferingsCommand() {

            @Override
            public void onComplete(TransactionOfferings var1) {
                SiteSelectionComponent.transactionOfferings = var1;
            }
        }.run(getHomeTab().getCommandContext());
    }

    @Override
    public void resizeToolkit() { }

    @Override
    public QueryState getQueryState() {
        return queryState;
    }

    @Override
    public EnvironmentState getEnvironmentState() {return environmentState;}


    @Override
    public GenericQueryTab getHomeTab() {
        return ht;
    }

    public void doNotDisplayHomeTab() { displayHomeTab = false; }

//    static public void addtoMainMenu(Widget w) { INSTANCE.mainMenuPanel.add(w); }
//
//    static public void clearMainMenu() { INSTANCE.mainMenuPanel.clear(); }

}
