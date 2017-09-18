package gov.nist.toolkit.simulators.proxy.util;

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.actortransaction.server.AbstractProxyTransform;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.simcommon.client.BadSimIdException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.simcommon.server.SimCache;
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimEndpoint;
import gov.nist.toolkit.simulators.proxy.sim.SimProxyFactory;
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.http.HttpRequest
import org.apache.http.HttpResponse

/**
 *
 */
public class SimProxyBase {
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
    ActorType serverActorType
    TransactionType serverTransactionType
    SimEndpoint endpoint
    Site targetSite

    public SimProxyBase(String url) throws Exception {
        endpoint = new SimEndpoint(url)
        clientActorType = ActorType.findActor(endpoint.actorType)
        assert clientActorType
        clientTransactionType = TransactionType.find(endpoint.transactionType)
        assert clientTransactionType

        SimId simId = SimIdParser.parse(url);
        this.simId = simId;
        // no new transaction - setTargetType() called first and already did that
        simDb = new SimDb(simId, clientActorType, clientTransactionType, true);
        config = simDb.getSimulator(simId);
        if (config == null) throw new BadSimIdException("Simulator " + simId +  " does not exist");

        proxySite = new SimProxyFactory().getActorSite(config, new Site());

        SimulatorConfigElement ele = config.getConfigEle(SimulatorProperties.proxyPartner);
        if (ele == null) throw new Exception("SimProxy " + simId + " has no backend sim (connection to target system)");

        simId2 = new SimId(ele.asString());

        requestTransformClassNames = config.get(SimulatorProperties.simProxyRequestTransformations)?.asList();
        responseTransformClassNames = config.get(SimulatorProperties.simProxyResponseTransformations)?.asList();

        String targetSiteName = config.get(SimulatorProperties.proxyForwardSite)?.asString()
        assert targetSiteName, "Proxy forward site not configured"
        targetSite = SimCache.getSite(targetSiteName)
        assert targetSite, "Site ${targetSiteName} does not exist"
    }

    def setTargetType(ActorType actorType, TransactionType transactionType) {
        serverActorType = actorType
        serverTransactionType = transactionType

        simDb2 = new SimDb(simId2, serverActorType, serverTransactionType)
        config2 = simDb.getSimulator(simId2);
        if (config2 == null) throw new BadSimIdException("Simulator " + simId2 +  " does not exist");
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
            if (!(instance instanceof RequestTransform))
                throw new SimProxyTransformException("Proxy Transform named ${className} cannot be created.")

            request = ((RequestTransform) instance).run(this, request)
        }
        return request
    }

    HttpResponse runResponseTransforms(HttpResponse response) {
        responseTransformClassNames.each { String className ->
            assert className
            def instance = Class.forName(className).newInstance()
            if (!(instance instanceof ResponseTransform))
                throw new SimProxyTransformException("Proxy Transform named ${className} cannot be created.")

            response = ((ResponseTransform) instance).run(this, response)
        }
        return response
    }

    boolean isSecure() { return endpoint.schemeName == 'https'}

    ProxyLogger getClientLogger() {
        return new ProxyLogger(simDb);
    }

    ProxyLogger getTargetLogger() {
        return new ProxyLogger(simDb2);
    }

    static ProxyLogger getProxyLoggerForRequest(HttpRequest request) {
        String uri = request.requestLine.uri
        SimEndpoint endpoint = new SimEndpoint(uri)
        SimId simId = SimIdParser.parse(uri)
        SimDb simDb = new SimDb(simId, endpoint.actorType, endpoint.transactionType)
        return new ProxyLogger(simDb)
    }

}
