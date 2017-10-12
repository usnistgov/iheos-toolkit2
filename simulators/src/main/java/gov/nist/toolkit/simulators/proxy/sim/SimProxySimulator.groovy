package gov.nist.toolkit.simulators.proxy.sim

import gov.nist.toolkit.actortransaction.server.EndpointParser
import gov.nist.toolkit.actortransaction.server.AbstractProxyTransform
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.errorrecording.client.XdsErrorCode
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.BaseActorSimulator
import gov.nist.toolkit.simcommon.server.SimCache
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimManager
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.xdsexception.client.XdsInternalException
import org.apache.http.HttpHeaders
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
        boolean isClientFhir = transactionType.isFhir()
        SimId simId2 = new SimId(config.getConfigEle(SimulatorProperties.proxyPartner).asString())
        String actor = db.actor
        String transaction = db.transaction
        SimDb db2 = new SimDb(simId2)
        db2.mirrorEvent(db, actor, transaction)

        // db is front side of proxy
        // db2 is back side of proxy

        // input message was automatically logged by sim infrastructure
        String hdrs1 = db.getRequestMessageHeader()
        HttpHeaders inHeaders = parseHeaders(db.getRequestMessageHeader())
        String inBody = new String(db.getRequestMessageBody())

        deleteChunkedHeader(inHeaders)

        Site forwardSite = lookupForwardSite()

        def transformClassNames = config.get(SimulatorProperties.simProxyRequestTransformations).asList()


        def (HttpHeaders transformOutHeaders, transformOutBody, forwardTransactionType) = processTransformations(transformClassNames, inHeaders, inBody)

        if (!forwardTransactionType)
            forwardTransactionType = transactionType

        boolean isServerFhir = forwardTransactionType.isFhir()

        // find endpoint to forward message to
        def (EndpointParser eparser, String endpoint) = lookupForwardEndpoint(forwardSite, forwardTransactionType)
        String serverCmd = "POST ${eparser.service} HTTP/1.1\r\n"

        // log host name that we are forwarding to
        db2.setClientIpAddess(eparser.host)

        db2.putRequestHeaderFile(transformOutHeaders.bytes)
        db2.putRequestBodyFile(transformOutBody.bytes)

        // Forward to downstream system
        def post = new URL(endpoint).openConnection()
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        headers.props.each { String key, values ->
            if (key) { // status line will be posted with null key
                post.setRequestProperty(key, values.join(';'))    // add to outgoing message
            }
        }

        post.getOutputStream().write(transformOutBody.bytes)
        def responseCode = post.getResponseCode()

        // handle response

        // TODO - remove this later
        assert (200..299).contains(responseCode), "POST to ${endpoint} returned code ${responseCode}"

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
            String responseBody = new String(responseBytes)
            db2.putResponseBody(responseBody)
            db.putResponseBody(new String(responseBytes))

            responseToClient.getOutputStream().write(responseBytes)
            responseToClient.getOutputStream().close()
        }

        return false
    }

    def deleteChunkedHeader(HttpHeaders headers) {
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
    List processTransformations(transformClassNames, HttpHeaders transformInHeader, String transformInBody) {
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

    HttpHeaders parseHeaders(String rawHeaders) {
        HttpHeaders headers = new HttpHeaders()
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
