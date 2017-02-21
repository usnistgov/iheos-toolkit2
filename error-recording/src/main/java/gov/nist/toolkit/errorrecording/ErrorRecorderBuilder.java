package gov.nist.toolkit.errorrecording;

/**
 * Interface for classes that are factories that build ErrorRecorders
 * @author bill
 *
 */
public interface ErrorRecorderBuilder {

	public ErrorRecorder buildNewErrorRecorder();  // Used by V2
	public ErrorRecorder buildNewErrorRecorder(Object o); // Used by V3
}
