package gov.nist.toolkit.testengine.simLogs

import ca.uhn.fhir.context.FhirContext
import edu.wustl.mir.erl.ihe.xdsi.util.Utility
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.fhir.server.resourceMgr.FileSystemResourceCache
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.installation.server.PropertyManager
import gov.nist.toolkit.installation.server.PropertyServiceManager
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.server.SimDb
import gov.nist.toolkit.simcommon.server.SimDbEvent
import gov.nist.toolkit.testengine.engine.FhirSimulatorTransaction
import gov.nist.toolkit.utilities.html.HeaderParser
import org.apache.log4j.Logger

import java.nio.file.Paths
import java.text.SimpleDateFormat
/**
 *
 */
class FhirTransactionLoader {
    private static Logger log = Utility.getLog();

    private static Installation installation = Installation.instance();
    private static PropertyServiceManager propertyServiceManager = installation.propertyServiceManager();
    private static PropertyManager propertyManager = propertyServiceManager.getPropertyManager();

    private static SimpleDateFormat timeOfTransactionFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static String externalCache = Paths.get(installation.externalCache().getAbsolutePath()).toString();

    private static FhirContext ctx = FileSystemResourceCache.ctx

    static List<FhirSimulatorTransaction> loadTransactions(SimId simId, TransactionType transactionType) {
        SimDb simDb = new SimDb(simId)

        simDb.getEventsSinceMarker().collect { SimDbEvent event ->
            FhirSimulatorTransaction trn = new FhirSimulatorTransaction(simId, transactionType)
            trn.setSimDbEvent(event)
            // Load request
            trn.setRequestHeaders(HeaderParser.parseHeaders(event.requestHeader))
            String mimeType = HeaderParser.mimeType(trn.getRequestHeaders())
            String body = new String(event.requestBody)
            if (mimeType.contains('json')) {
                trn.setRequest(ctx.newJsonParser().parseResource(body))
            } else {
                trn.setRequest(ctx.newXmlParser().parseResource(body))
            }

            // Load response
            trn.setResponseHeaders(HeaderParser.parseHeaders(event.responseHeader))
            mimeType = HeaderParser.mimeType(trn.getRequestHeaders())
            body = new String(event.responseBody)
            if (mimeType.contains('json')) {
                trn.setResponse(ctx.newJsonParser().parseResource(body))
            } else {
                trn.setResponse(ctx.newXmlParser().parseResource(body))
            }
            trn.url =  event.simLogUrl
            trn.placeToken = event.simLogPlaceToken
            trn
        }
    }
}
