package gov.nist.toolkit.simcoresupport.proxy.util

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ProxyTransformConfig
import gov.nist.toolkit.actortransaction.client.TransactionDirection
import gov.nist.toolkit.actortransaction.server.EndpointParser
import gov.nist.toolkit.configDatatypes.client.FhirVerb
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.fhir.server.utility.UriBuilder
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.simcommon.client.BadSimIdException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimIdFactory
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimEndpoint
import gov.nist.toolkit.simcommon.server.SiteFactory
import gov.nist.toolkit.simcommon.server.factories.SimProxyFactory
import gov.nist.toolkit.simcoresupport.mhd.CodeTranslator
import gov.nist.toolkit.simcoresupport.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.valsupport.client.ValidationContext
import groovy.transform.TypeChecked
import org.apache.http.Header
import org.apache.http.HttpHost
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Resource

/**
 *
 */
@TypeChecked
public class SimProxyBase {
    static final String fhirSupportSimName = 'fhir_support'
    static Logger logger = Logger.getLogger(SimProxyBase.class);


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
    CodeTranslator codeTranslator
    String clientAddress = null

    SimProxyBase() {
        ValidationContext vc = new ValidationContext()
        vc.codesFilename = Installation.instance().getDefaultCodesFile().toString()
        codeTranslator = new CodeTranslator(vc)
    }

    String fhirSupportBase() {
        def userName = simId.testSession
        SimId supportId = new SimId(userName, fhirSupportSimName)
        SimDb simDb = new SimDb(supportId)
        SimulatorConfig config = simDb.getSimulator(supportId)
        config.getConfigEle(SimulatorProperties.fhirEndpoint).asString()
    }

    String getClientAddress() {
        return clientAddress
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

        simDb2 = new SimDb(simId2, targetActorType, targetTransactionType, false)
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
        logger.info("Request on uri ${uri}")
        logger.info("SimEndpoint parsed as ${endpoint}")
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
        logger.info("SimId parsed from URI as ${simId}")
        simDb = new SimDb(simId, endpoint.actorType, clientTransactionType.shortName, false)
        config = simDb.getSimulator(simId);
        if (config == null) throw new BadSimIdException("Simulator " + simId +  " does not exist");

        proxySite = new SimProxyFactory().getActorSite(config, SiteFactory.buildSite(simId));

        SimulatorConfigElement ele = config.getConfigEle(SimulatorProperties.proxyPartner);
        if (ele == null) throw new Exception("SimProxy " + simId + " has no backend sim (connection to target system)");

        simId2 = SimIdFactory.simIdBuilder(ele.asString());

        transformConfigs = []
        SimulatorConfigElement sce = config.get(SimulatorProperties.simProxyTransformations)
        if (sce != null) {
            List<String> transforms = sce.asList()
            transformConfigs = transforms.collect { String xfrm ->
                ProxyTransformConfig.parse(xfrm)
            }
        }

//        transformConfigs =
//        config.get(SimulatorProperties.simProxyTransformations)?.asList()?.collect { String sce ->
//            ProxyTransformConfig.parse(sce)
//        }



        List<Header> contentTypeHeaders = request.getHeaders('Accept-Encoding') as List
        contentTypeHeaders.each { Header h ->
            String types = h.value
            types.split(';').each { String type ->
                if (type.contains(':'))
                    type = type.split(':')[1].trim()
                clientContentTypes << type
            }
        }

        String targetSiteName = config.get(SimulatorProperties.proxyForwardSite)?.asString()
        SimId targetSiteSimId = SimIdFactory.simIdBuilder(targetSiteName)
        if (!targetSiteName) return handleEarlyException(new Exception("Proxy forward site not configured"))
        targetSite = SimCache.getSite(targetSiteName, targetSiteSimId.testSession)
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
        clientContentTypes.find { String it -> it.contains('*')}
    }

}
