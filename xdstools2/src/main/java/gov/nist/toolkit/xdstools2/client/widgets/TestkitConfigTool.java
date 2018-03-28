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
        container.add(new HTML("<h2>Operations on Local Testkits</h2>"));
        container.add(new HTML("These operations create/update content in the External Cache."));
        container.add(new HTML("<hr /><h3>Convert Testkit to a New Affinity Domain Configuration</h3>"));
        container.add(new HTML(
                "Convert the default testkit to align with a new codes.xml (Affinity Domain) configuration " +
                        "by first copying the default testkit into the environment selected below " +
                        "and then converting the copy to align with the codes.xml (Affinity Domain) configuration " +
                        "already installed in that environment." +
//                        "configured for a selected affinity " +
//                "domain configuration. An affinity domain is chosen by selecting an environment. <br/>The affinity testkit created will be placed in " +
//                "the environment selected." +
                        "<br/><br/>"
        ));
        environmentManager = new EnvironmentManager(myTabContainer);
        container.add(environmentManager);
        Button runUpdater=new Button("Convert testkit",new RunTestkitConfigHandler());
        runUpdater.setTitle("Run the generation of a local (codes updated) copy of the testkit in the selected environment.");
//        HorizontalPanel buttonsContainer = new HorizontalPanel();
//        buttonsContainer.setStyleName("HP");
        container.add(runUpdater);
        container.add(new HTML("<br /><hr /><h3>Reindex Testkits</h3><br />"));
        container.add(new HTML("Rebuild the index of test definitions used by the Conformance tool. This needs to be done " +
        "any time new tests are added to an environment in the External Cache. Restarting Toolkit also rebuilds the indexes."));
        container.add(new Button("Reindex Test Kits", new IndexTestKitsHandler()));
        container.add(new HTML("<br /><hr /><h3>Create Testkit Structure</h3><br />"));
        container.add(new HTML("For the currently selected Environment and Test Session, build the necessary directory structure " +
        "in the environment directory of the External Cache so that local tests can be add during development. " +
        "Remember to Reindex (above) after adding new tests so the Conformance Tool sees them."));
        container.add(new Button("Create testkit structure",new TestkitConfigTool.CreateTestkitStructureHandler()));
        container.add(new HTML("<br /><hr /><br />"));
//        container.add(buttonsContainer);
        container.add(indexStatus);
        container.add(new HTML("<br /><br /><br />"));

        initWidget(container);
    }

    /** ClickHandler class for the button trigger the creation of the testkits structure in all environment of the EC. **/
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

    /** Click Handler for the button that trigger the indexation of all existing testkits. **/
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
            new CheckTestkitExistenceCommand() {
                @Override
                public void onComplete(Boolean exists) {
                    if (exists) {
                        boolean confirmed = Window.confirm("There already is a existing testkit configured for the environment " +
                                        environmentManager.getSelectedEnvironment() + ". " +
                                "This will over write it. \nDo you want to proceed?");
                        if (confirmed) runConfigTestkit();
                        else clearFeedbackBoard();
                    } else {
                        boolean confirmed = Window.confirm("This will create a new testkit in environment " +
                                environmentManager.getSelectedEnvironment() +
                                " using the codes.xml in environment " +
                                environmentManager.getSelectedEnvironment() + ". " +
                                "\nDo you want to proceed?");
                        runConfigTestkit();
                    }
                }
            }.run(new CommandContext(environmentManager.getSelectedEnvironment(),ClientUtils.INSTANCE.getTestSessionManager().getCurrentTestSession()));
        }

        /** Method that actually runs the configuration (code update) of the testkit. **/
        private void runConfigTestkit() {
            container.addStyleName("loading");
            resultPanel.setHTML("Running (connection timeout is 30 sec) ...");
            container.add(resultPanel);
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
