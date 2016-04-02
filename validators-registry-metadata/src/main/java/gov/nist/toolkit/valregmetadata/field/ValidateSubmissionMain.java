package gov.nist.toolkit.valregmetadata.field;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.valregmetadata.top.MetadataValidator;
import gov.nist.toolkit.valregmetadata.top.ObjectStructureValidator;
import gov.nist.toolkit.valregmetadata.top.SubmissionStructure;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;

import java.io.File;

public class ValidateSubmissionMain  {

	static public void main(String[] args) {
		Metadata m;
		ValidateSubmissionMain main = new ValidateSubmissionMain();
		String sampleDir = "/Users/bill/dev/sampleSubmissions/";
		
		ErrorRecorder er = new TextErrorRecorder();

		try {
			m = MetadataParser.parseNonSubmission(new File(sampleDir + args[0]));
			ValidationContext vc = DefaultValidationContextFactory.validationContext();
			vc.isR = true;
			vc.skipInternalStructure = true;
			MetadataValidator mv = new MetadataValidator(m, vc, null);
			new ObjectStructureValidator(m, vc, null).run(er);
			mv.runCodeValidation(er);
			new SubmissionStructure(m, null).run(er, vc);
			er.finish();

		} catch (Exception e) {
			er.err(XdsErrorCode.Code.XDSRegistryMetadataError, e);
		}
		
		er.showErrorInfo();
	}
	

}
