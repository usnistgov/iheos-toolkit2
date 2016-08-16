package gov.nist.toolkit.valregmetadata.newtop;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

public class MetadataValidator {
	Metadata m;
	ValidationContext vc;
	RegistryValidationInterface rvi;

	public MetadataValidator(Metadata m, ValidationContext vc, RegistryValidationInterface rvi) {
		this.m = m;
		this.vc = vc;
		this.rvi = rvi;
	}

	public void run(ErrorRecorder er) {
		new ObjectStructureValidator(m, vc, rvi).run(er);
		new CodeValidation(m, vc, er).run();
		new SubmissionStructure(m, rvi).run(er, vc);
	}

	public void runCodeValidation(ErrorRecorder er)   {
		new CodeValidation(m, vc, er).run();
	}

}
