package gov.nist.toolkit.simulators.proxy.util

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.simcommon.client.BadSimIdException
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimEndpoint
import gov.nist.toolkit.simulators.proxy.exceptions.SimProxyTransformException
import gov.nist.toolkit.simulators.proxy.sim.SimProxyFactory
import gov.nist.toolkit.sitemanagement.client.Site
import org.apache.http.Header
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse
import org.hl7.fhir.dstu3.model.Resource

/**
 *
 */
public class SimProxyBase {
    String uri
     SimId simId;
     SimId simId2;
     SimDb simDb;
     SimDb simDb2;
     SimulatorConfig config;
     SimulatorConfig config2;
     List<String> requestTransformClassNames;
    List<String> responseTransformClassNames;
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

    /**
     * called by the first transform when something is known about the target system transaction
     * @param actorType
     * @param transactionType
     */
    def setTargetType(ActorType actorType, TransactionType transactionType) {
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
        return targetSite.getEndpoint(targetTransactionType, isSecure(), false)
    }

    HttpRequest preProcessRequest(HttpRequest request) {
        return runRequestTransforms(request)
    }

    HttpResponse preProcessResponse(HttpResponse response) {
        return runResponseTransforms(response)
    }

    HttpRequest runRequestTransforms(HttpRequest request) {
        requestTransformClassNames.each { String className ->
            assert className
            def instance = Class.forName(className).newInstance()
            if (!(instance instanceof SimpleRequestTransform))
                throw new SimProxyTransformException("Proxy Transform named ${className} cannot be created.")

            request = ((SimpleRequestTransform) instance).run(this, request)
            assert request, "${className} returned null request"
        }
        return request
    }

    HttpResponse runResponseTransforms(HttpResponse response) {
        responseTransformClassNames.each { String className ->
            assert className
            def instance = Class.forName(className).newInstance()
            if (!(instance instanceof SimpleResponseTransform))
                throw new SimProxyTransformException("Proxy Transform named ${className} cannot be created.")

            response = ((SimpleResponseTransform) instance).run(this, response)
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

    def init(HttpRequest request) {
        uri = request.requestLine.uri
        endpoint = new SimEndpoint(uri)
        clientActorType = ActorType.findActor(endpoint.actorType)
        assert clientActorType
        clientTransactionType = TransactionType.find(endpoint.transactionType)
        assert clientTransactionType
        simId = SimIdParser.parse(uri)
        simDb = new SimDb(simId, endpoint.actorType, endpoint.transactionType)
        config = simDb.getSimulator(simId);
        if (config == null) throw new BadSimIdException("Simulator " + simId +  " does not exist");

        proxySite = new SimProxyFactory().getActorSite(config, new Site());

        SimulatorConfigElement ele = config.getConfigEle(SimulatorProperties.proxyPartner);
        if (ele == null) throw new Exception("SimProxy " + simId + " has no backend sim (connection to target system)");

        simId2 = new SimId(ele.asString());

        requestTransformClassNames = config.get(SimulatorProperties.simProxyRequestTransformations)?.asList();
        responseTransformClassNames = config.get(SimulatorProperties.simProxyResponseTransformations)?.asList();

        Header contentTypeHeader = request.getFirstHeader('Content-Type')
        clientContentType = contentTypeHeader.value

        String targetSiteName = config.get(SimulatorProperties.proxyForwardSite)?.asString()
        assert targetSiteName, "Proxy forward site not configured"
        targetSite = SimCache.getSite(targetSiteName)
        assert targetSite, "Site ${targetSiteName} does not exist"
        return new ProxyLogger(simDb)
    }

}
