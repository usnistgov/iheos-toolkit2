package gov.nist.toolkit.simProxy.server.proxy

import gov.nist.toolkit.actortransaction.EndpointParser
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.simcommon.server.BaseActorSimulator
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import org.apache.log4j.Logger

/**
 *
 */
class SimProxySimulator extends BaseActorSimulator {
    private static Logger logger = Logger.getLogger(SimProxySimulator.class)
    static List<TransactionType> transactions = TransactionType.asList()

    SimProxySimulator() {}
    
    public boolean run(TransactionType transactionType, MessageValidatorEngine mvc, String validation) throws IOException {
        String rawHeader = db.getRequestMessageHeader()

        String endpoint = config.getConfigEle(SimulatorProperties.proxyForwardEndpoint).asString()
        EndpointParser parser = new EndpointParser(endpoint)
        String host = parser.host
        String port = parser.port
        port = '9999'

        StringBuilder headerBuf = new StringBuilder()
        rawHeader.eachLine { String line ->
            String lcLine = line.toLowerCase()
            if (!(lcLine.contains('chunked') && lcLine.contains('transfer-encoding')) && !lcLine.startsWith('post')) {
                if (line.trim() != '')
                    headerBuf.append(line).append('\r\n')
            }
            if (line.startsWith("POST")) {
                String[] parts = line.split()
                parts[0] = 'POST'
                parts[1] = parser.getService();
                StringBuilder buf = new StringBuilder()
                parts.each { buf.append(it).append(' ') }
                line = buf.toString().trim() + '\r\n'
                headerBuf.append(line)
            }
        }

        String body = new String(db.getRequestMessageBody())

        headerBuf.append("Content-Length: ${body.size()}\r\n")
        headerBuf.append('\r\n')
        String header = headerBuf.toString()


        StringBuilder msg = new StringBuilder()
        msg.append(header)
        msg.append(body)
        String msgout = msg.toString()

        StringBuilder rheader = new StringBuilder()
        StringBuilder rbody = new StringBuilder()

        Socket s = new Socket(host, port as Integer)
        s.withStreams { input, output ->
            output << msgout
            List<String> lines = input.newReader().readLines()
            boolean isHeader = true
            lines.each {
                if (it.trim() == '')
                    isHeader = false
                else if (isHeader)
                    rheader.append(it)
                else
                    rbody.append(it)
            }

        }

        String responseHeader = rheader.toString()
        String responseBody = rbody.toString()


        return false
    }

}
