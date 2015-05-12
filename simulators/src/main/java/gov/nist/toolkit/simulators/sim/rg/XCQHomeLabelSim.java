package gov.nist.toolkit.simulators.sim.rg;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simulators.support.MetadataGeneratingSim;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.simulators.support.TransactionSimulator;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

import org.apache.axiom.om.OMElement;

/**
 * Decorate SQ output with homeCommunityId
 * @author bill
 *
 */

public class XCQHomeLabelSim extends TransactionSimulator implements MetadataGeneratingSim {

	Metadata m;
	String homeCommunityId;
	
	public XCQHomeLabelSim(SimCommon common, MetadataGeneratingSim mSim, String homeCommunityId) {
		super(common);

		this.m = mSim.getMetadata();
		this.homeCommunityId = homeCommunityId;
	}

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		for (OMElement ele : m.getExtrinsicObjects()) 
			m.setHome(ele, homeCommunityId);
		for (OMElement ele : m.getSubmissionSets())
			m.setHome(ele, homeCommunityId);
		for (OMElement ele : m.getFolders())
			m.setHome(ele, homeCommunityId);
		for (OMElement ele : m.getObjectRefs())
			m.setHome(ele, homeCommunityId);
	}

	public Metadata getMetadata() {
		// TODO Auto-generated method stub
		return m;
	}


}
