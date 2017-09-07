package gov.nist.toolkit.simProxy.server.proxy

import gov.nist.toolkit.actortransaction.EndpointParser
import gov.nist.toolkit.actortransaction.server.AbstractProxyTransform
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.client.XdsErrorCode
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.BaseActorSimulator
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.log4j.Logger

import javax.servlet.http.HttpServletResponse
/**
 * Gazelle has good example of terminology and display at
 * https://gazelle.ihe.net/proxy/messages/http.seam?id=1808249&conversationId=35
 */
class SimProxySimulator extends BaseActorSimulator {
    private static Logger logger = Logger.getLogger(SimProxySimulator.class)
    static List<TransactionType> transactions = TransactionType.asList()

    SimProxySimulator() {}

    public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        SimId simId2 = new SimId(config.getConfigEle(SimulatorProperties.proxyPartner).asString())
        String actor = db.actor
        String transaction = db.transaction
        SimDb db2 = new SimDb(simId2)
        db2.mirrorEvent(db, actor, transaction)

        // db is front side of proxy
        // db2 is back side of proxy

        String rawHeader = db.getRequestMessageHeader()
        MyHeaders headers = new MyHeaders()
        RequestLine requestLine = null
        // grab headers
        rawHeader.eachLine {String line ->
            line = line.trim()
            if (requestLine) {
                if (line)
                    headers.add(line)
            }
            else
                requestLine = new RequestLine(line)
        }

        // delete chunked header if it exists
        String encoding = headers.get('transfer-encoding')
        if (encoding && 'chunked' == encoding.toLowerCase())
            headers.remove('transfer-encoding')

//        String endpoint = config.getConfigEle(SimulatorProperties.proxyForwardEndpoint).asString()

//        String endpointConfigElementName = transactionType.getEndpointSimPropertyName()
//        if (!endpointConfigElementName) {
//            throw new XdsInternalException("Not configured to forward transaction type ${transactionType} to actor type ${actor} - see table TransactionType.java")
//        }
        String forwardSiteName = config.getConfigEle(SimulatorProperties.proxyForwardSite)?.asString()
        if (!forwardSiteName) {
            def msg = 'No Proxy forward system configured'
            Exception e = new XdsInternalException(msg)
            common.getCommonErrorRecorder().err(XdsErrorCode.Code.NoCode, e)
            throw e
        }
        Site site = findForwardSite(forwardSiteName)
        if (!site) {
            def msg = "Proxy configured to forward to System ${forwardSiteName} which does not exist"
            Exception e = new XdsInternalException(msg)
            common.getCommonErrorRecorder().err(XdsErrorCode.Code.NoCode, e)
            throw e
        }

        // find endpoint to forward message to
        def (EndpointParser eparser, String endpoint) = lookupForwardEndpoint(site, transactionType)

        // log host name that we are forwarding to
        db2.setClientIpAddess(eparser.host)

        StringBuilder outHeaders = new StringBuilder()

        outHeaders.append("POST ${eparser.service} HTTP/1.1\r\n")

        def post = new URL(endpoint).openConnection()
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        headers.props.each { String key, value ->
            if (key) { // status line will be posted with null key
                post.setRequestProperty(key, value)
                outHeaders.append(key).append(': ').append(value).append('\r\n')
            }
        }


        String outputHeaders = outHeaders.toString()
        byte[] outputBody = db.getRequestMessageBody()

        def transformClassNames = common.actorType.proxyTransformClassNames

        def (outHdrs, outBody, forwardTransactionType) = processTransformations(transformClassNames, outputHeaders, outputBody)

        db2.putRequestHeaderFile(outHdrs.bytes)
        db2.putRequestBodyFile(outBody)



        post.getOutputStream().write(outBody)
        def responseCode = post.getResponseCode()

        HttpServletResponse response = common.response
        response.setStatus(responseCode)

