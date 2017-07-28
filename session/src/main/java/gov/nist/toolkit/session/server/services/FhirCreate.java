package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.datasets.shared.DatasetElement;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static gov.nist.toolkit.results.CommonService.asList;
import static gov.nist.toolkit.results.CommonService.buildExtendedResultList;

/**
 *
 */
public class FhirCreate {
    Session session;

    public FhirCreate(Session session) {
        this.session = session;
    }

    public List<Result> run(SiteSpec site, DatasetElement datasetElement, String urlExtension) {
        try {
            session.setSiteSpec(site);
            session.transactionSettings.assignPatientId = false;
            TestInstance testInstance = new TestInstance("FHIR");
            List<String> sections = new ArrayList<>();
            sections.add("create");
            Map<String, String> params = new HashMap<String, String>();
            params.put("$ResourceFile$", new File(Installation.instance().datasets(), datasetElement.getPath()).getPath());
            params.put("$UrlExtension$", urlExtension);

            return asList(new XdsTestServiceManager(session).xdstest(testInstance, sections, params, null, null, true));
        } catch (Exception e) {
            return buildExtendedResultList(e);
        } finally {
            session.clear();
        }

    }
}
