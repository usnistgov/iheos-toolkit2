package gov.nist.toolkit.simulators.sim.reg.sq;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.sim.reg.AdhocQueryResponseGeneratingSim;
import gov.nist.toolkit.simulators.support.DsSimCommon;
import gov.nist.toolkit.simulators.support.MetadataGeneratingSim;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valregmsg.message.SoapMessageValidator;
import gov.nist.toolkit.valregmsg.registry.AdhocQueryResponse;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQuery;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.MetadataException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class SqSim  extends TransactionSimulator implements MetadataGeneratingSim, AdhocQueryResponseGeneratingSim {
	DsSimCommon dsSimCommon;
	AdhocQueryResponse response;
	Metadata m = new Metadata();
	Exception startUpException = null;
	Logger logger = Logger.getLogger(SqSim.class);

	public SqSim(SimCommon common, DsSimCommon dsSimCommon) {
		super(common, null);
		this.dsSimCommon = dsSimCommon;
		
		vc.hasSoap = true;
		vc.isSQ = true;
		vc.isRequest = true;
		vc.updateable = false;
		
		// build response
		try {
			response = new AdhocQueryResponse(Response.version_3);
		} catch (Exception e) {
			System.out.println(ExceptionUtil.exception_details(e));
			startUpException = e;
			return;
		}
	}
	
	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;

		if (startUpException != null)
			er.err(XdsErrorCode.Code.XDSRegistryError, startUpException);

		// if request didn't validate, return so errors can be reported
		if (common.hasErrors()) {
			try {
				response.add(dsSimCommon.getRegistryErrorList(), null);
			} catch (XdsInternalException e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e);
			}
			return;
		}
		
		// run stored query
		try {
			SoapMessageValidator smv = (SoapMessageValidator) common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
			OMElement ahqr = smv.getMessageBody();
			
			StoredQueryFactory fact = null;
			fact = new SQFactory(ahqr, response, null);
			StoredQuery sq = fact.getImpl();
			if (sq == null)
				throw new Exception("Stored Query not implemented");
			StoredQuerySupport sqs = sq.getStoredQuerySupport();
			
			// only appropriate for original implementation
			sqs.runEndProcessing = false;
			linkSqToRegIndex(sq);
			
			Metadata mr = sq.run();

			if (verifySinglePatientId(mr))
				m.copy(mr);

			er.detail("SQ contents: " + m.structure());
			
			List<OMElement> results = m.getAllObjects(); // everything but ObjectRefs
			results.addAll(m.getObjectRefs());
			response.addQueryResults(results, false);
		} catch (Exception e) {
			String msg = e.getMessage();
			if (msg == null || msg.equals(""))
				msg = ExceptionUtil.exception_details(e);
			logger.error(msg);
			er.err(XdsErrorCode.Code.XDSRegistryError, msg, this, null);
		}

	}

	boolean verifySinglePatientId(Metadata m) throws MetadataException {
		Set<String> pids = new HashSet<>();
		for (OMElement ele : m.getAllObjects()) {
			String pid = m.getPatientId(ele);
			if (pid == null) continue;
			pids.add(pid);
		}
		if (pids.size() > 1) {
			er.err(XdsErrorCode.Code.XDSResultNotSinglePatient, "Submission contains " + pids.size() + " Patient IDs", this, null);
			return false;
		}
		return true;
	}
	
	void linkSqToRegIndex(StoredQuery sq) throws XdsInternalException {
		if (sq instanceof FindDocumentsSim) {
			FindDocumentsSim sim = (FindDocumentsSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof FindSubmissionSetsSim) {
				FindSubmissionSetsSim sim = (FindSubmissionSetsSim) sq;
				sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof FindFoldersSim) {
			FindFoldersSim sim = (FindFoldersSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetDocumentsSim) {
			GetDocumentsSim sim = (GetDocumentsSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetRelatedDocumentsSim) {
			GetRelatedDocumentsSim sim = (GetRelatedDocumentsSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetSubmissionSetsSim) {
			GetSubmissionSetsSim sim = (GetSubmissionSetsSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetAssociationsSim) {
			GetAssociationsSim sim = (GetAssociationsSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetDocumentsAndAssociationsSim) {
			GetDocumentsAndAssociationsSim sim = (GetDocumentsAndAssociationsSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetFoldersSim) {
			GetFoldersSim sim = (GetFoldersSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetFoldersForDocumentSim) {
			GetFoldersForDocumentSim sim = (GetFoldersForDocumentSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetSubmissionSetAndContentsSim) {
			GetSubmissionSetAndContentsSim sim = (GetSubmissionSetAndContentsSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetFolderAndContentsSim) {
			GetFolderAndContentsSim sim = (GetFolderAndContentsSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else if (sq instanceof GetAllSim) {
			GetAllSim sim = (GetAllSim) sq;
			sim.setRegIndex(dsSimCommon.regIndex);
		} else {
			throw new XdsInternalException("Internal Error: " + sq.getClass().getCanonicalName() + " is not linked to Registry Index");
		}
	}


	public Metadata getMetadata() {
		return m;
	}

	public AdhocQueryResponse getAdhocQueryResponse() {
		return response;
	}

	public Response getResponse() {
		return response;
	}


}
