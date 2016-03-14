package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.client.selectors.EnvironmentManager;

import java.util.logging.Logger;

/**
 * Created by oherrmann on 3/3/16.
 */
public class TestkitConfigTool extends Composite {
    private final ToolkitServiceAsync toolkitService;
    private final HTML resultPanel=new HTML();
    private VerticalPanel container = new VerticalPanel();
    private EnvironmentManager environmentManager ;

    public TestkitConfigTool(TabContainer mytabContainer, ToolkitServiceAsync toolkitService) {
        this.toolkitService=toolkitService;
        container.add(new HTML("<h3>Configure Testkit</h3>"));
        container.add(new HTML("This tool will create a new copy of testkit configured for a selected affinity " +
                "domain configuration. An affinity domain is chosen by selecting an environment. <br/>The affinity testkit created will be placed in " +
                "the environment selected.<br/><br/>"));
        environmentManager = new EnvironmentManager(mytabContainer, toolkitService);
        container.add(environmentManager);
        Button runUpdater=new Button("Run",new RunTestkitConfigHandler());
        container.add(runUpdater);
//        container.add(resultPanel);

        initWidget(container);
    }

    public class RunTestkitConfigHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            resultPanel.setHTML("Running (connection timeout is 30 sec) ...");
            toolkitService.doesTestkitExist(environmentManager.getSelectedEnvironment(), new AsyncCallback<Boolean>() {

                @Override
                public void onFailure(Throwable throwable) {
                    // TODO probably gonna need to do something here
                    Logger.getLogger(this.getClass().getName()).info("weird");
                }

                @Override
                public void onSuccess(Boolean exists) {
                    Logger.getLogger(this.getClass().getName()).info("exists?");
                    if (exists) {
                        Logger.getLogger(this.getClass().getName()).info("OK");
                        boolean confirmed = Window.confirm("There already is a existing testkit configured for this environment. " +
                                "This will override it. Do you want to proceed?");
                        if (confirmed) runConfigTestkit();
                    } else {
                        Logger.getLogger(this.getClass().getName()).info("not OK");
                        runConfigTestkit();
                    }
                }
            });
        }
        public void runConfigTestkit() {
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
