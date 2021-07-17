package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.fhir.server.utility.FhirClient;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.serviceManager.XdsTestServiceManager;
import gov.nist.toolkit.sitemanagement.client.SiteSpec;
import org.hl7.fhir.dstu3.model.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;

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
            TestInstance testInstance = new TestInstance("FHIR", "search");
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

    public List<Result> run(SiteSpec site, String resourceTypeName, Map<String, List<String>> codesSpec) throws Exception {
        return run(site, resourceTypeName, codesSpecToQuery(codesSpec));
    }

    private String codesSpecToQuery(Map<String, List<String>> codesSpec) throws Exception {
        StringBuilder buf = new StringBuilder();

        boolean first = true;
        for (String codeName : codesSpec.keySet()) {
            List<String> values = codesSpec.get(codeName);
            if (values.size() > 0) {  // for now only encoded first value
                String value = values.get(0);
                if (codeName.equals("patient.identifier")) {
                    value = patientIdForSearch(value);
                }
                if (first)
                    first = false;
                else {
                    buf.append(';');
                }
                buf.append(codeName).append('=').append(value);
            }
        }

        return buf.toString();
    }

    /**
     *
     * @param patient system|value OR id^^^&oid&ISO || Patient Resource reference
     * @return
     * @throws Exception
     */
    private String patientIdForSearch(String patient) throws Exception {
        if (patient == null || patient.equals(""))
            return null;
        patient = patient.trim();
        if (patient.startsWith("http")) {
            IBaseResource res = FhirClient.readResource(patient);
            if (res instanceof Patient) {
                Patient pat = (Patient) res;
                String system = pat.getIdentifier().get(0).getSystem();
                String value = pat.getIdentifier().get(0).getValue();
                return system + "|" + value;
            } else
                throw new Exception("Do not understand Patient reference");
        } else if (patient.contains("^^^")) {
            String[] idAndRest = patient.split("\\^\\^\\^", 2);
            if (idAndRest.length != 2) {
                throw new Exception("Do not understand Patient reference");
            }
            String id = idAndRest[0];
            String amp = (patient.contains("&amp;")) ? "&amp;" : "&";
            String[] oidPlus = idAndRest[1].split(amp);
            if (oidPlus.length < 2) {
                throw new Exception("Do not understand Patient reference");
            }
            String oid = oidPlus[1];
            oid = "urn:oid:" + oid;
            return oid + "|" + id;
        } else if (patient.contains("|")) {
            return patient;
        } else {
            throw new Exception("Do not understand Patient reference");
        }
    }

}
