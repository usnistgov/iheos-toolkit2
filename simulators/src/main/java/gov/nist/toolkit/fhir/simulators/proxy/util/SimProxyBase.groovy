package gov.nist.toolkit.fhir.simulators.proxy.util

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ProxyTransformConfig
import gov.nist.toolkit.actortransaction.client.TransactionDirection
import gov.nist.toolkit.actortransaction.server.EndpointParser
import gov.nist.toolkit.configDatatypes.client.FhirVerb
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.simcommon.server.factories.SimProxyFactory
import gov.nist.toolkit.fhir.utility.UriBuilder
import gov.nist.toolkit.simcommon.client.BadSimIdException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimEndpoint
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import org.apache.http.Header
import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.hl7.fhir.dstu3.model.Resource
/**
 *
 */
public class SimProxyBase {
    static final String fhirSupportSimName = 'fhir_support'


    String uri = null
     SimId simId;   // mhd doc rec
     SimId simId2;  // reg/rep sim
     SimDb simDb;
     SimDb simDb2;
     SimulatorConfig config;
     SimulatorConfig config2;
     List<ProxyTransformConfig> transformConfigs;
//    List<String> responseTransformClassNames;
     Site proxySite
     ActorType clientActorType;
     TransactionType clientTransactionType;
    ActorType targetActorType
    TransactionType targetTransactionType
    SimEndpoint endpoint
    Site targetSite
    ProxyLogger clientLogger = null
    ProxyLogger targetLogger = null
    List<String> clientContentTypes = []
    List<Resource> resourcesSubmitted = []
    FhirVerb fhirVerb

    String fhirSupportBase() {
        def userName = simId.user
        SimId supportId = new SimId(userName, fhirSupportSimName)
        SimDb simDb = new SimDb(supportId)
        SimulatorConfig config = simDb.getSimulator(supportId)
        config.getConfigEle(SimulatorProperties.fhirEndpoint).asString()
    }


    /**
     * called by the first transform when something is known about the target system transaction
     * @param actorType
     * @param transactionType
     */
    def setTargetType(ActorType actorType, TransactionType transactionType) {
        assert actorType
        assert transactionType
        targetActorType = actorType
        targetTransactionType = transactionType

        simDb2 = new SimDb(simId2, targetActorType, targetTransactionType)
        config2 = simDb.getSimulator(simId2);
        if (config2 == null) throw new BadSimIdException("Simulator " + simId2 +  " does not exist");
    }

    /**
     * can only be called after setTargetType()
     * @return
     */
    String getTargetEndpoint() {
        assert targetSite
        assert targetTransactionType
        boolean isSecure = false
        if (targetTransactionType == TransactionType.RETRIEVE) {
            def reposId = targetSite.getRepositoryUniqueId(TransactionBean.RepositoryType.REPOSITORY)
            return targetSite.getRetrieveEndpoint(reposId, false, false)
        }
        return targetSite.getEndpoint(targetTransactionType, isSecure, false)
    }

    String getTargetRepositoryUniqueId() {
        if (targetTransactionType == TransactionType.RETRIEVE) {
            return targetSite.getRepositoryUniqueId(TransactionBean.RepositoryType.REPOSITORY)
        }
        return null
    }

    HttpHost getTargetHost() {
        return new EndpointParser(getTargetEndpoint()).getHttpHost()
    }

    HttpRequest preProcessRequest(HttpRequest request)  throws ReturnableErrorException {
        return runRequestTransforms(request)
    }

    HttpResponse preProcessResponse(HttpResponse response) {
        return runResponseTransforms(response)
    }

    HttpRequest runRequestTransforms(HttpRequest request) throws ReturnableErrorException {
        def transformsRun = []
        transformConfigs.each { ProxyTransformConfig config ->
            assert config
            if (config.transactionDirection == TransactionDirection.REQUEST && config.transactionType == clientTransactionType) {
                def className = config.transformClassName
                transformsRun << className
                def instance = Class.forName(className).newInstance()
                if (!(instance instanceof SimpleRequestTransform))
                    throw new SimProxyTransformException("Proxy Transform named ${className} cannot be created.")

                request = ((SimpleRequestTransform) instance).run(this, request)
                assert request, "${className} returned null request"
            }

        }
        assert transformsRun.size() > 0, "SimProxyBase#runRequestTransform: none of the input transforms declared the clientTransaction ${clientTransactionType}"
        return request
    }

