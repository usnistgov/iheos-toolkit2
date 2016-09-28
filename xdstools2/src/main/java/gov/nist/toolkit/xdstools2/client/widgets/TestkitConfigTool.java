package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.selectors.EnvironmentManager;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;

import java.util.logging.Logger;


/**
 * Code for the widget that updates the codes in a selected testkit.
 * Created by oherrmann on 3/3/16.
 */
public class TestkitConfigTool extends Composite {
    private ToolkitServiceAsync toolkitService= ClientUtils.INSTANCE.getToolkitServices();

    private VerticalPanel container = new VerticalPanel();
    private final HTML resultPanel=new HTML();
    private HTML indexStatus = new HTML();

    private EnvironmentManager environmentManager ;

    /**
     * Main constructor (default)
     * @param myTabContainer tab container
     */
    public TestkitConfigTool(TabContainer myTabContainer) {
        container.add(new HTML("<h3>Configure Testkit</h3>"));
        container.add(new HTML("This tool will create a new copy of testkit configured for a selected affinity " +
                "domain configuration. An affinity domain is chosen by selecting an environment. <br/>The affinity testkit created will be placed in " +
                "the environment selected.<br/><br/>"));
        environmentManager = new EnvironmentManager(myTabContainer);
        container.add(environmentManager);
        Button runUpdater=new Button("Run",new RunTestkitConfigHandler());
        container.add(runUpdater);
        container.add(new Button("Reindex Test Kits", new IndexTestKitsHandler()));
        container.add(indexStatus);

        initWidget(container);
    }

    private class IndexTestKitsHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            indexStatus.setHTML("Running...");
            toolkitService.indexTestKits(new AsyncCallback<Boolean>() {
                @Override
                public void onFailure(Throwable throwable) {
                    indexStatus.setHTML("Failed - " + throwable.getMessage());
                }

                @Override
                public void onSuccess(Boolean aBoolean) {
                    indexStatus.setHTML("Success");
                }
            });
        }
    }

    /**
     * ClickHandler Runner class for the button in Testkit configuration widget.
     */
    public class RunTestkitConfigHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            resultPanel.setHTML("Running (connection timeout is 30 sec) ...");
            toolkitService.doesTestkitExist(environmentManager.getSelectedEnvironment(), new AsyncCallback<Boolean>() {

                @Override
                public void onFailure(Throwable throwable) {
                    Logger.getLogger(this.getClass().getName()).info(throwable.getMessage());
                    new PopupMessage("Error when trying to configure testkit.");
                }

                @Override
                public void onSuccess(Boolean exists) {
                    if (exists) {
                        boolean confirmed = Window.confirm("There already is a existing testkit configured for this environment. " +
                                "This will override it. Do you want to proceed?");
                        if (confirmed) runConfigTestkit();
                    } else {
                        runConfigTestkit();
                    }
                }
            });
        }

        /** Method that actually runs the configuration (code update) of the testkit. **/
        private void runConfigTestkit() {
            toolkitService.configureTestkit(environmentManager.getSelectedEnvironment(), new AsyncCallback<String>() {
                @Override
                public void onFailure(Throwable throwable) {
                    Logger.getLogger(this.getClass().getName()).info("not OK");
                    resultPanel.setHTML(throwable.getMessage());
                }

                @Override
                public void onSuccess(String s) {
                    Logger.getLogger(this.getClass().getName()).info("OK");
                    resultPanel.setHTML(s.replace("\n", "<br/>"));
                    container.add(resultPanel);
                }
            });
        }
    }
}
