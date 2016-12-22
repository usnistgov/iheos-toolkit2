/**
 * 
 */
package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.services.client.IdcOrchestrationRequest;
import gov.nist.toolkit.services.client.IdcOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.services.server.orchestration.IdcOrchestrationBuilder;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.OrchestrationSupportTestsDisplay;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
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
       this.testRunner = testRunner;

       setParentPanel(initializationPanel);
       setLabel(label);
       setResetLabel("Reset");
       build();
       panel().add(initializationResultsPanel);
   }

  
   @Override
   public void handleClick(ClickEvent clickEvent) {
       String msg = testContext.verifyTestContext();
       if (msg != null) {
           testContextView.launchDialog(msg);
           return;
       }

      initializationResultsPanel.clear();
      
      IdcOrchestrationRequest request = new IdcOrchestrationRequest();
      request.setUserName(testTab.getCurrentTestSession());
      request.setEnvironmentName(testTab.getEnvironmentSelection());
      request.setUseExistingState(!isResetRequested());
      SiteSpec siteSpec = new SiteSpec(testContext.getSiteName());
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
            if (handleError(rawResponse, IdcOrchestrationResponse.class)) return;
            IdcOrchestrationResponse orchResponse = (IdcOrchestrationResponse) rawResponse;
            testTab.setOrchestrationResponse(orchResponse);

            initializationResultsPanel.add(new HTML("Initialization Complete"));
            
            if (testContext.getSiteUnderTest() != null) {
               initializationResultsPanel.add(new HTML("<h2>System Under Test Configuration</h2>"));
               initializationResultsPanel.add(new HTML("Site: " + testContext.getSiteUnderTest().getName()));
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
                  if (prop.isList()) value = prop.asList().toString();
                  table.setWidget(row++ , 2, new HTML(value));
               }
               } catch (Exception e) {
                  initializationResultsPanel.add(new HTML("<h3>exception " + o.name() + " " + e.getMessage() + "/h3>"));
               }
            }
            initializationResultsPanel.add(table);

            initializationResultsPanel.add(new HTML("<br />"));

            initializationResultsPanel.add(new OrchestrationSupportTestsDisplay(orchResponse, testContext, testContextView, testRunner ));

            initializationResultsPanel.add(new HTML("<br />"));

             initializationResultsPanel.add(new HTML("<p>Configure your " +
             "Imaging Document Consumer SUT to integrate with these simulators<hr/>"));
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
