package gov.nist.toolkit.fhir.simulators.sim.reg.sq;

import gov.nist.toolkit.metadataModel.DocEntry;
import gov.nist.toolkit.metadataModel.MetadataCollection;
import gov.nist.toolkit.metadataModel.RegIndex;
import gov.nist.toolkit.metadataModel.StatusValue;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.SQCodeAnd;
import gov.nist.toolkit.valregmsg.registry.SQCodeOr;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.FindDocumentsForMultiplePatients;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.util.ArrayList;
import java.util.List;

public class FindDocumentsForMultiplePatientsSim extends FindDocumentsForMultiplePatients {

    RegIndex ri;

    public void setRegIndex(RegIndex ri) {
        this.ri = ri;
    }

    public FindDocumentsForMultiplePatientsSim(StoredQuerySupport sqs)
            throws MetadataValidationException {
        super(sqs);
    }

    protected Metadata runImplementation() throws MetadataException,
            XdsException, LoggerException {

        MetadataCollection mc = ri.getMetadataCollection();
        List<DocEntry> results = new ArrayList<>();

        if (patient_id == null) {
            results.addAll(mc.docEntryCollection.getAll());
        } else {
            // match on patient id
            for (String pid : patient_id) {
                List<DocEntry> res = mc.docEntryCollection.findByPid(pid);
                results.addAll(res);
            }
        }

        // validate on availabilityStatus
        List<StatusValue> statuses = ri.translateStatusValues(this.status);
        results = mc.docEntryCollection.filterByStatus(statuses, results);

        // validate on authorPerson
        results = mc.docEntryCollection.filterByAuthorPerson(author_person, results);

        // validate on creation time
        results = mc.docEntryCollection.filterByCreationTime(creation_time_from, creation_time_to, results);

        // validate on serviceStartTime
        results = mc.docEntryCollection.filterByServiceStartTime(service_start_time_from, service_start_time_to, results);

        // validate on serviceStopTime
        results = mc.docEntryCollection.filterByServiceStopTime(service_stop_time_from, service_stop_time_to, results);

        // validate on objectType
        results = mc.docEntryCollection.filterByObjectType(entry_type, results);

        // validate on formatCode
        if (format_codes != null && !format_codes.isEmpty()) {
            if (format_codes instanceof SQCodeOr) {
                results = mc.docEntryCollection.filterByFormatCode((SQCodeOr)format_codes, results);
            } else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + format_codes.getClass().getName() + " (from formatCodes) into an instance of class SQCodeOr", null);
            }
        }

        // validate on classCode
        if (class_codes != null && !class_codes.isEmpty()) {
            if (class_codes instanceof SQCodeOr) {
                results = mc.docEntryCollection.filterByClassCode((SQCodeOr)class_codes, results);
            } else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + class_codes.getClass().getName() + " (from classCodes) into an instance of class SQCodeOr", null);
            }
        }

        // validate on typeCode
        if (type_codes != null && !type_codes.isEmpty()) {
            if (type_codes instanceof SQCodeOr) {
                results = mc.docEntryCollection.filterByTypeCode((SQCodeOr)type_codes, results);
            } else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + type_codes.getClass().getName() + " (from typeCodes) into an instance of class SQCodeOr", null);
            }
        }

        // validate on practiceSettingCode
        if (practice_setting_codes != null && !practice_setting_codes.isEmpty()) {
            if (practice_setting_codes instanceof SQCodeOr) {
                results = mc.docEntryCollection.filterByPracticeSettingCode((SQCodeOr)practice_setting_codes, results);
            } else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + practice_setting_codes.getClass().getName() + " (from practiceSettingCode) into an instance of class SQCodeOr", null);
            }
        }

        // validate on hcftCode
        if (hcft_codes != null && !hcft_codes.isEmpty()) {
            if (hcft_codes instanceof SQCodeOr) {
                results = mc.docEntryCollection.filterByHcftCode((SQCodeOr)hcft_codes, results);
            } else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + hcft_codes.getClass().getName() + " (from hcftCode) into an instance of class SQCodeOr", null);
            }
        }

        // validate on eventCode
        if (event_codes != null && !event_codes.isEmpty()) {
            if (event_codes instanceof SQCodeOr) {
                results = mc.docEntryCollection.filterByEventCode((SQCodeOr)event_codes, results);
            }
            else if (event_codes instanceof SQCodeAnd) {
                results = mc.docEntryCollection.filterByEventCode((SQCodeAnd)event_codes, results);
            } else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + event_codes.getClass().getName() + " (from eventCode) into an instance of class SQCodeOr or SQCodeAnd", null);
            }
        }

        // validate on confCode
        if (conf_codes != null && !conf_codes.isEmpty()) {
            if (conf_codes instanceof SQCodeOr) {
                results = mc.docEntryCollection.filterByConfCode((SQCodeOr)conf_codes, results);
            }
            else if (conf_codes instanceof SQCodeAnd) {
                results = mc.docEntryCollection.filterByConfCode((SQCodeAnd)conf_codes, results);
            }
            else {
                throw new XdsException("FindDocumentsSim: cannot cast model of type " + conf_codes.getClass().getName() + " (from confCode) into an instance of class SQCodeOr or SQCodeAnd", null);
            }
        }

        List<String> uuids = mc.getIdsForObjects(results);

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
