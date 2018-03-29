/**
 * 
 */
package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.client.ActorType;
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
 * Factory class for Combined Initiating Gateway Simulator
 * (Initiating Gateway and Initiating Imaging Gateway)
 * 
 * @author Ralph Moulton / MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 *
 */
public class CigActorFactory extends AbstractActorFactory implements IActorFactory {
   
   @Override
   protected Simulator buildNew(SimManager simm, SimId newID, boolean configureBase) throws Exception {
   ActorType actorType = ActorType.COMBINED_INITIATING_GATEWAY;
   SimulatorConfig sc;
   if (configureBase) sc = configureBaseElements(actorType, newID, newID.getTestSession());
   else sc = new SimulatorConfig();
   
   SimId simId = sc.getId();
   
   // Group with an Initiating Gateway
   IGActorFactory ig = new IGActorFactory();
   SimulatorConfig igConfig = ig.buildNew(simm, simId, true).getConfig(0);
   sc.add(igConfig);
   
   // Group with an Initiating Imaging Gateway
   IigActorFactory iig = new IigActorFactory();
   SimulatorConfig iigConfig = iig.buildNew(simm, simId, true).getConfig(0);
   sc.add(iigConfig);
   
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

   site = new IGActorFactory().buildActorSite(sc, site);
   site = new IigActorFactory().buildActorSite(sc, site);

   return site;
}

@Override
public List<TransactionType> getIncomingTransactions() {
   List<TransactionType> tt = new ArrayList<TransactionType>();
   tt.addAll(new IGActorFactory().getIncomingTransactions());
   tt.addAll(new IigActorFactory().getIncomingTransactions());
   return tt;
}


   @Override
   public ActorType getActorType() {
      return ActorType.COMBINED_INITIATING_GATEWAY;
   }
}
