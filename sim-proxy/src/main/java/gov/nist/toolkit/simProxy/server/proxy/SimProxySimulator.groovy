package gov.nist.toolkit.simProxy.server.proxy

import gov.nist.toolkit.actortransaction.EndpointParser
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.BaseActorSimulator
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import org.apache.log4j.Logger

import javax.servlet.http.HttpServletResponse
/**
 *
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

        String endpoint = config.getConfigEle(SimulatorProperties.proxyForwardEndpoint).asString()
        EndpointParser eparser = new EndpointParser(endpoint)

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

        // Transformation goes here and alters outputHeaders and outputBody



        // end of transformation



        db2.putRequestHeaderFile(outputHeaders.bytes)
        db2.putRequestBodyFile(outputBody)

        post.getOutputStream().write(outputBody)
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
