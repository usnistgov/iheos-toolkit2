package gov.nist.toolkit.errorrecording.factories;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

/**
 * Interface for classes that are factories that build ErrorRecorders
 * @author bill
 *
 */
public interface ErrorRecorderBuilder {

	public ErrorRecorder buildNewErrorRecorder();
}
