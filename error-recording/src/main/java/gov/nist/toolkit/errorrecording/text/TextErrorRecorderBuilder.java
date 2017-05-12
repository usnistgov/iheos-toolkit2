package gov.nist.toolkit.errorrecording.text;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;

public class TextErrorRecorderBuilder implements IErrorRecorderBuilder {

	public TextErrorRecorder buildNewErrorRecorder() {
		TextErrorRecorder rec =  new TextErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

	@Override
	public IErrorRecorder buildNewErrorRecorder(Object o) {
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
