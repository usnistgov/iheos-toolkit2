package gov.nist.toolkit.session.server.services

import gov.nist.toolkit.datasets.shared.DatasetElement
import gov.nist.toolkit.installation.server.Installation
import gov.nist.toolkit.results.client.Result
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.xdsexception.ExceptionUtil
import java.util.logging.*

import static gov.nist.toolkit.results.CommonService.asList
import static gov.nist.toolkit.results.CommonService.buildExtendedResultList

public class ProvideDocumentBundle {
    static private final Logger logger = Logger.getLogger(ProvideDocumentBundle.class.getName());
    Session session;

    public ProvideDocumentBundle(Session session) {
        this.session = session;
    }

    public List<Result> run(SiteSpec site, DatasetElement datasetElement) {
        try {

            // make sure the FHIR Support server is running
//            ToolkitApi api = ToolkitApi.forNormalUse(session)
//            FhirSupportOrchestrationResponse response = new FhirSupportOrchestrationBuilder(api, session,true).buildTestEnvironment()
//            assert !response.error

            session.setSiteSpec(site);
            session.transactionSettings.assignPatientId = false;
            TestInstance testInstance = new TestInstance("FHIR", site.testSession);
            List<String> sections = new ArrayList<>();
            sections.add("pdb");
            Map<String, String> params = new HashMap<String, String>();
            params.put('$ResourceFile$', new File(Installation.instance().datasets(), datasetElement.getFile()).getPath());
            params.put('$UrlExtension$', '/' + datasetElement.getType());

            return asList(new XdsTestServiceManager(session).xdstest(testInstance, sections, params, null, null, true));
        } catch (Throwable e) {
            logger.severe("ProvideDocumentBundle: ${ExceptionUtil.exception_details(e)}")
            return buildExtendedResultList(e);
        } finally {
            session.clear();
        }

    }
}
