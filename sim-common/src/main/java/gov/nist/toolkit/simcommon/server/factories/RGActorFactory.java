package gov.nist.toolkit.simcommon.server.factories;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties;
import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.IActorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.NoSimulatorException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RGActorFactory extends AbstractActorFactory implements IActorFactory {
   static private final Logger logger = Logger.getLogger(RGActorFactory.class);

   SimId newID = null;

   static private final String homeCommunityIdBase = "urn:oid:1.1.4567334.1.";
   static private int homeCommunityIdIncr = 1;

   static private String getNewHomeCommunityId() {
      return homeCommunityIdBase + homeCommunityIdIncr++ ;
   }

   static final List <TransactionType> incomingTransactions =
      Arrays.asList(TransactionType.XC_QUERY, TransactionType.XC_RETRIEVE);

   @Override
   public Simulator buildNew(SimManager simm, @SuppressWarnings("hiding") SimId newID,
                             boolean configureBase)
           throws Exception {
      this.newID = newID;
      ActorType actorType = ActorType.RESPONDING_GATEWAY;
      SimulatorConfig sc;
      if (configureBase) sc = configureBaseElements(actorType, newID);
      else sc = new SimulatorConfig();

      SimId simId = sc.getId();

      File codesFile = EnvSetting.getEnvSetting(simm.sessionId()).getCodesFile();
      addEditableConfig(sc, SimulatorProperties.codesEnvironment,
         ParamType.SELECTION, codesFile.toString());
      addEditableConfig(sc, SimulatorProperties.homeCommunityId, ParamType.TEXT,
         getNewHomeCommunityId());

      addFixedEndpoint(sc, SimulatorProperties.xcqEndpoint, actorType,
         TransactionType.XC_QUERY, false);
      addFixedEndpoint(sc, SimulatorProperties.xcqTlsEndpoint, actorType,
         TransactionType.XC_QUERY, true);
      addFixedEndpoint(sc, SimulatorProperties.xcrEndpoint, actorType,
         TransactionType.XC_RETRIEVE, false);
      addFixedEndpoint(sc, SimulatorProperties.xcrTlsEndpoint, actorType,
         TransactionType.XC_RETRIEVE, true);
      addEditableConfig(sc, SimulatorProperties.errors, ParamType.SELECTION,
         new ArrayList <String>(), false);
      addEditableConfig(sc, SimulatorProperties.errorForPatient,
         ParamType.SELECTION, new PatientErrorMap());
      addEditableConfig(sc, SimulatorProperties.repositories,
              ParamType.SELECTION, new ArrayList <String>(), true);


      // This needs to be grouped with a Document Registry
      RegistryActorFactory raf = new RegistryActorFactory();
      raf.setTransactionOnly(isTransactionOnly());
      SimulatorConfig registryConfig =
         raf.buildNew(simm, simId, true).getConfig(0); // was
                                                                              // false

      // This needs to be grouped with a Document Repository also
      SimulatorConfig repositoryConfig =
         new RepositoryActorFactory().buildNew(simm, simId, true).getConfig(0); // was
                                                                                // false
      sc.add(registryConfig); // this adds the individual
                              // SimulatorConfigElements to the RG
                              // SimulatorConfig
      // their identity as belonging to the Registry or Repository is lost
      // which means the SimServlet cannot find them when a message comes in
      sc.add(repositoryConfig);
      
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
         site.setTestSession(sc.getId().getTestSession()); // labels this site as coming from a sim

         boolean isAsync = false;

         site.addTransaction(new TransactionBean(
            TransactionType.XC_QUERY.getCode(), RepositoryType.NONE,
            sc.get(SimulatorProperties.xcqEndpoint).asString(), false,
            isAsync));
         site.addTransaction(new TransactionBean(
            TransactionType.XC_QUERY.getCode(), RepositoryType.NONE,
            sc.get(SimulatorProperties.xcqTlsEndpoint).asString(), true,
            isAsync));

         site.addTransaction(new TransactionBean(
            TransactionType.XC_RETRIEVE.getCode(), RepositoryType.NONE,
            sc.get(SimulatorProperties.xcrEndpoint).asString(), false,
            isAsync));
         site.addTransaction(new TransactionBean(
            TransactionType.XC_RETRIEVE.getCode(), RepositoryType.NONE,
            sc.get(SimulatorProperties.xcrTlsEndpoint).asString(), true,
            isAsync));

         site.setHome(sc.get(SimulatorProperties.homeCommunityId).asString());

         site = new RegistryActorFactory().getActorSite(sc, site);
         site = new RepositoryActorFactory().getActorSite(sc, site);

         return site;
      } catch (Throwable t) {
         sc.isExpired(true);
         logger.error(ExceptionUtil.exception_details(t));
         throw new NoSimulatorException("Not Defined", t);
      }
   }

   @Override
   public List <TransactionType> getIncomingTransactions() {
      List <TransactionType> tt = new ArrayList <TransactionType>();
      tt.addAll(incomingTransactions);
      tt.addAll(new RegistryActorFactory().getIncomingTransactions());
      tt.addAll(new RepositoryActorFactory().getIncomingTransactions());
      return tt;
   }

   @Override
   public ActorType getActorType() {
      return ActorType.RESPONDING_GATEWAY;
   }
}
