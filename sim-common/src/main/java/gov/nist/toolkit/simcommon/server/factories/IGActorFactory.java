package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagementui.client.Site;
import gov.nist.toolkit.sitemanagementui.client.TransactionBean;
import gov.nist.toolkit.sitemanagementui.client.TransactionBean.RepositoryType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IGActorFactory extends AbstractActorFactory implements IActorFactory {
   SimId newID = null;

   static final List <TransactionType> incomingTransactions =
      Arrays.asList(TransactionType.STORED_QUERY, TransactionType.RETRIEVE);

   public Simulator buildNew(SimManager simm, SimId newID,
                             boolean configureBase) {
      this.newID = newID;

      ActorType actorType = ActorType.INITIATING_GATEWAY;
      SimulatorConfig sc;
      if (configureBase) sc = configureBaseElements(actorType, newID);
      else sc = new SimulatorConfig();

      addFixedEndpoint(sc, SimulatorProperties.igqEndpoint, actorType,
         TransactionType.IG_QUERY, false);
      addFixedEndpoint(sc, SimulatorProperties.igqTlsEndpoint, actorType,
         TransactionType.IG_QUERY, true);
      addFixedEndpoint(sc, SimulatorProperties.igrEndpoint, actorType,
         TransactionType.IG_RETRIEVE, false);
      addFixedEndpoint(sc, SimulatorProperties.igrTlsEndpoint, actorType,
         TransactionType.IG_RETRIEVE, true);
      addEditableConfig(sc, SimulatorProperties.respondingGateways,
         ParamType.SELECTION, new ArrayList <String>(), true);
      addEditableConfig(sc, SimulatorProperties.returnNoHome,
              ParamType.BOOLEAN, false);

      return new Simulator(sc);
   }

   protected void verifyActorConfigurationOptions(SimulatorConfig sc) {

   }

   public Site getActorSite(SimulatorConfig sc, Site site) {
      String siteName = sc.getDefaultName();

      if (site == null) site = new Site(siteName);

      site.user = sc.getId().user; // labels this site as coming from a sim

      boolean isAsync = false;

      site.addTransaction(new TransactionBean(
         TransactionType.IG_QUERY.getCode(), RepositoryType.NONE,
         sc.get(SimulatorProperties.igqEndpoint).asString(), false, isAsync));
      site.addTransaction(new TransactionBean(
         TransactionType.IG_QUERY.getCode(), RepositoryType.NONE,
         sc.get(SimulatorProperties.igqTlsEndpoint).asString(), true, isAsync));

      site.addTransaction(new TransactionBean(
         TransactionType.IG_RETRIEVE.getCode(), RepositoryType.NONE,
         sc.get(SimulatorProperties.igrEndpoint).asString(), false, isAsync));
      site.addTransaction(new TransactionBean(
         TransactionType.IG_RETRIEVE.getCode(), RepositoryType.NONE,
         sc.get(SimulatorProperties.igrTlsEndpoint).asString(), true, isAsync));

      return site;
   }

   public List <TransactionType> getIncomingTransactions() {
      return incomingTransactions;
   }

   @Override
   public ActorType getActorType() {
      return ActorType.INITIATING_GATEWAY;
   }
}
