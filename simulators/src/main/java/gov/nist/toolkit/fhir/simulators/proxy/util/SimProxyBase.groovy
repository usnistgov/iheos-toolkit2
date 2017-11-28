package gov.nist.toolkit.fhir.simulators.proxy.util

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.client.ProxyTransformConfig
import gov.nist.toolkit.actortransaction.client.TransactionDirection
import gov.nist.toolkit.actortransaction.server.EndpointParser
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.fhir.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.fhir.simulators.proxy.sim.SimProxyFactory
import gov.nist.toolkit.simcommon.client.BadSimIdException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimEndpoint
import gov.nist.toolkit.sitemanagement.client.Site
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
     SimId simId;
     SimId simId2;
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
    String clientContentType
    List<Resource> resourcesSubmitted = []

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
        return targetSite.getEndpoint(targetTransactionType, isSecure, false)
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
        assert transformsRun.size() > 0, "SimProxyBase#runRequestTransform: none of the input transforms declared the targetTransaction."
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
       // if (serverConnection)
       //     this.serverConnection = serverConnection
        SimEndpoint endpoint = new SimEndpoint(uri)
        clientActorType = ActorType.findActor(endpoint.actorType)
        if (!clientActorType) return handleEarlyException(new Exception("ActorType name was ${endpoint.actorType}"))
        clientTransactionType = TransactionType.find(endpoint.transactionType)
        if (!clientTransactionType && clientActorType.transactions.size() == 1)
            clientTransactionType = clientActorType.transactions[0]
        if (!clientTransactionType) return handleEarlyException(new Exception("TransactionType name was ${endpoint.transactionType}"))
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
//        transformConfigs = ProxyTransformConfig.parse();
//        responseTransformClassNames = config.get(SimulatorProperties.simProxyResponseTransformations)?.asList();

        Header contentTypeHeader = request.getFirstHeader('Content-Type')
        clientContentType = contentTypeHeader.value

        String targetSiteName = config.get(SimulatorProperties.proxyForwardSite)?.asString()
        if (!targetSiteName) return handleEarlyException(new Exception("Proxy forward site not configured"))
        targetSite = SimCache.getSite(targetSiteName)
        if (!targetSite) return handleEarlyException(new Exception("Site ${targetSiteName} does not exist"))
        return null
        //return new ProxyLogger(simDb)
    }

    Exception handleEarlyException(Exception e) {
        earlyException = e
//        if (clientTransactionType.isFhir()) {
//            BasicHttpResponse response = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion('http', 1, 1), 500, e.getMessage()))
//            OutputStream outstream = serverConnection.prepareOutputStream(response)
//            outstream.write(response.toString().getBytes())
//            outstream.flush()
//            outstream.close()
//            return e
//        }
//        assert true, "handleEarlyException - non-FHIR exceptions not implemented"
    }



}
