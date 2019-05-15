package gov.nist.toolkit.fhir.simulators.filterProxy;

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.FilterProxyProperties
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon
import gov.nist.toolkit.http.httpclient.HttpClient
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement
import gov.nist.toolkit.utilities.io.Io
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import groovy.transform.TypeChecked
import org.apache.http.Header
import org.apache.http.HeaderElement
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.log4j.Logger

@TypeChecked
class FilterProxySimulator extends BaseDsActorSimulator  {
    private static Logger logger = Logger.getLogger(FilterProxySimulator.class);

    protected ErrorRecorder er = null
    SimulatorConfig simulatorConfig = null

    boolean supports(TransactionType transactionType) {
        return true
    }

    FilterProxySimulator() {}

    FilterProxySimulator(DsSimCommon dsSimCommon, SimulatorConfig simulatorConfig) {
        super(dsSimCommon.simCommon, dsSimCommon);
        this.db = dsSimCommon.simCommon.db;
        this.response = dsSimCommon.simCommon.response;
        this.setSimulatorConfig(simulatorConfig);
        init();
    }

    void init() {

    }

    @Override
    boolean run(TransactionType transactionType /* ignored */, MessageValidatorEngine mvc, String validation) throws IOException {
        // re-send without using Axiom and Axis2

        simulatorConfig = dsSimCommon.simulatorConfig

        String header = dsSimCommon.simDb().getRequestMessageHeader()
        byte[] bodyBytes = dsSimCommon.simDb().getRequestMessageBody()
        String body = new String(bodyBytes)

        if (!transactionType.requiresMtom) {
            String inputName = transactionType.endpointSimPropertyName
            String relayName = FilterProxyProperties.getRelayEndpointName(inputName)
            SimulatorConfigElement ele = simulatorConfig.getConfigEle(relayName)
            String outgoingEndpoint = ele.asString()
            byte[] response = post(outgoingEndpoint, header, body)
            String responseStr = new String(response)
            String responseHdr = lastHeader
            if (dsSimCommon.simCommon.db) {
                dsSimCommon.simCommon.db.putResponseBody(response)
                dsSimCommon.simCommon.db.putResponseHeader(lastHeader)
            }
            if (dsSimCommon.simCommon.os) {
                dsSimCommon.simCommon.os.write(response)
            }
        } else  {

        }

        return false
    }

    static List<String> postHeadersToIgnore = [
            'content-length'
    ]

    String lastHeader = null

    byte[] post(String endpoint, String msgHeader, String msgBody) {
        org.apache.http.client.HttpClient httpclient = HttpClients.createDefault()
        HttpPost post = new HttpPost(endpoint)
        post.setEntity(new StringEntity(msgBody))
        msgHeader.split('\n').each { String headerLine ->
            headerLine = headerLine.trim()
            if (!headerLine.contains(':')) return
            String[] parts = headerLine.split(':')
            String name = parts[0].trim()
            String value = parts[1].trim()
            if (postHeadersToIgnore.contains(name.toLowerCase())) return
        }
        HttpResponse response = httpclient.execute(post)
        Header[] hs = response.allHeaders
        StringBuilder buf = new StringBuilder()
        response.allHeaders.each { Header h ->
            String name = h.name.trim()
            if (postHeadersToIgnore.contains(name.toLowerCase())) return
            String value = h.value.trim()
            buf.append("${name}: \"${value}\"")
//            buf.append(name).append(': ')
//            buf.append("\"")
//            buf.append(value)
//            buf.append("\"")
//            h.getElements().each { HeaderElement he ->
//                String ename = he.name
//                String evalue = he.value
//                buf.append('; ').append(ename).append('=').append('"').append(evalue).append('"')
//            }
            buf.append('\n')
        }
        lastHeader = buf.toString()
        HttpEntity returnEntity = response.entity
        InputStream is = returnEntity.content
        is.bytes
    }

    // remove namespace from Id attrubute in Timestamp element
    static String transform(String str) {
        int startOfHeader = str.indexOf('Header')
        if (startOfHeader == -1) return str    // cannot transform
        int endOfHeader = str.indexOf('Header>', startOfHeader + 1)
        if (endOfHeader == -1) return str

        int startOfSecurity = str.indexOf('Security', startOfHeader)
        if (startOfSecurity == -1) return str
        int endOfSecurity = str.indexOf('Security>', startOfSecurity + 1)
        if (endOfSecurity == -1) return str
        if (endOfSecurity > endOfHeader) return str

        int startOfTimestamp = str.indexOf('Timestamp', startOfSecurity)
        if (startOfTimestamp == -1) return str
        int endOfTimestamp = str.indexOf('Timestamp>', startOfTimestamp + 1)
        if (endOfTimestamp == -1) return str
        if (endOfTimestamp > endOfSecurity) return str

        int idIndex = str.indexOf('Id', startOfTimestamp)
        if (idIndex == -1) return str
        if (idIndex > endOfTimestamp) return str

        boolean hasNamespace = idIndex > 0 && str[idIndex-1] == ':'
        if (!hasNamespace) return str
        int colonIndex = idIndex - 1
        int namespaceIndex = colonIndex - 1
        while (namespaceIndex > -1 && !isWhitespace(str[namespaceIndex])) {
            namespaceIndex--
        }
        namespaceIndex++ // don't delete preceeding white space
        if (namespaceIndex == -1) return str

        String toDelete = ''
        StringBuilder buf = new StringBuilder(str)
        for (int i=namespaceIndex; i<=colonIndex; i++) {
            toDelete = toDelete + buf[namespaceIndex]
            buf.deleteCharAt(namespaceIndex)
        }
        return buf.toString()
    }

    static boolean isWhitespace(String a) {
        a == ' ' || a == '\n' || a == '\r' || a == '\t'
    }

}
