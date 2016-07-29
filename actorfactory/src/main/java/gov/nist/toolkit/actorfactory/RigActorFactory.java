/**
 * 
 */
package gov.nist.toolkit.actorfactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;
import gov.nist.toolkit.xdsexception.NoSessionException;
import gov.nist.toolkit.xdsexception.NoSimulatorException;

/**
 * Creates Responding Imaging Gateway Simulator
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class RigActorFactory extends AbstractActorFactory {
   SimId newID = null;

   static final String homeCommunityIdBase = "urn:oid:1.1.4567334.101.";
   static int homeCommunityIdIncr = 1;

   static String getNewHomeCommunityId() {
      return homeCommunityIdBase + homeCommunityIdIncr++ ;
   }

   static final List <TransactionType> incomingTransactions =
      Arrays.asList(TransactionType.XC_RET_IMG_DOC_SET);

   @Override
   protected Simulator buildNew(SimManager simm, @SuppressWarnings("hiding") SimId newID,
      boolean configureBase)
         throws EnvironmentNotSelectedException, NoSessionException {
      this.newID = newID;
      ActorType actorType = ActorType.RESPONDING_IMAGING_GATEWAY;
      SimulatorConfig sc;
      if (configureBase) sc = configureBaseElements(actorType, newID);
      else sc = new SimulatorConfig();

      addEditableConfig(sc, SimulatorProperties.homeCommunityId, ParamType.TEXT,
         getNewHomeCommunityId());

      addFixedEndpoint(sc, SimulatorProperties.xcirEndpoint, actorType,
         TransactionType.XC_RET_IMG_DOC_SET, false);
      addFixedEndpoint(sc, SimulatorProperties.xcirTlsEndpoint, actorType,
         TransactionType.XC_RET_IMG_DOC_SET, true);
      addEditableConfig(sc, SimulatorProperties.imagingDocumentSources, 
         ParamType.SELECTION, new ArrayList<String>(), true);

      return new Simulator(sc);
   }

   @Override
   protected void verifyActorConfigurationOptions(SimulatorConfig sc) {
   }

   @Override
   public Site getActorSite(SimulatorConfig sc, Site site)
      throws NoSimulatorException {

      if (sc == null || sc.isExpired())
         throw new NoSimulatorException("Expired");

      try {
         String siteName = sc.getDefaultName();

         if (site == null) site = new Site(siteName);
         site.user = sc.getId().user; // labels this site as coming from a sim

         boolean isAsync = false;

         site.addTransaction(new TransactionBean(
            TransactionType.XC_RET_IMG_DOC_SET.getCode(), RepositoryType.NONE,
            sc.get(SimulatorProperties.xcirEndpoint).asString(), false,
            isAsync));
         site.addTransaction(new TransactionBean(
            TransactionType.XC_RET_IMG_DOC_SET.getCode(), RepositoryType.NONE,
            sc.get(SimulatorProperties.xcirTlsEndpoint).asString(), true,
            isAsync));

         site.setHome(sc.get(SimulatorProperties.homeCommunityId).asString());
         return site;
      } catch (Throwable t) {
         sc.isExpired(true);
         throw new NoSimulatorException("Not Defined", t);
      }
   }

   @Override
   public List <TransactionType> getIncomingTransactions() {
      return incomingTransactions;
   }

}
