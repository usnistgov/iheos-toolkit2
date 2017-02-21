package gov.nist.toolkit.errorrecording.text;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.ErrorRecorderBuilder;

public class TextErrorRecorderBuilder implements ErrorRecorderBuilder {

	public TextErrorRecorder buildNewErrorRecorder() {
		TextErrorRecorder rec =  new TextErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

	@Override
	public ErrorRecorder buildNewErrorRecorder(Object o) {
		return null;
	}

	/**
	public TextErrorRecorder buildNewErrorRecorder(ErrorRecorder parent) {
		TextErrorRecorder rec =  new TextErrorRecorder();
		rec.errorRecorderBuilder = this;
		parent.getChildren().add(rec);
		return rec;
	 }
	 **/

}
