package gov.nist.toolkit.session.server.serviceManager

import gov.nist.toolkit.datasets.shared.DatasetElement
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.services.FhirCreate
import gov.nist.toolkit.session.server.services.FhirRead
import gov.nist.toolkit.session.server.services.FhirSearch
import gov.nist.toolkit.session.server.services.ProvideDocumentBundle
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

    List<Result> create(SiteSpec site, DatasetElement datasetElement) {
        return new FhirCreate(session).run(site, datasetElement);
    }

    List<Result> transaction(SiteSpec site, DatasetElement datasetElement) {
        return new ProvideDocumentBundle(session).run(site, datasetElement);
    }

    List<Result> read(SiteSpec site, String reference) {
        return new FhirRead(session).run(site, reference);
    }

    List<Result> search(SiteSpec site, String resourceTypeName, Map<String, List<String>> codesSpec) {
        return new FhirSearch(session).run(site, resourceTypeName, codesSpec);
    }
}
