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
public class FhirSearch {
    Session session;

    public FhirSearch(Session session) {
        this.session = session;
    }

    public List<Result> run(SiteSpec site, String resourceTypeName, String query) {
        try {
            session.setSiteSpec(site);
            session.transactionSettings.assignPatientId = false;
            TestInstance testInstance = new TestInstance("FHIR");
            List<String> sections = new ArrayList<>();
            sections.add("search");
            Map<String, String> params = new HashMap<String, String>();
            params.put("$ResourceType$", resourceTypeName);
            params.put("$Query$", query);

            List<Result> results = asList(new XdsTestServiceManager(session).xdstest(testInstance, sections, params, null, null, true));
            return results;
        } catch (Exception e) {
            return buildExtendedResultList(e);
        } finally {
            session.clear();
        }

    }

    public List<Result> run(SiteSpec site, String resourceTypeName, Map<String, List<String>> codesSpec) {
        return run(site, resourceTypeName, codesSpecToQuery(codesSpec));
    }

    private String codesSpecToQuery(Map<String, List<String>> codesSpec) {
        StringBuilder buf = new StringBuilder();

        boolean first = true;
        for (String codeName : codesSpec.keySet()) {
            List<String> values = codesSpec.get(codeName);
            if (values.size() > 0) {  // for now only encoded first value
                if (first)
                    first = false;
                else {
                    buf.append(';');
                }
                buf.append(codeName).append('=').append(values.get(0));
            }
        }

        return buf.toString();
    }
}
