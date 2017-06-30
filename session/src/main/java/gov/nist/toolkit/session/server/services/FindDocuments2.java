package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.registrymetadata.client.Code;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.sitemanagementui.client.SiteSpec;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Diane Azais local on 10/7/2015.
 *
 * Service class for the new Find Documents tab.
 */
public class FindDocuments2 extends CommonService {
    static Logger logger = Logger.getLogger(FindDocuments2.class);
    Session session;

    public FindDocuments2(Session session) throws XdsException {
        this.session = session;
    }

    public List<Result> run(SiteSpec site, String pid, Map<String, List<String>> selectedCodes) {
        try {
            session.setSiteSpec(site);
            session.transactionSettings.assignPatientId = false;
            TestInstance testInstance = new TestInstance("FindDocuments2");

            logger.info("FindDocuments2:  " + selectedCodes);


            // XDS Codes
            List<String> deType = selectedCodes.get(CodesConfiguration.DocumentEntryType);
            if (deType == null) {
                deType = new ArrayList<String>();
                deType.add("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
            }

            List<String> deStatus = selectedCodes.get(CodesConfiguration.DocumentEntryStatus);
            if (deStatus == null) deStatus = new ArrayList<String>();

            List<String> classCodes = selectedCodes.get(CodesConfiguration.ClassCode);
            if (classCodes == null) classCodes = new ArrayList<String>();

            List<String> typeCodes = selectedCodes.get(CodesConfiguration.TypeCode);
            if (typeCodes == null) typeCodes = new ArrayList<String>();

            List<String> formatCodes = selectedCodes.get(CodesConfiguration.FormatCode);
            if (formatCodes == null) formatCodes = new ArrayList<String>();

            List<String> healthcareFacilityTypeCodes = selectedCodes.get(CodesConfiguration.HealthcareFacilityTypeCode);
            if (healthcareFacilityTypeCodes == null) healthcareFacilityTypeCodes = new ArrayList<String>();

            List<String> practiceSettingCodes = selectedCodes.get(CodesConfiguration.PracticeSettingCode);
            if (practiceSettingCodes == null) practiceSettingCodes = new ArrayList<String>();

            List<String> confCodes = selectedCodes.get(CodesConfiguration.ConfidentialityCode);
            if (confCodes == null) confCodes = new ArrayList<String>();

            List<String> eventCodeList = selectedCodes.get(CodesConfiguration.EventCodeList);
            if (eventCodeList == null) eventCodeList = new ArrayList<String>();


            // Date parameters
            List<String> creationTimeFrom = selectedCodes.get(CodesConfiguration.CreationTimeFrom);
            if (creationTimeFrom == null) creationTimeFrom = new ArrayList<String>();

            List<String> creationTimeTo = selectedCodes.get(CodesConfiguration.CreationTimeTo);
            if (creationTimeTo == null) creationTimeTo = new ArrayList<String>();

            List<String> serviceStartTimeFrom = selectedCodes.get(CodesConfiguration.ServiceStartTimeFrom);
            if (serviceStartTimeFrom == null) serviceStartTimeFrom = new ArrayList<String>();

            List<String> serviceStartTimeTo = selectedCodes.get(CodesConfiguration.ServiceStartTimeTo);
            if (serviceStartTimeTo == null) serviceStartTimeTo = new ArrayList<String>();

            List<String> serviceStopTimeFrom = selectedCodes.get(CodesConfiguration.ServiceStopTimeFrom);
            if (serviceStopTimeFrom == null) serviceStopTimeFrom = new ArrayList<String>();

            List<String> serviceStopTimeTo = selectedCodes.get(CodesConfiguration.ServiceStopTimeTo);
            if (serviceStopTimeTo == null) serviceStopTimeTo = new ArrayList<String>();


            // Other parameters
            List<String> authorPerson = selectedCodes.get(CodesConfiguration.AuthorPerson);
            if (authorPerson == null) authorPerson = new ArrayList<String>();

            List<String> returnsType = selectedCodes.get(CodesConfiguration.ReturnsType);
            if (returnsType == null) {
                returnsType = new ArrayList<>();
                returnsType.add("ObjectRef");
            }

            List<String> sections = new ArrayList<String>();

            // ----- Build the parameters map -----
            // Codes are generated using: new Code(codeDef).getNoDisplay());   ||    Other parameters are simply copied.
            Map<String, String> params = new HashMap<String, String>();

            if (session.siteSpec.actorType.equals(ActorType.REGISTRY))
                sections.add("XDS");
            else if (session.siteSpec.actorType.equals(ActorType.INITIATING_GATEWAY))
                sections.add("IG");
            else {
                sections.add("XCA");
                String home = site.homeId;
                if (home != null && !home.equals("")) {
                    params.put("$home$", home);
                }
            }



            // PID
            if (pid != null && !pid.equals(""))
                params.put("$patient_id$", pid);

            // DocumentEntryType
            int i=0;
            for (String codeDef : deType) {
                params.put("$ot" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // DocumentEntryStatus
            i=0;
            for (String codeDef : deStatus) {
                params.put("$dst" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // ClassCode
            i=0;
            for (String codeDef : classCodes) {
                params.put("$clc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            // TypeCode
            i=0;
            for (String codeDef : typeCodes) {
                params.put("$tc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            // FormatCode
            i=0;
            for (String codeDef : formatCodes) {
                params.put("$fc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            // HealthcareFacilityTypeCode
            i=0;
            for (String codeDef : healthcareFacilityTypeCodes) {
                params.put("$hftc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            // PracticeSettingCode
            i=0;
            for (String codeDef : practiceSettingCodes) {
                params.put("$psc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            // ConfidentialityCode
            i=0;
            for (String codeDef : confCodes) {
                params.put("$cc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            // EventCodeList
            i=0;
            for (String codeDef : eventCodeList) {
                params.put("$ecl" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            // CreationTimeFrom
            i=0;
            for (String codeDef : creationTimeFrom) {
                params.put("$ctf" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // CreationTimeTo
            i=0;
            for (String codeDef : creationTimeTo) {
                params.put("$ctt" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // ServiceStartTimeFrom
            i=0;
            for (String codeDef : serviceStartTimeFrom) {
                params.put("$sstf" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // ServiceStartTimeTo
            i=0;
            for (String codeDef : serviceStartTimeTo) {
                params.put("$sstt" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // ServiceStopTimeFrom
            i=0;
            for (String codeDef : serviceStopTimeFrom) {
                params.put("$sttf" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // ServiceStopTimeTo
            i=0;
            for (String codeDef : serviceStopTimeTo) {
                params.put("$sttt" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // AuthorPerson
            i=0;
            for (String codeDef : authorPerson) {
                params.put("$ap" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            // ReturnType
            i=0;
            for (String codeDef : returnsType) {
                params.put("$rt" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            logger.info("Starting FindDocuments query");
            Result r = session.xdsTestServiceManager().xdstest(testInstance, sections, params, null, null, true);
            return asList(r);
        } catch (Exception e) {
            return buildExtendedResultList(e);
        } finally {
            session.clear();
        }
    }


}