    HttpResponse runResponseTransforms(HttpResponse response) {
        transformConfigs.each { ProxyTransformConfig config ->
            assert config
            if (config.transactionDirection == TransactionDirection.RESPONSE && config.transactionType == clientTransactionType) {
                def className = config.transformClassName
                def instance = Class.forName(className).newInstance()
                if (!(instance instanceof SimpleResponseTransform))
                    throw new SimProxyTransformException("Proxy Transform named ${className} cannot be created.")

                response = ((SimpleResponseTransform) instance).run(this, response)
            }
        }
        return response
    }

    boolean isSecure() { return endpoint.schemeName == 'https'}


    ProxyLogger getClientLogger() {
        if (clientLogger) return clientLogger
        clientLogger = new ProxyLogger(simDb);
        return clientLogger
    }

    ProxyLogger getTargetLogger() {
        if (targetLogger) return targetLogger
        targetLogger = new ProxyLogger(simDb2);
        return targetLogger
    }

    //ServerConnection serverConnection
    Exception earlyException = null

    def init(HttpRequest request) {
        if (uri) return
        uri = request.requestLine.uri
        endpoint = new SimEndpoint(uri)
        clientActorType = ActorType.findActor(endpoint.actorType)
        if (!clientActorType) return handleEarlyException(new Exception("ActorType name was ${endpoint.actorType}"))
        if (endpoint.transactionType) {
            clientTransactionType = endpoint.transactionType
            fhirVerb = endpoint.fhirVerb
        }
        URI urix = UriBuilder.build(uri)
//        if (urix.query) {  // to distringuish query from read
//            clientTransactionType = TransactionType.find(endpoint.transactionTypeName)
//            fhirVerb = FhirVerb.QUERY
//        }
//        if (!clientTransactionType) {
//            clientTransactionType = (clientActorType.transactions.contains(endpoint.transactionType)) ?  endpoint.transactionType : null
//        }
//        if (!clientTransactionType && clientActorType.transactions.contains(TransactionType.FHIR))
//            clientTransactionType = TransactionType.FHIR  // probably a read
        if (!clientTransactionType) return handleEarlyException(new Exception("TransactionType name was ${endpoint.transactionTypeName}"))
        simId = SimIdParser.parse(uri)
        simDb = new SimDb(simId, endpoint.actorType, clientTransactionType.shortName)
        config = simDb.getSimulator(simId);
        if (config == null) throw new BadSimIdException("Simulator " + simId +  " does not exist");

        proxySite = new SimProxyFactory().getActorSite(config, new Site());

        SimulatorConfigElement ele = config.getConfigEle(SimulatorProperties.proxyPartner);
        if (ele == null) throw new Exception("SimProxy " + simId + " has no backend sim (connection to target system)");

        simId2 = new SimId(ele.asString());

        transformConfigs =
        config.get(SimulatorProperties.simProxyTransformations)?.asList()?.collect {
            ProxyTransformConfig.parse(it)
        }
        List<Header> contentTypeHeaders = request.getHeaders('Accept')
        contentTypeHeaders.each { Header h ->
            clientContentTypes << h.value
        }

        String targetSiteName = config.get(SimulatorProperties.proxyForwardSite)?.asString()
        if (!targetSiteName) return handleEarlyException(new Exception("Proxy forward site not configured"))
        targetSite = SimCache.getSite(targetSiteName)
        if (!targetSite) return handleEarlyException(new Exception("Site ${targetSiteName} does not exist"))
        return null
    }

    Exception handleEarlyException(Exception e) {
        earlyException = e
    }

    static List<String> types = [
            'application/fhir+json',
            'application/fhir+xml'
    ]

    String chooseContentType() {
        if (clientContentTypes.empty)
            return 'application/fhir+json'
        def xx = types.intersect(clientContentTypes)
        if (xx) return xx[0]
        return 'application/fhir+json'
    }

    boolean isNonTradionalContentTypeRequested() {
        !clientContentTypes.empty && !types.contains(clientContentTypes[0])
    }

    String getRequestedContentType() {
        clientContentTypes[0]
    }

    boolean isRequestedContentTypeWildcarded() {
        clientContentTypes.find { it.contains('*')}
    }

}
