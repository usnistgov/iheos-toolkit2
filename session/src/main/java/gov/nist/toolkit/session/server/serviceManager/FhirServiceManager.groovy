package gov.nist.toolkit.session.server.serviceManager

import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.shared.gov.nist.toolkit.session.shared.DatasetElement
import org.apache.log4j.Logger
/**
 *
 */
class FhirServiceManager {
    static Logger logger = Logger.getLogger(FhirServiceManager.class)
    private final Session session;

    FhirServiceManager(Session session) {
        this.session = session
    }

    Result create(DatasetElement datasetElement) {

    }
}
