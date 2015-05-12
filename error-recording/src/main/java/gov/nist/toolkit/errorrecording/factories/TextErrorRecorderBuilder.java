package gov.nist.toolkit.errorrecording.factories;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;

public class TextErrorRecorderBuilder implements ErrorRecorderBuilder {

	public ErrorRecorder buildNewErrorRecorder() {
		TextErrorRecorder rec =  new TextErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

}
