package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.registrymetadata.client.Code;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.client.CodesConfiguration;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.results.client.TestId;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by bill on 8/26/15.
 */
public class GetAll extends CommonService {
    Session session;

    public GetAll(Session session) throws XdsException {
        this.session = session;
    }
    public List<Result> run(SiteSpec site, String pid, Map<String, List<String>> selectedCodes) {
        try {
            session.setSiteSpec(site);
            session.transactionSettings.assignPatientId = false;
            TestId testId = new TestId("GetAll");

            System.out.println("GetAll:  " +  selectedCodes);

            List<String> formatCodes = selectedCodes.get(CodesConfiguration.FormatCode);
            if (formatCodes == null) formatCodes = new ArrayList<String>();

            List<String> confCodes = selectedCodes.get(CodesConfiguration.ConfidentialityCode);
            if (confCodes == null) confCodes = new ArrayList<String>();

            List<String> dStatus = selectedCodes.get(CodesConfiguration.DocumentEntryStatus);
            if (dStatus == null) dStatus = new ArrayList<String>();

            List<String> fStatus = selectedCodes.get(CodesConfiguration.FolderStatus);
            if (fStatus == null) fStatus = new ArrayList<String>();

            List<String> sStatus = selectedCodes.get(CodesConfiguration.SubmissionSetStatus);
            if (sStatus == null) sStatus = new ArrayList<String>();

            List<String> deType = selectedCodes.get(CodesConfiguration.DocumentEntryType);
            if (deType == null) {
                deType = new ArrayList<String>();
                deType.add("urn:uuid:7edca82f-054d-47f2-a032-9b2a5b5186c1");
            }

            List<String> returnsType = selectedCodes.get(CodesConfiguration.ReturnsType);
            if (returnsType == null) {
                returnsType = new ArrayList<>();
                returnsType.add("ObjectRef");
            }

            List<String> sections = new ArrayList<String>();
            if (session.siteSpec.actorType.equals(ActorType.REGISTRY))
                sections.add("XDS");
            else
                sections.add("XCA");

            Map<String, String> params = new HashMap<String, String>();
            if (pid != null && !pid.equals(""))
                params.put("$patient_id$", pid);

            int i=0;
            for (String codeDef : formatCodes) {
                params.put("$fc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            i=0;
            for (String codeDef : confCodes) {
                params.put("$cc" + String.valueOf(i) + "$", new Code(codeDef).getNoDisplay());
                i++;
            }

            i=0;
            for (String codeDef : dStatus) {
                params.put("$dst" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            i=0;
            for (String codeDef : fStatus) {
                params.put("$fst" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            i=0;
            for (String codeDef : sStatus) {
                params.put("$sst" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            i=0;
            for (String codeDef : deType) {
                params.put("$ot" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            i=0;
            for (String codeDef : returnsType) {
                params.put("$rt" + String.valueOf(i) + "$", codeDef);
                i++;
            }

            Result r = session.xdsTestServiceManager().xdstest(testId, sections, params, null, null, true);
            return asList(r);
        } catch (Exception e) {
            return buildExtendedResultList(e);
        } finally {
            session.clear();
        }
    }
}
