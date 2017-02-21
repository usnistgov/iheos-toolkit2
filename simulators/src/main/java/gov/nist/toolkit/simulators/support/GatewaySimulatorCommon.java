package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode.Code;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymsg.registry.Response;
import gov.nist.toolkit.simulators.sim.reg.sq.SQFactory;
import gov.nist.toolkit.valregmsg.registry.AdhocQueryResponse;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQuery;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQueryFactory;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;

import org.apache.axiom.om.OMElement;

abstract public class GatewaySimulatorCommon extends BaseDsActorSimulator {
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();

	public GatewaySimulatorCommon(SimCommon common, DsSimCommon dsSimCommon) {
		super(common, dsSimCommon);
	}

	public GatewaySimulatorCommon() {}

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
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA126");
			String location = (isRG) ? "RGActorSimulator" : "IGActorSimulator";
			String detail = "Parameters found: " + sqs.getParams().getNames();
			er.err(XdsErrorCode.Code.XDSMissingHomeCommunityId, assertion, this, location, detail);
			return false;
		}

		return true;
	}


}
