/**
 *
 */
package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.services.client.IdsOrchestrationRequest;
import gov.nist.toolkit.services.client.IdsOrchestrationResponse;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.xdstools2.client.command.command.BuildIdsTestOrchestrationCommand;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.client.widgets.buttons.AbstractOrchestrationButton;
import gov.nist.toolkit.xdstools2.shared.command.request.BuildIdsTestOrchestrationRequest;

/**
 *
 *
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class BuildIDSTestOrchestrationButton extends AbstractOrchestrationButton {

    private ConformanceTestTab testTab;
    private Panel initializationPanel;
    private FlowPanel initializationResultsPanel = new FlowPanel();
    private TestContext testContext;
    private TestContextView testContextView;

   BuildIDSTestOrchestrationButton(ConformanceTestTab testTab, TestContext testContext, TestContextView testContextView, Panel initializationPanel, String label) {
       this.initializationPanel = initializationPanel;
       this.testTab = testTab;
       this.testContext = testContext;
       this.testContextView = testContextView;
       HTML instructions = new HTML(
               "<h2>Preamble</h2>" +
               "<p>"  +
                       "Please read the overview material here: " +
                       "<a href=\"https://github.com/usnistgov/iheos-toolkit2/wiki/Conformance-XDSI-Imaging-Document-Source-DICOM-Instances\">" +
                       "Conformance XDSI Imaging Document Source DICOM Instances</a> " +
                       "for information about where to find the test data and for general execution instructions." +
               "</p>" +
               "<p>"  +
                       "The System Under Test (SUT) is an Imaging Document Source. " +
                       "The diagram below shows the test environment with the SUT in orange. " +
                       "The test software creates and configures the simulators in the diagram. " +
               "</p>" +
               "<p>"  +
                       "You need to configure your Imaging Document Source to communicate with " +
                       "the simulators shown in the diagram. " +
                       "After you have initialized the test environment, you should see the full set of configuration " +
                       "parameters needed to configure and test your system. " +
               "</p>" +
               "<p>"  +
                       "Use the following value for the Assigning Authority for the patient identifiers in the XDS Affinity Domain:" +
                       "<br /><blockquote>1.3.6.1.4.1.21367.2005.13.20.1000</blockquote>" +
               "</p>" +
               "<p>"  +
                       "Patient identifiers will be of the following form (ignoring escaping for XML)" +
                       "<br /><blockquote>IDS-AD001-a^^^&amp;1.3.6.1.4.1.21367.2005.13.20.1000&amp;ISO</blockquote>" +
               "</p>" +
               "<p>"  +
                       "Imaging Document Source is required to use the following OID in the repositoryUniqueID for " +
                       "RAD-69 transactions and in the Retrieve Location UID (0040,&nbsp;E011) element in the KOS object:" +
                       "<br /><blockquote>1.3.6.1.4.1.21367.13.80.110</blockquote>" +
                       "No restrictions are placed on the element Retrieve AE Title (0008,&nbsp;0054)" +
               "</p>" +
               "<p>"  +
                       "The tests for an Imaging Document Consumer use a fixed set of images as input data. " +
                       "Each imaging study is identified by a department identifier (DICOM Patient ID (0010,&nbsp;0020) " +
                       "and possibly by an Accession Number (0008,&nbsp;0050). " +
                       "The Patient Identifier in the XDS.b metadata is not the same as the identifier in the " +
                       "DICOM image. " +
                       "It is the responsibility of the Imaging Document Source to map the departmental identifier " +
                       "to the Affinity Domain identifier specified in the test cases. " +
               "</p>" +
               "<p>"  +
                       "The tests below assume a standard testing procedure:" +
                       "<ol><li>Imaging Document Source imports the test images and does not change patient names, " +
                       "patient identifiers, accession numbers or unique identifiers. " +
                       "The tests will fail if the Imaging Document Source modifies those elements within the images.</li>" +
                       "<li>The Imaging Document Source maps the departmental identifiers to the identifiers " +
                       " identified by the Affinity Domain (see individual tests for values). " +
                       "The test tools do not provide a mapping service.</li>" +
                       "<li>Imaging Document Source generates a KOS object for each imaging study and submits that " +
                       "KOS object via a Provide and Register transaction to a Repository/Registry simulator that is " +
                       "dedicated to the Imaging Document Source. " +
                       "<ul><li>Note that there is at least one patient that has three imaging studies. " +
                       "The tests assume that each imaging study is processed separately by the Imaging Document Source. " +
                       "That is, the tests expect separate KOS objects for each imaging study, even though " +
                       "the imaging studies are for the same patient.</li></ul>" +
                       "</li>" +

                       "<li>Imaging Document Source provides access to each imaging study using all three mechanisms defined by the XDS-I profile:" +
                       "<ol><li>RAD-69 Retrieve Imaging Document Set</li>" +
                       "<li>RAD-55 DICOM WADO Retrieve (Not yet ready)</li>" +
                       "<li>RAD-16 DICOM C-Move (Not yet ready)</li></ol>" +
                       "</li>" +
                       "</ol>" +
               "</p>"
       );

       initializationPanel.add(instructions);

       setSystemDiagramUrl("diagrams/IDSdiagram.png");

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
      
      IdsOrchestrationRequest request = new IdsOrchestrationRequest();
      request.setUserName(testTab.getCurrentTestSession());
      request.setEnvironmentName(testTab.getEnvironmentSelection());
      request.setUseExistingSimulator(!isResetRequested());
      SiteSpec siteSpec = new SiteSpec(testContext.getSiteName());
       if (isSaml()) {
           setSamlAssertion(siteSpec);
       }
       request.setSiteUnderTest(siteSpec);

        testTab.setSiteToIssueTestAgainst(siteSpec);

        new BuildIdsTestOrchestrationCommand(){
            @Override
            public void onComplete(RawResponse rawResponse) {
                if (handleError(rawResponse, IdsOrchestrationResponse.class)) return;
                IdsOrchestrationResponse orchResponse = (IdsOrchestrationResponse) rawResponse;
                testTab.setOrchestrationResponse(orchResponse);

                initializationResultsPanel.add(new HTML("FrameworkInitialization Complete"));

                if (testContext.getSiteUnderTest() != null) {
                    initializationResultsPanel.add(new HTML("<h2>System Under Test Configuration</h2>"));
                    initializationResultsPanel.add(new HTML("Site: " + testContext.getSiteUnderTest().getName()));
                    FlexTable table = new FlexTable();
                    int row = 0;
                    table.setText(row, 0, "Retrieve Image Document Set");
                    try {
                        table.setText(row++, 1, testContext.getSiteUnderTest().getRawEndpoint(TransactionType.RET_IMG_DOC_SET, false, false));
                    } catch (Exception e) {
                    }

               table.setText(row, 0, "Repository Unique ID");
                String repositoryUid = "UNKNOWN";
                try {
                    repositoryUid = testContext.getSiteUnderTest().getRepositoryUniqueId(TransactionBean.RepositoryType.REPOSITORY);
                } catch (Exception e) {
                    repositoryUid = "UNKNOWN";
                }
               try {
                   if (repositoryUid.equals("UNKNOWN")) {
                       repositoryUid = testContext.getSiteUnderTest().getRepositoryUniqueId(TransactionBean.RepositoryType.IDS);
                   }
               } catch (Exception e) {
                  new PopupMessage("sut config: " + e.getMessage());
                   repositoryUid = "UNKNOWN";
               }
                table.setText(row++, 1, repositoryUid);

                    initializationResultsPanel.add(table);
                }

                initializationResultsPanel.add(new HTML("<h2>Generated Environment</h2>"));

                initializationResultsPanel.add(new HTML("<h3>Supporting Repository/Registry Configuration</h3>"));
                initializationResultsPanel.add(new HTML("<br />"));

                FlexTable table = new FlexTable();

                int row = 0;

                SimulatorConfig rr = orchResponse.getRRConfig();

                table.setText(row, 0, "Name");
                table.setText(row++,  1,  rr.getId().toString());

                table.setText(row,  0, "Repository Unique ID");
                table.setText(row++, 1, rr.getConfigEle(SimulatorProperties.repositoryUniqueId).asString());

                table.setText(row, 0, "Provide and Register endpoint");
                table.setText(row++, 1, rr.getConfigEle(SimulatorProperties.pnrEndpoint).asString());

            initializationResultsPanel.add(table);
           
            initializationResultsPanel.add(new HTML("<p>Configure your Image Document Source SUT to send Provide and Register transactions to the above endpoint.<hr />"));


                // test will be run out of support site so pass it back to conformance test tab
                //testTab.setSitetoIssueTestAgainst(orchResponse.getSupportSite());
            }
        }.run(new BuildIdsTestOrchestrationRequest(ClientUtils.INSTANCE.getCommandContext(),request));
    }


}
