package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode.Code;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.sim.reg.sq.SQFactory;
import gov.nist.toolkit.valregmsg.registry.AdhocQueryResponse;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQuery;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;

import org.apache.axiom.om.OMElement;

abstract public class GatewaySimulatorCommon extends AbstractDsActorSimulator {
	
	public GatewaySimulatorCommon(SimCommon common, DsSimCommon dsSimCommon) {
		super(common, dsSimCommon);
	}

	public  boolean validateHomeCommunityId(ErrorRecorder er, OMElement query, boolean isRG) {
		// verify that if Patient ID param not present then homeCommunityId is in header
		// and homeCommunityId is in proper format
		
		
		StoredQueryFactory fact = null;
		boolean hasHome;
		try {
			fact = new SQFactory(query, new AdhocQueryResponse(Response.version_3), null);
			hasHome = fact.hasHome();
		} catch (Exception e) {
			er.err(Code.XDSRegistryError, e);
			dsSimCommon.sendErrorsInRegistryResponse(er);
			return false;
		}
		
		
		StoredQuery sq = fact.getImpl();
		if (sq == null)
			try {
				throw new Exception("Stored Query not implemented");
			} catch (Exception e1) {
				er.err(Code.XDSRegistryError, e1);
				dsSimCommon.sendErrorsInRegistryResponse(er);
				return false;
			}
		StoredQuerySupport sqs = sq.getStoredQuerySupport();
		
		boolean hasPatientIdParm = sqs.hasPatientIdParameter();
		
		if (!hasPatientIdParm && !hasHome) {
			er.err(Code.XDSMissingHomeCommunityId, "Non-PatientID query and home is not specified", 
					(isRG) ? "RGActorSimulator" : "IGActorSimulator", 
					"ITI TF-2b: 3.38.4.1");
			dsSimCommon.sendErrorsInRegistryResponse(er);
			return false;
		}
		
		return true;
	}
	

}
