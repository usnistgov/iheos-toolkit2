package gov.nist.toolkit.services.server.orchestration

import gov.nist.toolkit.actortransaction.client.ActorType
import gov.nist.toolkit.configDatatypes.server.SimulatorProperties
import gov.nist.toolkit.fhir.utility.FhirClient
import gov.nist.toolkit.installation.Installation
import gov.nist.toolkit.results.client.TestInstance
import gov.nist.toolkit.services.client.FhirSupportOrchestrationRequest
import gov.nist.toolkit.services.client.FhirSupportOrchestrationResponse
import gov.nist.toolkit.services.client.PatientDef
import gov.nist.toolkit.services.server.ToolkitApi
import gov.nist.toolkit.session.server.Session
import gov.nist.toolkit.simcommon.client.SimId
import gov.nist.toolkit.simcommon.client.SimulatorConfig
import gov.nist.toolkit.sitemanagement.client.SiteSpec
import gov.nist.toolkit.xdsexception.ExceptionUtil
import org.apache.log4j.Logger
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Identifier
import org.hl7.fhir.dstu3.model.Patient
import org.hl7.fhir.dstu3.model.Resource
import org.hl7.fhir.instance.model.api.IBaseResource

class FhirSupportOrchestrationBuilder {
    static private final Logger logger = Logger.getLogger(FhirSupportOrchestrationBuilder.class);
    ToolkitApi api
    private Session session
    private FhirSupportOrchestrationRequest request
    private Util util
    TestInstance testInstance = new TestInstance('supporting_fhir_patients')
    String siteName
    String simName = 'fhir_support'
    SiteSpec siteSpec
    private ActorType actorType = ActorType.FHIR_SERVER
    private SimId simId

    // use existing state means use existing sim if it exists

    FhirSupportOrchestrationBuilder(ToolkitApi api, Session session, FhirSupportOrchestrationRequest request) {
        this.api = api
        this.session = session
        this.request = request
        this.util = new Util(api)
        this.siteName = "${request.userName}__${simName}"
        this.siteSpec = new SiteSpec(siteName)
    }

    FhirSupportOrchestrationBuilder(ToolkitApi api, Session session, String userName, boolean useExistingState) {
        this(api, session, requestBuilder(userName, useExistingState))
    }

    private static FhirSupportOrchestrationRequest requestBuilder(String userName, boolean useExistingState) {
        FhirSupportOrchestrationRequest request = new FhirSupportOrchestrationRequest()
        request.userName = userName
        request.useExistingState = useExistingState
        return request
    }

    FhirSupportOrchestrationResponse buildTestEnvironment() {
        FhirSupportOrchestrationResponse response = new FhirSupportOrchestrationResponse()
        FhirOrchestrationProperties orchProps = new FhirOrchestrationProperties(request.userName, !request.useExistingState)
        SimulatorConfig simConfig

        simId = new SimId(request.userName, simName, ActorType.FHIR_SERVER.shortName)
        boolean simExists = api.simulatorExists(simId)
        boolean needsLoading
        if (simExists && !request.useExistingState) {
            api.deleteSimulatorIfItExists(simId)
            simExists = false
        }

        if (!simExists) {
            simConfig = api.createSimulator(simId).getConfig(0)
            simExists = true
            needsLoading = true
        } else {
            simConfig = api.getConfig(simId)
            needsLoading = false
        }

        try {
            if (needsLoading)
                util.submit(request.userName, siteSpec, testInstance)
        } catch (Exception e) {
            String error = "Error submiting Patients to FHIR server ${simId.toString()} \n ${ExceptionUtil.exception_details(e)}"
            response.addMessage(testInstance, false, error)
            logger.error(error)
            return response
        }

        String url = simConfig.get(SimulatorProperties.fhirEndpoint).asString()
        response.addMessage(null, true, "FHIR Server URL: ${url}")

        IBaseResource baseResource = FhirClient.readResource("${url}/Patient")
        if (!(baseResource instanceof Bundle)) {
            response.addMessage(null, false, "FHIR READ of Patients from support server should return Bundle, returned ${baseResource.getClass().getName()} instead.")
            return response
        }
        Bundle bundle = (Bundle) baseResource
        bundle.entry.each { Bundle.BundleEntryComponent comp ->
            Resource r = comp.getResource()
            if (r instanceof Patient) {
                Patient p = (Patient) r
                Identifier id = p.getIdentifier()?.get(0)
                String system = id.system
                String value = id.value
                if (!system) {
                    response.addMessage(null, false, "Patient found in simulator without valid Identifier.system: ${system}, Resource is ${url}")
                    return response
                }
                if (!value) {
                    response.addMessage(null, false, "Patient found in simulator without valid Identifier.value: ${value}, Resource is ${url}")
                    return response
                }
                if (system.startsWith('urn:oid:'))
                    system = system.substring('urn:oid:'.size())
                String pid = "${value}^^^&${system}&ISO"
                String given = p.name?.get(0)?.given?.get(0)
                String family = p.name?.get(0)?.family
                String pUrl = comp.fullUrl
                response.addPatient(new PatientDef(pid, given, family, pUrl))
            }
        }

        return response
    }

    String getSiteName() { return siteName }
    SimId getSimId() { return simId }

    class StoredPatient {
        def name
        def url

        StoredPatient(def name, def url) {
            this.name = name
            this.url = url
        }
    }

    class FhirOrchestrationProperties {
        private File orchestrationPropFile;
        private Properties orchProps = new Properties();
        private boolean updated = false;
        private Session session;

        FhirOrchestrationProperties(String userName, boolean reinitialize) {
            orchestrationPropFile = Installation.instance().orchestrationPropertiesFile(userName, actorType.getShortName());
            if (orchestrationPropFile.exists())
                orchProps.load(new FileInputStream(orchestrationPropFile))
        }

        public void save() throws IOException {
            if (updated) {
                orchestrationPropFile.getParentFile().mkdirs();
                orchProps.store(new FileOutputStream(orchestrationPropFile), null);
                updated = false;
            }

        }

    }
}
