package gov.nist.toolkit.session.server.serviceManager

import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.services.FhirCreate
import gov.nist.toolkit.session.shared.gov.nist.toolkit.session.shared.DatasetElement
import gov.nist.toolkit.sitemanagement.client.SiteSpec
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

    List<Result> create(SiteSpec site, DatasetElement datasetElement, String urlExtension) {
        return new FhirCreate(session).run(site, datasetElement, urlExtension);
    }
}
