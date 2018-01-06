package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.toolkit.results.CommonService.asList;
import static gov.nist.toolkit.results.CommonService.buildExtendedResultList;

/**
 *
 */
public class FhirRead {
    Session session;

    public FhirRead(Session session) {
        this.session = session;
    }

    public List<Result> run(SiteSpec site, String reference) {
        try {
            session.setSiteSpec(site);
            session.transactionSettings.assignPatientId = false;
            TestInstance testInstance = new TestInstance("FHIR", "read");
            List<String> sections = new ArrayList<>();
            sections.add("read");
            Map<String, String> params = new HashMap<String, String>();
            if (reference.startsWith("http"))
                params.put("$UrlExtension$", reference);
            else
                params.put("$UrlExtension$", '/' + reference);

            List<Result> results = asList(new XdsTestServiceManager(session).xdstest(testInstance, sections, params, null, null, true));
            return results;
        } catch (Exception e) {
            return buildExtendedResultList(e);
        } finally {
            session.clear();
        }

    }
}
