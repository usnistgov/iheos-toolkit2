package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.LogMessage;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XDSRegistryOutOfResourcesException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import org.apache.axiom.om.OMElement;

public class SQFactory extends StoredQueryFactory {
	RegIndex ri;
	
	public SQFactory(OMElement ahqr, Response response, LogMessage log_message)
			throws XdsInternalException, MetadataException, XdsException,
			LoggerException {
		super(ahqr, response, log_message);
	}
	
	public void setRegIndex(RegIndex ri) {
		this.ri = ri;
	}

	public StoredQueryFactory buildStoredQueryHandler(StoredQuerySupport sqs)
			throws MetadataValidationException, LoggerException {
		if (query_id.equals(MetadataSupport.SQ_FindDocuments)) {
			setTestMessage("FindDocuments");
			FindDocumentsSim sim = new FindDocumentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		} 
		else if (query_id.equals(MetadataSupport.SQ_GetDocuments)) {
			setTestMessage("GetDocuments");
			GetDocumentsSim sim = new GetDocumentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		
		
		else if (query_id.equals(MetadataSupport.SQ_FindSubmissionSets)) {
			setTestMessage("FindSubmissionSets");
			FindSubmissionSetsSim sim = new FindSubmissionSetsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl	 = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_FindFolders)) {
			setTestMessage("FindFolders");
			FindFoldersSim sim = new FindFoldersSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetAll)) {
			setTestMessage("GetAll");
			response.add_error("XDSRegistryError", "UnImplemented Stored Query query id = " + query_id, "AdhocQueryRequest.java", null, log_message);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFolders)) {
			setTestMessage("GetFolders");
			GetFoldersSim sim = new GetFoldersSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetAssociations)) {
			setTestMessage("GetAssociations");
			GetAssociationsSim sim = new GetAssociationsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetDocumentsAndAssociations)) {
			setTestMessage("GetDocumentsAndAssociations");
			GetDocumentsAndAssociationsSim sim = new GetDocumentsAndAssociationsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetSubmissionSets)) {
			setTestMessage("GetSubmissionSets");
			GetSubmissionSetsSim sim = new GetSubmissionSetsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetSubmissionSetAndContents)) {
			setTestMessage("GetSubmissionSetAndContents");
			GetSubmissionSetAndContentsSim sim = new GetSubmissionSetAndContentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFolderAndContents)) {
			setTestMessage("GetFolderAndContents");
			GetFolderAndContentsSim sim = new GetFolderAndContentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFoldersForDocument)) {
			setTestMessage("GetFoldersForDocument");
			GetFoldersForDocumentSim sim = new GetFoldersForDocumentSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl	= sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_GetRelatedDocuments)) {
			setTestMessage("GetRelatedDocuments");
			GetRelatedDocumentsSim sim = new GetRelatedDocumentsSim(sqs);
			sim.setRegIndex(ri);
			storedQueryImpl = sim;
		}
		else if (query_id.equals(MetadataSupport.SQ_FindDocumentsForMultiplePatients)) {
			setTestMessage("FindDocumentsForMulitplePatients");
//			storedQueryImpl = new EbXML21FindDocumentsForMultiplePatients(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_FindFoldersForMultiplePatients)) {
			setTestMessage("FindFoldersForMulitplePatients");
//			storedQueryImpl = new EbXML21FindFoldersForMultiplePatients(sqs);
		}
		else {
			setTestMessage(query_id);
			response.add_error("XDSRegistryError", "Unknown Stored Query query id = " + query_id, "AdhocQueryRequest.java", SqDocRef.QueryID, log_message);
		}

		if (log_message != null) {
			if (storedQueryImpl == null)
				log_message.addOtherParam("storedQueryImpl  not defined for query id = ", query_id);
			else
				log_message.addOtherParam("storedQueryImpl", storedQueryImpl.getClass().getName());
		}

		return this;
	}

	
	// these are not used - yet
	@Override
	public Metadata FindDocuments(StoredQuerySupport sqs) throws XdsException,
			LoggerException, XDSRegistryOutOfResourcesException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata FindFolders(StoredQuerySupport sqs) throws XdsException,
			LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata FindSubmissionSets(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetAssociations(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetDocuments(StoredQuerySupport sqs) throws XdsException,
			LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetDocumentsAndAssociations(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetFolderAndContents(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetFolders(StoredQuerySupport sqs) throws XdsException,
			LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetFoldersForDocument(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetRelatedDocuments(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetSubmissionSetAndContents(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Metadata GetSubmissionSets(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		// TODO Auto-generated method stub
		return null;
	}

}
