package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrymetadata.MetadataUtil;
import gov.nist.toolkit.valregmetadata.field.MetadataValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate metadata update request messages
 * @author bill
 *
 */
public class UpdateRequestValidator extends MessageValidator {
	OMElement xml;
	Metadata m = null;
	RegistryValidationInterface rvi;

	public UpdateRequestValidator(ValidationContext vc, OMElement xml, RegistryValidationInterface rvi) {
		super(vc);
		this.xml = xml;
		this.rvi = rvi;
	}
	
	void err(String msg, String ref) {
		er.err(XdsErrorCode.Code.XDSRegistryMetadataError, msg, this, ref);
	}
	
	void err(Exception e) {
		er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
	}


	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);
		if (xml == null) {
			err("UpdateRequestValidator: top element null", "");
            er.unRegisterValidator(this);
			return;
		}

		try {
			m = MetadataParser.parseNonSubmission(xml);

			MetadataMessageValidator.contentSummary(er, m);

			MetadataValidator mv = new MetadataValidator(m, vc, rvi);
			mv.runObjectStructureValidation(er);
			mv.runCodeValidation(er);
			mv.runSubmissionStructureValidation(er);


			if (m.getSubmissionSets().size() == 0) {
				err("Cannot validate Update Request, no SubmissionSet present","ITI TF-2b: 3.57.4.1.3.1 Rule 1");
                er.unRegisterValidator(this);
				return;
			}
			else if (m.getSubmissionSets().size() > 1) {
				err("Cannot validate Update Request, multiple SubmissionSets present","ITI TF-2b: 3.57.4.1.3.1 Rule 1");
                er.unRegisterValidator(this);
				return;
			}
			
			

			// these assocs rooted on SubmissionSet
			List<OMElement> ssHasMemberAssocs;
			List<OMElement> ssOtherAssocs;

			ssHasMemberAssocs = getSSHasMemberAssocs(m);
			ssOtherAssocs = getSSOtherAssocs(m);
			
			allObjectsHaveId();
			noInitialVersionDocEntries();
			noInitialVersionFols();
			onlyInitialVersionAssocs();
			
		} catch (Exception e) {
			err(e);
		}
        finally {
            er.unRegisterValidator(this);
        }

		er.finish();

	}
	
	void allObjectsHaveId() {
		for (OMElement e : m.getAllObjects()) {
			String id = m.getId(e);
			if (id == null || id.equals(""))
				err(MetadataUtil.formatObjectIdentity(e) + " does not have a id attribute", "ITI TF-2b: 3.57.4.1.3.1 Rule 9");
		}
	}

	void noInitialVersionDocEntries() {
		for (OMElement deEle : m.getExtrinsicObjects()) {
			String id = m.getId(deEle);
			String lid = m.getLid(deEle);
			
			if (lid == null)
				lid = "";
			
			if (lid.equals("") || lid.equals(id)) 
				err(MetadataUtil.formatObjectIdentity(deEle) + " is an initial version (id == lid or lid == null", "ITI TF-2b: 3.57.4.1.3.1 Rule 2");
		}
	}

	void noInitialVersionFols() {
		for (OMElement folEle : m.getFolders()) {
			String id = m.getId(folEle);
			String lid = m.getLid(folEle);
			
			if (lid == null)
				lid = "";
			
			if (lid.equals("") || lid.equals(id)) 
				err(MetadataUtil.formatObjectIdentity(folEle) + " is an initial version (id == lid or lid == null", "ITI TF-2b: 3.57.4.1.3.1 Rule 2");
		}
	}

	void onlyInitialVersionAssocs() {
		for (OMElement aEle : m.getAssociations()) {
			String id = m.getId(aEle);
			String lid = m.getLid(aEle);
			
			if (lid == null)
				lid = "";
			
			if (!lid.equals("") && !lid.equals(id)) 
				err(MetadataUtil.formatObjectIdentity(aEle) + " Associations cannot be updated (lid != null and id != lid", "ITI TF-2b: 3.57.4.1.3.1 Rule 6");
		}
	}

	List<OMElement> getSSOtherAssocs(Metadata m) {
		List<OMElement> assocs = new ArrayList<OMElement>();
		String ssId = m.getSubmissionSetId();

		for (OMElement a : m.getAssociations()) {
			try {
				if (m.getAssocSource(a) == ssId)
					if (!m.getSimpleAssocType(a).equals("HasMember"))
						assocs.add(a);
			} catch (Exception e) {}
		}
		return assocs;
	}

	List<OMElement> getSSHasMemberAssocs(Metadata m) {
		List<OMElement> assocs = new ArrayList<OMElement>();
		String ssId = m.getSubmissionSetId();

		for (OMElement a : m.getAssociations()) {
			try {
				if (m.getAssocSource(a) == ssId)
					if (m.getSimpleAssocType(a).equals("HasMember"))
						assocs.add(a);
			} catch (Exception e) {}
		}
		return assocs;
	}

}
