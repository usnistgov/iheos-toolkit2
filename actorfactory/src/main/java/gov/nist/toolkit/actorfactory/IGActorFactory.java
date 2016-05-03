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

public class IGActorFactory extends AbstractActorFactory {
   SimId newID = null;

   static final List <TransactionType> incomingTransactions =
      Arrays.asList(TransactionType.STORED_QUERY, TransactionType.RETRIEVE);

   protected Simulator buildNew(SimManager simm, SimId newID,
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
      addFixedEndpoint(sc, SimulatorProperties.idsrEndpoint, actorType,
         TransactionType.RET_IMG_DOC_SET, false);
      addFixedEndpoint(sc, SimulatorProperties.idsrTlsEndpoint, actorType,
         TransactionType.RET_IMG_DOC_SET, true);
      addEditableConfig(sc, SimulatorProperties.respondingGateways,
         ParamType.SELECTION, new ArrayList <String>(), true);

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

      site.addTransaction(new TransactionBean(
         TransactionType.RET_IMG_DOC_SET.getCode(), RepositoryType.NONE,
         sc.get(SimulatorProperties.idsrEndpoint).asString(), false, isAsync));
      site.addTransaction(new TransactionBean(
         TransactionType.RET_IMG_DOC_SET.getCode(), RepositoryType.NONE,
         sc.get(SimulatorProperties.idsrTlsEndpoint).asString(), true, isAsync));

      return site;
   }

   public List <TransactionType> getIncomingTransactions() {
      return incomingTransactions;
   }

}
