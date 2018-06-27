/**
 * 
 */
package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.services.client.IdcOrchestrationRequest;
import gov.nist.toolkit.services.client.IdcOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;

/**
 * Build environment for testing Imaging Document Consumer SUT
 */
public class BuildIDCTestOrchestrationButton extends AbstractOrchestrationButton {

   private ConformanceTestTab testTab;
   private Panel initializationPanel;
   private FlowPanel initializationResultsPanel = new FlowPanel();
    private TestContext testContext;
    private TestContextView testContextView;
    private TestRunner testRunner;

   BuildIDCTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, TestRunner testRunner, Panel initializationPanel, String label) {
       this.initializationPanel = initializationPanel;
       this.testTab = testTab;
       this.testContext = testContext;
       this.testContextView = testContextView;

       HTML instructions = new HTML(
               "<p>" +
                       "The System Under Test (SUT) is an Imaging Document Consumer. </p>" +
               "<p>" +
                       "The tests for an Imaging Document Consumer use a fixed set of images as input data." +
                       "The images with patient names and identifiers are listed with each test as appropriate. </p>" +
               "<p>" +
                       "The test data has departmental patient identifiers (e.g., those used in " +
                       "the Radiology Department when the images are acquired) and identifiers for the Affinity Domain. " +
                       "There is no assigning authority for the departmental identifiers. " +
                       "The assigning authority for the Affinity Domain is:" +
                       "<ul><li>&amp;1.3.6.1.4.1.21367.2005.13.20.1000&amp;ISO</li></ul></p>" +
               "<p>" +
                       "<h3>Standard Test Procedure</h3>" +
                       "<br />" +
                       "The tests below assume a standard testing procedure:" +
                       "<ol><li>A standard test set is created that contains imaging data and associated KOS objects. " +
                       "The standard test images are identified by patient identifier and are listed with each test as appropriate.</li>" +
                       "<li>The test software does not provide a mapping mechanism between the patient identifier in the image" +
                       "and the patient identifier in the Affinity Domain. " +
                       "It is the responsibility of the Imaging Document Consumer to use the correct patient " +
                       "identifier for the Affinity Domain.</li>" +
                       "<li>The Imaging Document Consumer is instructed to send query and retrieve requests to the testing system. " +
                       "The logging mechanism of the simulators in the testing system records the requests. " +
                       "The testing system supports retrieves using the RAD-55 (WADO) and RAD-69 (SOAP) transactions. " +
                       "Traditional DICOM C-Move transactions are not supported. " +
                       "The Imaging Document Consumer under test needs to complete all tests using " +
                       "RAD-55 transactions or all tests using RAD-69 transactions. " +
                       "The Imaging Document Consumer may test both RAD-55 and RAD-69 transactions if " +
                       "both are supported.</li>" +
                       "<li>The test manager reviews the Imaging Document Consumer requests captured by the test system simulators.</li></ol></p>" +
               "<p>" +
                       "Test validation has several aspects:" +
                       "<ol><li>Did the Imaging Document Consumer use properly formatted XDS.b query/retrieve operations (ITI-43, RAD-69 transactions)?</li>" +
                       "<li>Did the Imaging Document Consumer use properly formatted DICOM WADO retrieved operations (RAD-55 transaction)?</li>" +
                       "<li>Did the Imaging Document Consumer use the proper patient identifiers (and other identifiers) during the retrieve process.</li>" +
                       "<li>Can the Imaging Document Consumer demonstrate that it can use the retrieve images in their product? " +
                       "This step requires some interpretation. " +
                       "Some systems are workstations where the end product is to render data for a customer. " +
                       "Other Imaging Document Consumer systems might consist of a middleware implementation " +
                       "that retrieves the data and passes it on to another application.</li></ol></p>" +
                       ""



       );
       initializationPanel.add(instructions);

       this.testRunner = testRunner;

       setParentPanel(initializationPanel);
       setLabel(label);
       setResetLabel("Reset");
       build();
       panel().add(initializationResultsPanel);
   }

   public void orchestrate() {

       String msg = testContext.verifyTestContext();
       if (msg != null) {
           testContextView.launchDialog(msg);
           return;
       }

      initializationResultsPanel.clear();
       testTab.getMainView().showLoadingMessage("Initializing...");
      
      IdcOrchestrationRequest request = new IdcOrchestrationRequest();
      request.setTestSession(new TestSession(testTab.getCurrentTestSession()));
      request.setEnvironmentName(testTab.getEnvironmentSelection());
       request.setUseTls(isTls());
      request.setUseExistingState(!isResetRequested());
      SiteSpec siteSpec = new SiteSpec(testContext.getSiteName(), new TestSession(testTab.getCurrentTestSession()));
       if (isSaml()) {
           setSamlAssertion(siteSpec);
       }
       request.setSiteUnderTest(siteSpec);

      testTab.setSiteToIssueTestAgainst(siteSpec);
      
      ClientUtils.INSTANCE.getToolkitServices().buildIdcTestOrchestration(request, new AsyncCallback<RawResponse>() {
         @Override
         public void onFailure(Throwable throwable) {
             handleError(throwable);
         }
         
         @Override
         public void onSuccess(RawResponse rawResponse) {
            if (handleError(rawResponse, IdcOrchestrationResponse.class)) {
                testTab.getMainView().clearLoadingMessage();
                return;
            }
            IdcOrchestrationResponse orchResponse = (IdcOrchestrationResponse) rawResponse;
            testTab.setOrchestrationResponse(orchResponse);

            initializationResultsPanel.add(new HTML("Initialization Complete"));
            
            if (testContext.getSiteUnderTest() != null) {
               initializationResultsPanel.add(new HTML("<h2>System Under Test Configuration</h2>"));
               initializationResultsPanel.add(new HTML("Site: " + testContext.getSiteUnderTestName()));
           }

            initializationResultsPanel.add(new HTML("<h2>Generated Environment</h2>"));

            FlexTable table = new FlexTable();
            int row = 0;
            // Pass through simulators in Orchestra enum order
            for (Orchestra o : Orchestra.values()) {
               // get matching simulator config
               SimulatorConfig sim = null;
               for (SimulatorConfig c : orchResponse.getSimulatorConfigs()) {
                  if (c.getId().getId().equals(o.name())) {
                     sim = c;
                     break;
                  }
               }
               if (sim == null) continue;

               try {
               // First row: title, sim id, test data and log buttons
               table.setWidget(row, 0, new HTML("<h3>" + o.title + "</h3>"));
               table.setText(row++ , 1, sim.getId().toString());

               // Property rows, based on ActorType and Orchestration enum
               for (String property : o.getDisplayProps()) {
                  table.setWidget(row, 1, new HTML(property));
                  SimulatorConfigElement prop = sim.get(property);
                  String value = prop.asString();
                  if (prop.hasList()) value = prop.asList().toString();
                  table.setWidget(row++ , 2, new HTML(value));
               }
               } catch (Exception e) {
                  initializationResultsPanel.add(new HTML("<h3>exception " + o.name() + " " + e.getMessage() + "/h3>"));
               }
            }
            initializationResultsPanel.add(table);

            initializationResultsPanel.add(new HTML("<br />"));

            initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextView, testRunner, testTab ));

            initializationResultsPanel.add(new HTML("<br />"));

             initializationResultsPanel.add(new HTML("<p>Configure your " +
             "Imaging Document Consumer SUT to integrate with these simulators<hr/>"));

             testTab.displayTestCollection(testTab.getMainView().getTestsPanel());
        }

    });
}

   public enum Orchestra {

      ids("Imaging Document Source", ActorType.IMAGING_DOC_SOURCE,
         new SimulatorConfigElement[] {
            new SimulatorConfigElement(SimulatorProperties.idsRepositoryUniqueId, ParamType.TEXT,
               "1.3.6.1.4.1.21367.102.1.1"),
            new SimulatorConfigElement(SimulatorProperties.idsImageCache, ParamType.TEXT, "ids-dataset-a") }),

      rr("Repository Registry", ActorType.REPOSITORY_REGISTRY,
         new SimulatorConfigElement[] {
            new SimulatorConfigElement(SimulatorProperties.VALIDATE_AGAINST_PATIENT_IDENTITY_FEED, ParamType.BOOLEAN,
               false),
            new SimulatorConfigElement(SimulatorProperties.repositoryUniqueId, ParamType.TEXT,
               "1.3.6.1.4.1.21367.13.71.101.1") });

      public final String title;
      public final ActorType actorType;
      public final SimulatorConfigElement[] elements;

      Orchestra(String title, ActorType actorType, SimulatorConfigElement[] elements) {
         this.title = title;
         this.actorType = actorType;
         this.elements = elements;
      }

      public ActorType getActorType() {
         return actorType;
      }

      public SimulatorConfigElement[] getElements() {
         return elements;
      }

      /**
       * @return array of Simulator Property names which should be displayed in
       * Conformance testing for this type of actor.
       */
      public String[] getDisplayProps() {
         switch (actorType) {
            case IMAGING_DOC_SOURCE:
               return new String[] { 
                  SimulatorProperties.idsRepositoryUniqueId, 
                  SimulatorProperties.idsrEndpoint,
                  SimulatorProperties.wadoEndpoint, 
                  SimulatorProperties.idsImageCache, };
            case REPOSITORY_REGISTRY:
               return new String[] { 
                  SimulatorProperties.retrieveEndpoint,
                  SimulatorProperties.storedQueryEndpoint,
                  SimulatorProperties.repositoryUniqueId, };
            default:
         }
         return new String[0];
      }

   } // EO Orchestra enum

}
