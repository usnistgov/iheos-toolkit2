/**
 * 
 */
package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.shared.ActorType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.NoSimulatorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Factory Class for Combined Responding Gateway Simulator
 * (Responding Gateway and Responding Imaging Gateway)
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class CrgActorFactory extends AbstractActorFactory implements IActorFactory {
   
   @Override
   protected Simulator buildNew(SimManager simm, SimId newID, boolean configureBase) throws Exception {
      ActorType actorType = ActorType.COMBINED_RESPONDING_GATEWAY;
      SimulatorConfig sc;
      if (configureBase) sc = configureBaseElements(actorType, newID, newID.getTestSession());
      else sc = new SimulatorConfig();
      
      SimId simId = sc.getId();
      
      // Group with a Responding Gateway
      RGActorFactory rg = new RGActorFactory();
      rg.setTransactionOnly(isTransactionOnly());
      SimulatorConfig rgConfig = rg.buildNew(simm, simId, true).getConfig(0);
      sc.add(rgConfig);
      
      // Group with a Responding Imaging Gateway
      RigActorFactory rig = new RigActorFactory();
      SimulatorConfig rigConfig = rig.buildNew(simm, simId, true).getConfig(0);
      sc.add(rigConfig);
      
      return new Simulator(sc);
   }

   @Override
   protected void verifyActorConfigurationOptions(SimulatorConfig sc) {
      
   }

   public Site buildActorSite(SimulatorConfig sc, Site site) throws NoSimulatorException {
      String siteName = sc.getDefaultName();

      if (site == null)
         site = new Site(siteName, sc.getId().getTestSession());
      site.setTestSession(sc.getId().getTestSession());  // labels this site as coming from a sim

      boolean isAsync = false;

      site = new RGActorFactory().buildActorSite(sc, site);
      site = new RigActorFactory().buildActorSite(sc, site);

      return site;
   }

   @Override
   public List<TransactionType> getIncomingTransactions() {
      List<TransactionType> tt = new ArrayList<TransactionType>();
      tt.addAll(new RGActorFactory().getIncomingTransactions());
      tt.addAll(new RigActorFactory().getIncomingTransactions());
      return tt;
   }

   @Override
   public ActorType getActorType() {
      return ActorType.COMBINED_RESPONDING_GATEWAY;
   }
}
