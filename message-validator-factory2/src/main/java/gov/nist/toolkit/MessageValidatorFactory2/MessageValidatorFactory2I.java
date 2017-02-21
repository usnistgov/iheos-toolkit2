package gov.nist.toolkit.MessageValidatorFactory2;

import gov.nist.toolkit.errorrecording.ErrorRecorderBuilder;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.registry.RegistryValidationInterface;

public interface MessageValidatorFactory2I {
	public MessageValidatorEngine getValidator(ErrorRecorderBuilder erBuilder, byte[] input, byte[] directCertInput, ValidationContext vc, RegistryValidationInterface rvi);

}
