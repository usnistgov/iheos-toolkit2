package gov.nist.toolkit.simulators.support
import gov.nist.toolkit.actorfactory.SimDb
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import groovy.xml.MarkupBuilder
/**
 *
 */

class TransactionReportBuilder {

    String build(SimDb db, SimulatorConfig config) {
        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.transactionLog(type: config.actorType, simId: config.id) {
            request() {
                header(db.requestMessageHeader)
                body(new String(db.requestMessageBody))
            }
            response() {
//                header(db.responseMessageHeader)  not available this early in the processing - gets written by simfilter
                body(new String(db.responseMessageBody))
            }
        }

        return writer.toString()
    }
}
