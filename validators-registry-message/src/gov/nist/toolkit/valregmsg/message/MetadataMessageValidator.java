package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.valregmetadata.field.MetadataValidator;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.MessageValidator;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

import org.apache.axiom.om.OMElement;

public class MetadataMessageValidator extends MessageValidator {
	OMElement xml;
	Metadata m = null;
	ErrorRecorderBuilder erBuilder;
	MessageValidatorEngine mvc;
	RegistryValidationInterface rvi;

	public Metadata getMetadata() { return m; }

//	public MetadataMessageValidator(ValidationContext vc, OMElement xml) {
//		super(vc);
//		this.xml = xml;
//	}
	
	public MetadataMessageValidator(ValidationContext vc, OMElement xml, ErrorRecorderBuilder erBuilder, MessageValidatorEngine mvc, RegistryValidationInterface rvi) {
		super(vc);
		this.erBuilder = erBuilder;
		this.mvc = mvc;
		this.xml = xml;
		this.rvi = rvi;
	}
	
	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		
		if (xml == null) {
			er.err(XdsErrorCode.Code.XDSRegistryError, "MetadataMessageValidator: top element null", this, "");
			return;
		}
		
		

		try {
			m = MetadataParser.parseNonSubmission(xml);
			
			// save on validation stack so others can find it if they need it
			mvc.addMessageValidator("MetadataContainer", new MetadataContainer(vc, m), erBuilder.buildNewErrorRecorder());

			
			contentSummary(er, m);
			
			MetadataValidator mv = new MetadataValidator(m, vc, rvi);
			mv.runObjectStructureValidation(er);
			mv.runCodeValidation(er);
			mv.runSubmissionStructureValidation(er);
			

		} catch (Exception e) {
			er.err(XdsErrorCode.Code.XDSRegistryError, e);
		}

		er.finish();

	}
	
	static public void contentSummary(ErrorRecorder er, Metadata m) {
		er.detail("**Metadata Validation**");
		er.sectionHeading("Content Summary");
		er.detail(m.getSubmissionSetIds().size() + " SubmissionSets");
		er.detail(m.getExtrinsicObjectIds().size() + " DocumentEntries");
		er.detail(m.getFolderIds().size() + " Folders");
		er.detail(m.getAssociationIds().size() + " Associations");
		if (m.getSubmissionSetIds().size() == 0 &&
				m.getExtrinsicObjectIds().size() == 0 &&
				m.getFolderIds().size() == 0 &&
				m.getAssociationIds().size() == 0)
			er.detail(m.getObjectRefIds().size() + " ObjectRefs");
	}

}
