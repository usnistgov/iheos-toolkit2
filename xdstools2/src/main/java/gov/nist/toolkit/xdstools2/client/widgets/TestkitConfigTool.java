package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.command.command.CheckTestkitExistenceCommand;
import gov.nist.toolkit.xdstools2.client.command.command.ConfigureTestkitCommand;
import gov.nist.toolkit.xdstools2.client.command.command.GenerateTestkitStructureCommand;
import gov.nist.toolkit.xdstools2.client.command.command.IndexTestkitsCommand;
import gov.nist.toolkit.xdstools2.client.selectors.EnvironmentManager;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.util.ToolkitServiceAsync;
import gov.nist.toolkit.xdstools2.shared.command.CommandContext;


/**
 * Code for the widget that updates the codes in a selected testkit.
 * Created by oherrmann on 3/3/16.
 */
public class TestkitConfigTool extends Composite {

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
        HorizontalPanel buttonsContainer = new HorizontalPanel();
        buttonsContainer.setStyleName("HP");
        buttonsContainer.add(runUpdater);
        buttonsContainer.add(new Button("Reindex Test Kits", new IndexTestKitsHandler()));
        buttonsContainer.add(new Button("Create EC testkit structure",new TestkitConfigTool.CreateTestkitStructureHandler()));
        container.add(buttonsContainer);
        container.add(indexStatus);

        initWidget(container);
    }

    private class CreateTestkitStructureHandler implements ClickHandler {
        @Override
        public void onClick(ClickEvent clickEvent) {
            clearFeedbackBoard();
            resultPanel.setHTML("Running...");
            container.add(resultPanel);
            container.addStyleName("loading");
            new GenerateTestkitStructureCommand() {
                @Override
                public void onComplete(Void result) {
                    resultPanel.setHTML("-- Success! ");
                    container.add(resultPanel);
                    container.removeStyleName("loading");
                }
            }.run(ClientUtils.INSTANCE.getCommandContext());
        }
    }

    private class IndexTestKitsHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            clearFeedbackBoard();
            indexStatus.setHTML("Running...");
            container.add(indexStatus);
            new IndexTestkitsCommand() {
                @Override
                public void onFailure(Throwable throwable) {
                    super.onFailure(throwable);
                    indexStatus.setHTML("Failure!");
                }
                @Override
                public void onComplete(Boolean var1) {
                    indexStatus.setHTML("Success!");
                }
            }.run(new CommandContext());
        }
    }

    /**
     * ClickHandler Runner class for the button in Testkit configuration widget.
     */
    public class RunTestkitConfigHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent clickEvent) {
            clearFeedbackBoard();
            container.addStyleName("loading");
            resultPanel.setHTML("Running (connection timeout is 30 sec) ...");
            container.add(resultPanel);
            new CheckTestkitExistenceCommand() {
                @Override
                public void onComplete(Boolean exists) {
                    if (exists) {
                        boolean confirmed = Window.confirm("There already is a existing testkit configured for this environment. " +
                                "This will override it. \nDo you want to proceed?");
                        if (confirmed) runConfigTestkit();
                        else clearFeedbackBoard();
                    } else {
                        runConfigTestkit();
                    }
                }
            }.run(new CommandContext(environmentManager.getSelectedEnvironment(),ClientUtils.INSTANCE.getTestSessionManager().getCurrentTestSession()));
        }

        /** Method that actually runs the configuration (code update) of the testkit. **/
        private void runConfigTestkit() {
            new ConfigureTestkitCommand(){
                @Override
                public void onFailure(Throwable throwable){
                    super.onFailure(throwable);
                    resultPanel.setHTML(throwable.getMessage());
                }
                @Override
                public void onComplete(String result) {
                    resultPanel.setHTML(result.replace("\n", "<br/>"));
                    container.add(resultPanel);
                    container.removeStyleName("loading");
                }
            }.run(new CommandContext(environmentManager.getSelectedEnvironment(),ClientUtils.INSTANCE.getTestSessionManager().getCurrentTestSession()));
        }
    }

    private void clearFeedbackBoard() {
        container.remove(resultPanel);
        container.remove(indexStatus);
    }
}
