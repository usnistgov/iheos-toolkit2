/**
 * 
 */
package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;

import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.services.client.IdsOrchestrationRequest;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.services.client.IdsOrchestrationResponse;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.OrchestrationButton;

/**
 *
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class BuildIDSTestOrchestrationButton extends OrchestrationButton {

   private ConformanceTestTab testTab;
   private Panel initializationPanel;
   private FlowPanel initializationResultsPanel = new FlowPanel();

   BuildIDSTestOrchestrationButton(ConformanceTestTab testTab, Panel initializationPanel, String label) {
       this.initializationPanel = initializationPanel;
       this.testTab = testTab;

       setParentPanel(initializationPanel);
       setLabel(label);
       setResetLabel("Reset");
       build();
       panel().add(initializationResultsPanel);
   }

  
   @Override
   public void handleClick(ClickEvent clickEvent) {
      String msg = testTab.verifyConformanceTestEnvironment();
      if (msg != null) {
          testTab.launchTestEnvironmentDialog(msg);
          return;
      }

      initializationResultsPanel.clear();
      
      IdsOrchestrationRequest request = new IdsOrchestrationRequest();
      request.setUserName(testTab.getCurrentTestSession());
      request.setEnvironmentName(testTab.getEnvironmentSelection());
      request.setUseExistingSimulator(!isResetRequested());
      SiteSpec siteSpec = new SiteSpec(testTab.getSiteName());
      request.setIdsSut(siteSpec);

      testTab.setSitetoIssueTestAgainst(siteSpec);
      
      ClientUtils.INSTANCE.getToolkitServices().buildIdsTestOrchestration(request, new AsyncCallback<RawResponse>() {
         @Override
         public void onFailure(Throwable throwable) {
             handleError(throwable);
         }
         
         @Override
         public void onSuccess(RawResponse rawResponse) {
            if (handleError(rawResponse, IdsOrchestrationResponse.class)) return;
            IdsOrchestrationResponse orchResponse = (IdsOrchestrationResponse) rawResponse;

            initializationResultsPanel.add(new HTML("Initialization Complete"));
            
            if (testTab.getSiteUnderTest() != null) {
               initializationResultsPanel.add(new HTML("<h2>System Under Test Configuration</h2>"));
               initializationResultsPanel.add(new HTML("Site: " + testTab.getSiteUnderTest().getName()));
               FlexTable table = new FlexTable();
               int row = 0;
               table.setText(row, 0, "Retrieve Image Document Set");
               try {
                   table.setText(row++, 1, testTab.getSiteUnderTest().getRawEndpoint(TransactionType.RET_IMG_DOC_SET, false, false));
               } catch (Exception e) {
               }

               table.setText(row, 0, "Repository Unique ID");
               try {
                   String repUid = testTab.getSiteUnderTest().getRepositoryUniqueId(TransactionBean.RepositoryType.IDS);
                   table.setText(row++, 1, repUid);
               } catch (Exception e) {
                  new PopupMessage("sut config: " + e.getMessage());
               }

               initializationResultsPanel.add(table);
           }

            initializationResultsPanel.add(new HTML("<h2>Generated Environment</h2>"));

            initializationResultsPanel.add(new HTML("<h3>Supporting Repository/Registry Configuration</h3>"));
            initializationResultsPanel.add(new HTML("<br />"));

            FlexTable table = new FlexTable();

            int row = 0;
            
            SimulatorConfig rr = orchResponse.getRegrepConfig();
            
            table.setText(row, 0, "Name");
            table.setText(row++,  1,  rr.getId().toString());
            
            table.setText(row,  0, "Repository Unique ID");
            table.setText(row++, 1, rr.getConfigEle(SimulatorProperties.repositoryUniqueId).asString());

            table.setText(row, 0, "Provide and Register endpoint");
            table.setText(row++, 1, rr.getConfigEle(SimulatorProperties.pnrEndpoint).asString());

            initializationResultsPanel.add(table);
           
            initializationResultsPanel.add(new HTML("<p>Configure your Image Document Source SUT to forward Provide and Register transactions to the above endpoint.<hr />"));


            // test will be run out of support site so pass it back to conformance test tab
            //testTab.setSitetoIssueTestAgainst(orchResponse.getSupportSite());
        }


    });
}


}
