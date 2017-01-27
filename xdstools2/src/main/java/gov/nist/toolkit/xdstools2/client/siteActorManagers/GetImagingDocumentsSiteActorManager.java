/**
 * 
 */
package gov.nist.toolkit.xdstools2.client.siteActorManagers;


import gov.nist.toolkit.actortransaction.shared.SiteSpec;

/**
 *
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class GetImagingDocumentsSiteActorManager extends BaseSiteActorManager {

  
   @Override
   public String getEndpointSelectionHelp() {
      return "This tool tests an Initiating Imaging Gateway by simulating an " +
             "Imaging Document Consumer, sending Retrieve Imaging Document " +
             "Set Request (RAD-69) messages to the system under test. The " +
             "also provides simulated Responding Imaging Gateway and " +
             "Imaging Document Source actors, simulating three communities " +
             "for the system under test (SUT) to communicate with. To use " +
             "the tool, (1) Add or select a Test Session, (2) Build a Test " +
             "Environment for that Session, (3) configure your SUT using the " +
             "provided Test Environment data, (4) Create a Site/Actor " +
             "configuration in the toolkit for your SUT, (5) Select your SUT," +
             "(6) Select the test to run, (7) Run the test, and (8) Inspect " +
             "the test results.";
   }
   

   public SiteSpec verifySiteSelection() {
      // TODO Auto-generated method stub
      return null;
   }

}
