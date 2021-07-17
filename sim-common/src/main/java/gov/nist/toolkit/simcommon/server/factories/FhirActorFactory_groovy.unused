package gov.nist.toolkit.simcommon.server.factories

import gov.nist.toolkit.actortransaction.shared.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.Simulator
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.server.AbstractActorFactory
import gov.nist.toolkit.simcommon.server.IActorFactory
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.xdsexception.NoSimulatorException
import groovy.transform.TypeChecked

/**
 *
 */
@TypeChecked
class FhirActorFactory extends AbstractActorFactory implements IActorFactory {

    static final List<TransactionType> incomingTransactions =
            Arrays.asList(
                    TransactionType.FHIR
            );


    @Override
    protected Simulator buildNew(SimManager simm, SimId simId, String environment, boolean configureBase) throws Exception {
        ActorType actorType = ActorType.FHIR_SERVER
        SimulatorConfig sc
        if (configureBase)
            sc = configureBaseElements(actorType, simId, simId.testSession, environment)
        else
            sc = new SimulatorConfig()

        addFixedFhirEndpoint(sc, SimulatorProperties.fhirEndpoint, actorType, TransactionType.FHIR, false)
        //addFixedFhirEndpoint(sc, SimulatorProperties.fhirTlsEndpoint, actorType, TransactionType.FHIR, true)


        return new Simulator(sc)
    }

    @Override
    protected void verifyActorConfigurationOptions(SimulatorConfig config) {

    }

    @Override
    Site buildActorSite(SimulatorConfig asc, Site site) throws NoSimulatorException {
        String siteName = asc.getDefaultName()

        if (site == null)
            site = new Site(siteName, asc.id.testSession)

        boolean isAsync = false

        site.addTransaction(new TransactionBean(
                TransactionType.FHIR.getCode(),
                TransactionBean.RepositoryType.NONE,
                asc.get(SimulatorProperties.fhirEndpoint).asString(),
                false,
                isAsync));
//        site.addTransaction(new TransactionBean(
//                TransactionType.FHIR.getCode(),
//                TransactionBean.RepositoryType.NONE,
//                asc.get(SimulatorProperties.fhirTlsEndpoint).asString(),
//                true,
//                isAsync));

        return site
    }

    @Override
    List<TransactionType> getIncomingTransactions() {
        return incomingTransactions
    }

    @Override
    ActorType getActorType() {
        return ActorType.FHIR_SERVER
    }
}