        StringBuilder inHeaders = new StringBuilder()
        Map<String, List<String>> hdrs = post.getHeaderFields()
        hdrs.each { String name, List<String> values ->
            String value = values.join('; ')
            if (name) {
                response.addHeader(name, value)
                inHeaders.append("${name}: ${value}\r\n")
            } else {
                inHeaders.append("${value}\r\n")
            }
        }
        String inputHeaders = inHeaders.toString()
        db2.getResponseHdrFile().text = inputHeaders
        db.getResponseHdrFile().text = inputHeaders
        if (responseCode < 300) {
            byte[] responseBytes = post.getInputStream().bytes
            response.getOutputStream().write(responseBytes)
            response.getOutputStream().close()
            db2.putResponseBody(new String(responseBytes))
            db.putResponseBody(new String(responseBytes))
        }

        return false
    }

    private List lookupForwardEndpoint(Site site, TransactionType transactionType) {
        String endpoint = null
        try {
            endpoint = site.getEndpoint(transactionType, false, false)
        } catch (Exception e) {

        }
        if (!endpoint) {
            def msg = "Proxy configured to forward to System ${site} which is not configured for Transaction type ${transactionType}"
            Exception e = new XdsInternalException(msg)
            common.getCommonErrorRecorder().err(XdsErrorCode.Code.NoCode, e)
            throw e
        }
        EndpointParser eparser = new EndpointParser(endpoint)
        [eparser, endpoint]
    }

    /**
     *
     * @param transformClassNames
     * @param transformInHeader
     * @param transformInBody
     * @return [ outHeader, outBody, forwardTransactionType]
     */
    List processTransformations(transformClassNames, String transformInHeader, byte[] transformInBody) {
        TransactionType forwardTransactionType = null

        common.actorType.proxyTransformClassNames?.each { String transformClassName ->
            assert transformClassName
            def instance = Class.forName(transformClassName).newInstance()
            if (!(instance instanceof AbstractProxyTransform)) {
                def msg = "Proxy Transform named ${transformClassName} cannot be created."
                Exception e = new XdsInternalException(msg)
                common.getCommonErrorRecorder().err(XdsErrorCode.Code.NoCode, e)
                throw e
            }

            AbstractProxyTransform transform = (AbstractProxyTransform) instance
            transform.inputHeader = transformInHeader
            transform.inputBody = transformInBody

            TransactionType tt = transform.run()
            if (tt) forwardTransactionType = tt

            // set up for next transform
            transformInHeader = transform.outputHeader
            transformInBody = transform.outputBody
        }
        return [transformInHeader, transformInBody, forwardTransactionType]

    }

    Site findForwardSite(forwardSiteName) {
        Site site = SimCache.getSite(forwardSiteName)
        if (site) return site
        // maybe site is a sim or even a FHIR sim
        SimId simId = new SimId(forwardSiteName)
        if (new SimDb().getSimulator(simId))
            return SimManager.getSite(simId)
        simId.forFhir()
        if (new SimDb().getSimulator(simId))
            return SimManager.getSite(simId)
        return null
    }


    class MyHeader {
        String name
        String value

        MyHeader(String h) {
            h = h.trim()
            int i = h.indexOf(':')
            if (i == -1) {
                name = h
                value = ""
            } else {
                name = h.substring(0, i)
                value = h.substring(i+1)
            }
        }
    }

    class MyHeaders {
        Properties props = new Properties()

        def add(String header) {
            MyHeader myHeader = new MyHeader(header)
            props.setProperty(myHeader.name, myHeader.value)
        }

        def get(String name) {
            String key = key(name)
            if (!key) return null
            return props.getProperty(key).trim()
        }

        def key(String name) {
            String lowerName = name.toLowerCase()
            String key = props.keySet().find { String akey ->
                String lowerKey = akey.toLowerCase()
                lowerKey == lowerName
            }
            key
        }

        def remove(String key) {
            props.remove(key)
        }
    }

    class RequestLine {
        def method
        def uri
        def httpversion

        RequestLine(String line) {
            String[] parts = line.trim().split(' ')
            method = parts[0]
            uri = parts[1]
            httpversion = parts[2]
        }
    }

}
