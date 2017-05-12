package gov.nist.toolkit.errorrecording;

/**
 * Interface for classes that are factories that build ErrorRecorders
 * @author bill
 *
 */
public interface IGWTErrorRecorderBuilder {

	public IErrorRecorder buildNewErrorRecorder();  // Used by V2
	public IErrorRecorder buildNewErrorRecorder(Object o); // Used by V3
}
