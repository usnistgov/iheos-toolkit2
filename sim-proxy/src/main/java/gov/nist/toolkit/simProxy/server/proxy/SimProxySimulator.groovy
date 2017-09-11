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

    boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        SimId simId2 = new SimId(config.getConfigEle(SimulatorProperties.proxyPartner).asString())
        String actor = db.actor
        String transaction = db.transaction
        SimDb db2 = new SimDb(simId2)
        db2.mirrorEvent(db, actor, transaction)

        // db is front side of proxy
        // db2 is back side of proxy

        MyHeaders headers = parseHeaders(db.getRequestMessageHeader())

        deleteChunkedHeader(headers)

        Site forwardSite = lookupForwardSite()

        def transformClassNames = config.get(SimulatorProperties.simProxyTransformations).asList()

        StringBuilder headerBuilder = new StringBuilder()

        headers.props.each { String key, value ->
            if (key) { // status line will be posted with null key
                headerBuilder.append(key).append(': ').append(value).append('\r\n')  // save for logs
            }
        }
        String transformInHeaders = headerBuilder.toString()
        byte[] transformInBody = db.getRequestMessageBody()
        String bodyString = new String(transformInBody)

        def (transformOutHeaders, transformOutBody, forwardTransactionType) = processTransformations(transformClassNames, transformInHeaders, transformInBody)

        if (!forwardTransactionType)
            forwardTransactionType = transactionType

        // find endpoint to forward message to
        def (EndpointParser eparser, String endpoint) = lookupForwardEndpoint(forwardSite, forwardTransactionType)
        headerBuilder.insert(0, "POST ${eparser.service} HTTP/1.1\r\n")

        // log host name that we are forwarding to
        db2.setClientIpAddess(eparser.host)


        def post = new URL(endpoint).openConnection()
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        headers.props.each { String key, value ->
            if (key) { // status line will be posted with null key
                post.setRequestProperty(key, value)    // add to outgoing message
            }
        }


        db2.putRequestHeaderFile(transformOutHeaders.bytes)
        db2.putRequestBodyFile(transformOutBody)

        post.getOutputStream().write(transformOutBody)
        def responseCode = post.getResponseCode()

        HttpServletResponse responseToClient = common.response
        responseToClient.setStatus(responseCode)

        StringBuilder responseToClientHeaders = new StringBuilder()
        Map<String, List<String>> hdrs = post.getHeaderFields()
        hdrs.each { String name, List<String> values ->
            String value = values.join('; ')
            if (name) {
                responseToClient.addHeader(name, value)
                responseToClientHeaders.append("${name}: ${value}\r\n")
            } else {
                responseToClientHeaders.append("${value}\r\n")
            }
        }
        String returnHeaders = responseToClientHeaders.toString()
        db2.getResponseHdrFile().text = returnHeaders
        db.getResponseHdrFile().text = returnHeaders

        if (responseCode < 300) {
            byte[] responseBytes = post.getInputStream().bytes
            responseToClient.getOutputStream().write(responseBytes)
            responseToClient.getOutputStream().close()
            db2.putResponseBody(new String(responseBytes))
            db.putResponseBody(new String(responseBytes))
        }

        return false
    }

    def deleteChunkedHeader(MyHeaders headers) {
        String encoding = headers.get('transfer-encoding')
        if (encoding && 'chunked' == encoding.toLowerCase())
            headers.remove('transfer-encoding')
    }

    Site lookupForwardSite() {
        String forwardSiteName = config.getConfigEle(SimulatorProperties.proxyForwardSite)?.asString()
        if (!forwardSiteName) {
            def msg = 'No Proxy forward system configured'
            Exception e = new XdsInternalException(msg)
            common.getCommonErrorRecorder().err(XdsErrorCode.Code.NoCode, e)
            throw e
        }
        Site forwardSite = findForwardSite(forwardSiteName)
        if (!forwardSite) {
            def msg = "Proxy configured to forward to System ${forwardSiteName} which does not exist"
            Exception e = new XdsInternalException(msg)
            common.getCommonErrorRecorder().err(XdsErrorCode.Code.NoCode, e)
            throw e
        }
        return forwardSite
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
        assert transformInHeader
        assert transformInBody
        TransactionType forwardTransactionType = null

        transformClassNames?.each { String transformClassName ->
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

        assert transformInHeader
        assert transformInBody
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

    MyHeaders parseHeaders(String rawHeaders) {
        MyHeaders headers = new MyHeaders()
        RequestLine requestLine = null
        // grab headers
        rawHeaders.eachLine {String line ->
            line = strip(line)
            if (requestLine) {
                if (line)
                    headers.add(line)
            }
            else
                requestLine = new RequestLine(line)
        }
        return headers
    }

    def strip(String line) {
        line = line.trim()
        while (line.size() > 0 && (line.endsWith('\r') || line.endsWith('\n')) )
            line = line.substring(0, line.size() - 1)
        return line
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
