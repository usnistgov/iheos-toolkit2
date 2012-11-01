package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.MetadataCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.simulators.sim.reg.store.SubSet;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex.StatusValue;
import gov.nist.toolkit.valregmsg.registry.SQCodeOr;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.FindSubmissionSets;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory.QueryReturnType;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.List;

public class FindSubmissionSetsSim extends FindSubmissionSets {
	RegIndex ri;
	
	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}

	public FindSubmissionSetsSim(StoredQuerySupport sqs)
			throws MetadataValidationException {
		super(sqs);
	}

	protected Metadata runImplementation() throws MetadataException,
			XdsException, LoggerException {

		MetadataCollection mc = ri.getMetadataCollection();
		
		List<SubSet> results;
		
		// match on patient id
		results = mc.subSetCollection.findByPid(patient_id);
		
		// filter on availabilityStatus
		List<StatusValue> statuses = ri.translateStatusValues(this.status);
		results = mc.subSetCollection.filterByStatus(statuses, results);
		
		// filter on sourceId
		if (source_id != null)
			results = mc.subSetCollection.filterBySourceId(source_id, results);
		
		// filter on submissionTime
		results = mc.subSetCollection.filterBySubmissionTime(submission_time_from, submission_time_to,  results);
		
		// filter on authorPerson
		results = mc.subSetCollection.filterByAuthorPerson(author_person, results);
		
		// filter on contentType
		if (content_type != null && !content_type.isEmpty()) {
			if (content_type instanceof SQCodeOr) {
				results = mc.subSetCollection.filterByContentTypeCode((SQCodeOr)content_type, results);
			} else {
				throw new XdsException("FindSubmissionSetsSim: cannot cast object of type " + content_type.getClass().getName() + " (from contentTypeCodes) into an instance of class SQCodeOr", null);
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
