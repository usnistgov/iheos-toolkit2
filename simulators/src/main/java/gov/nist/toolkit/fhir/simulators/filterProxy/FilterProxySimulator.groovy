package gov.nist.toolkit.fhir.simulators.filterProxy;

import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.configDatatypes.server.FilterProxyProperties
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.envSetting.EnvSetting
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.fhir.simulators.support.BaseDsActorSimulator
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon
import gov.nist.toolkit.http.httpclient.HttpClient
import gov.nist.toolkit.installation.server.Installation
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
import org.apache.http.config.Registry
import org.apache.http.config.RegistryBuilder
import org.apache.http.conn.socket.ConnectionSocketFactory
import org.apache.http.conn.socket.PlainConnectionSocketFactory
import org.apache.http.conn.ssl.SSLConnectionSocketFactory
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager
import org.apache.http.ssl.SSLContexts
import java.util.logging.*

import javax.net.ssl.SSLContext
import java.security.KeyManagementException
import java.security.NoSuchAlgorithmException

@TypeChecked
class FilterProxySimulator extends BaseDsActorSimulator  {
    private static Logger logger = Logger.getLogger(FilterProxySimulator.class.getName());

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

//        common.setTls(true)

        String inputName = (common.isTls()) ? transactionType.tlsEndpointSimPropertyName : transactionType.endpointSimPropertyName
            String relayName = FilterProxyProperties.getRelayEndpointName(inputName)
            SimulatorConfigElement ele = simulatorConfig.getConfigEle(relayName)
            String outgoingEndpoint = ele.asString()
            byte[] responseBody = post(outgoingEndpoint, header, body)
            if (dsSimCommon.simCommon.db) {
                dsSimCommon.simCommon.db.putResponseBody(responseBody)
                dsSimCommon.simCommon.db.putResponseHeader(lastHeader)
            }
            lastHeader.split('\n').each { String hdr ->
                String[] parts = hdr.split(':', 2)
                if (!parts.size() == 2) return
                String name = parts[0]
                String value = parts[1]
                response.addHeader(name, value)
            }
            if (dsSimCommon.simCommon.os) {
                dsSimCommon.simCommon.os.write(responseBody)
            }

        return false
    }

    static List<String> postHeadersToIgnore = [
            'content-length',
            'transfer-encoding',
            'user-agent',
            'host'
    ]

    String lastHeader = null

    byte[] post(String endpoint, String msgHeader, String msgBody) {
        logger.info("Has simConfig ${dsSimCommon.simulatorConfig != null}")
        logger.info("Has environmentName ${dsSimCommon.simulatorConfig.environmentName != null}")
        String envName = dsSimCommon.simulatorConfig.environmentName //.getConfigEle(SimulatorProperties.environmentName).asString()
        logger.info("Environment name is ${envName}")
        File keyStoreFile = Installation.instance().getKeystore(envName)
        logger.info("Keystore is ${keyStoreFile}")
        String keyStorePassword = Installation.instance().getKeystorePassword(envName)
        logger.info("POST to ${endpoint}")
        logger.info("Header is \n${msgHeader}")
        org.apache.http.client.HttpClient httpclient = (org.apache.http.client.HttpClient) ((common.isTls()) ? TlsBuilder.getTlsClient(keyStoreFile, keyStorePassword) : HttpClients.createDefault())
        HttpPost post = new HttpPost(endpoint)
        post.setEntity(new StringEntity(msgBody))
        logger.info("Filtering headers:")
        msgHeader.split('\n').each { String headerLine ->
            headerLine = headerLine.trim()
            if (!headerLine.contains(':')) return
            String[] parts = headerLine.split(':', 2)
            String name = parts[0].trim()
            String value = parts[1].trim()
            if (postHeadersToIgnore.contains(name.toLowerCase())) return
            logger.info("...using header " + name + ": " + value)
            post.addHeader(name, value)
        }

        HttpResponse response
        try {
            response = httpclient.execute(post)
        } catch (Exception e) {
            logger.log(Level.SEVERE, "POST to ${endpoint} failed", e)
            throw e
        }
        Header[] hs = response.allHeaders
        StringBuilder buf = new StringBuilder()
        response.allHeaders.each { Header h ->
            String name = h.name.trim()
            if (postHeadersToIgnore.contains(name.toLowerCase())) return
            String value = h.value.trim()
            buf.append("${name}: ${value}\n")
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
