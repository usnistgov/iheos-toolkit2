package gov.nist.toolkit.fhir.simulators.sim.reg.sq;

import gov.nist.toolkit.docref.EbRim;
import gov.nist.toolkit.metadataModel.*;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valregmsg.registry.SQCodeAnd;
import gov.nist.toolkit.valregmsg.registry.SQCodeOr;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.GetAll;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by bill on 8/13/15.
 */
public class GetAllSim extends GetAll {
    RegIndex ri;

    public void setRegIndex(RegIndex ri) {
        this.ri = ri;
    }

    public GetAllSim(StoredQuerySupport sqs)
            throws MetadataValidationException {
        super(sqs);
    }

    protected Metadata runImplementation() throws XdsException {

        // validate DEType
        if (deObjectTypes == null) deObjectTypes = new ArrayList<>();
        for (String objectType : deObjectTypes) {
            if (!documentTypes.contains(objectType))
                throw new MetadataValidationException("$XDSDocumentEntryType parameter shall only contain these values " + validDocumentTypesString(), EbRim.RegistryObject_attributes);
        }
        if (deObjectTypes.isEmpty())
            deObjectTypes.add(stableDocumentType);

        MetadataCollection mc = ri.getMetadataCollection();
        List<DocEntry> deResults;
        List<Fol> folResults;
        List<SubSet> ssResults;

//        System.out.println("Query: " + toString());

        // match on patient id
        deResults = mc.docEntryCollection.findByPid(patient_id);
        ssResults = mc.subSetCollection.findByPid(patient_id);
        folResults = mc.folCollection.findByPid(patient_id);

//        System.out.println("By patientId:");
//        System.out.println("   DE: " + deResults);
//        System.out.println("   SS: " + ssResults);
//        System.out.println("   FOL: " + folResults);

        // validate on DE availabilityStatus
        List<StatusValue> deStatuses = ri.translateStatusValues(deStatus);
        deResults = mc.docEntryCollection.filterByStatus(deStatuses, deResults);

        // validate on SS availabilityStatus
        List<StatusValue> ssStatuses = ri.translateStatusValues(ssStatus);
        ssResults = mc.subSetCollection.filterByStatus(ssStatuses, ssResults);

        // validate on FOL availabilityStatus
        List<StatusValue> folStatuses = ri.translateStatusValues(folStatus);
        folResults = mc.folCollection.filterByStatus(folStatuses, folResults);

//        System.out.println("After status validate:");
//        System.out.println("   DE: " + deResults);
//        System.out.println("   SS: " + ssResults);
//        System.out.println("   FOL: " + folResults);

        // validate on DEType
        deResults = mc.docEntryCollection.filterByObjectType(deObjectTypes, deResults);

//        System.out.println("After model type validate:");
//        System.out.println("   DE: " + deResults);

        // validate on formatCode
        if (format_codes != null && !format_codes.isEmpty()) {
            if (format_codes instanceof SQCodeOr) {
                deResults = mc.docEntryCollection.filterByFormatCode((SQCodeOr)format_codes, deResults);
            } else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + format_codes.getClass().getName() + " (from formatCodes) into an instance of class SQCodeOr", null);
            }
        }

//        System.out.println("After format code validate:");
//        System.out.println("   DE: " + deResults);

        // validate on confCode
        if (conf_codes != null && !conf_codes.isEmpty()) {
            if (conf_codes instanceof SQCodeOr) {
                deResults = mc.docEntryCollection.filterByConfCode((SQCodeOr)conf_codes, deResults);
            }
            else if (conf_codes instanceof SQCodeAnd) {
                deResults = mc.docEntryCollection.filterByConfCode((SQCodeAnd)conf_codes, deResults);
            }
            else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + conf_codes.getClass().getName() + " (from confCode) into an instance of class SQCodeOr or SQCodeAnd", null);
            }
        }

//        System.out.println("After conf code validate:");
//        System.out.println("   DE: " + deResults);

//        System.out.println("deIds - " + mc.getIdsForObjects(deResults));
//        System.out.println("ssIds - " + mc.getIdsForObjects(ssResults));
//        System.out.println("folIds - " + mc.getIdsForObjects(folResults));

        Set<String> uuids = new HashSet<>(mc.getIdsForObjects(deResults));
        uuids.addAll(mc.getIdsForObjects(ssResults));
        uuids.addAll(mc.getIdsForObjects(folResults));

        List<Assoc> assocs = new ArrayList<Assoc>();

        for (String id : uuids)
            assocs.addAll(mc.assocCollection.getBySourceOrDest(id, id));

        for (Assoc a : assocs)
           uuids.add(a.getId());

        Metadata m = new Metadata();
        m.setVersion3();
        if (sqs.returnType == QueryReturnType.LEAFCLASS || sqs.returnType == QueryReturnType.LEAFCLASSWITHDOCUMENT) {
            m = mc.loadRo(uuids);
        } else {
            m.mkObjectRefs(uuids);
        }

        return m;
    }

}
