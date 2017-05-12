package gov.nist.toolkit.valregmetadata.object;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.valsupport.client.ValidationContext;

import java.util.Set;

public interface TopLevelObject {
	public void validate(IErrorRecorder er, ValidationContext vc, Set<String> knownIds);
}
