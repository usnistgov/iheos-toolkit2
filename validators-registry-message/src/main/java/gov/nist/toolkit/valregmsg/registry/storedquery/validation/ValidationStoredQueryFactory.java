package gov.nist.toolkit.valregmsg.registry.storedquery.validation;

import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.client.assertions.Assertion;
import gov.nist.toolkit.errorrecording.client.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XDSRegistryOutOfResourcesException;
import gov.nist.toolkit.xdsexception.XdsException;

import org.apache.axiom.om.OMElement;

public class ValidationStoredQueryFactory extends StoredQueryFactory {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public ValidationStoredQueryFactory(OMElement ahqr) throws XdsException,
			LoggerException {
		super(ahqr);
	}

	public ValidationStoredQueryFactory(OMElement ahqr, Response response) throws XdsException,
			LoggerException {
		super(ahqr, response, null);
	}

	public ValidationStoredQueryFactory(OMElement ahqr, ErrorRecorder er) throws XdsException,
			LoggerException {
		super(ahqr, er);
	}

	@Override
	public Metadata FindDocuments(StoredQuerySupport sqs) throws XdsException,
			LoggerException, XDSRegistryOutOfResourcesException {
		return null;
	}

	@Override
	public Metadata FindFolders(StoredQuerySupport sqs) throws XdsException,
			LoggerException {
		return null;
	}

	@Override
	public Metadata FindSubmissionSets(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		return null;
	}

	@Override
	public Metadata GetAssociations(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		return null;
	}

	@Override
	public Metadata GetDocuments(StoredQuerySupport sqs) throws XdsException,
			LoggerException {
		return null;
	}

	@Override
	public Metadata GetDocumentsAndAssociations(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		return null;
	}

	@Override
	public Metadata GetFolderAndContents(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		return null;
	}

	@Override
	public Metadata GetFolders(StoredQuerySupport sqs) throws XdsException,
			LoggerException {
		return null;
	}

	@Override
	public Metadata GetFoldersForDocument(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		return null;
	}

	@Override
	public Metadata GetRelatedDocuments(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		return null;
	}

	@Override
	public Metadata GetSubmissionSetAndContents(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		return null;
	}

	@Override
	public Metadata GetSubmissionSets(StoredQuerySupport sqs)
			throws XdsException, LoggerException {
		return null;
	}

	public StoredQueryFactory buildStoredQueryHandler(StoredQuerySupport sqs)
			throws MetadataValidationException, LoggerException {
		if (query_id == null) {
			throw new MetadataValidationException("Null Query ID", SqDocRef.QueryID);
		}

		if (query_id.equals(MetadataSupport.SQ_FindDocuments)) {
			setTestMessage("FindDocuments");
			storedQueryImpl = new ValidationFindDocuments(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_FindSubmissionSets)) {
			setTestMessage("FindSubmissionSets");
			storedQueryImpl = new ValidationFindSubmissionSets(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_FindFolders)) {
			setTestMessage("FindFolders");
			storedQueryImpl = new ValidationFindFolders(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetAll)) {
			setTestMessage("GetAll");
			storedQueryImpl = new ValidationGetAll(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetDocuments)) {
			setTestMessage("GetDocuments");
			storedQueryImpl = new ValidationGetDocuments(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFolders)) {
			setTestMessage("GetFolders");
			storedQueryImpl = new ValidationGetFolders(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetAssociations)) {
			setTestMessage("GetAssociations");
			storedQueryImpl = new ValidationGetAssociations(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetDocumentsAndAssociations)) {
			setTestMessage("GetDocumentsAndAssociations");
			storedQueryImpl = new ValidationGetDocumentsAndAssociations(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetSubmissionSets)) {
			setTestMessage("GetSubmissionSets");
			storedQueryImpl = new ValidationGetSubmissionSets(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetSubmissionSetAndContents)) {
			setTestMessage("GetSubmissionSetAndContents");
			storedQueryImpl = new ValidationGetSubmissionSetAndContents(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFolderAndContents)) {
			setTestMessage("GetFolderAndContents");
			storedQueryImpl = new ValidationGetFolderAndContents(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetFoldersForDocument)) {
			setTestMessage("GetFoldersForDocument");
			storedQueryImpl = new ValidationGetFoldersForDocument(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_GetRelatedDocuments)) {
			setTestMessage("GetRelatedDocuments");
			storedQueryImpl = new ValidationGetRelatedDocuments(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_FindDocumentsForMultiplePatients)) {
			setTestMessage("FindDocumentsForMultiplePatients");
			storedQueryImpl = new ValidationFindDocumentsForMultiplePatients(sqs);
		}
		else if (query_id.equals(MetadataSupport.SQ_FindFoldersForMultiplePatients)) {
			setTestMessage("FindFoldersForMultiplePatients");
			storedQueryImpl = new ValidationFindFoldersForMultiplePatients(sqs);
		}
		else {
			setTestMessage(query_id);
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA156");
			String detail = "Query ID: '" + query_id + "'";
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "AdhocQueryRequest.java", detail, log_message);
		}

		if (log_message != null) {
			if (storedQueryImpl == null)
				log_message.addOtherParam("storedQueryImpl  not defined for query id = ", query_id);
			else
				log_message.addOtherParam("storedQueryImpl", storedQueryImpl.getClass().getName());
		}

		return this;
	}

}
