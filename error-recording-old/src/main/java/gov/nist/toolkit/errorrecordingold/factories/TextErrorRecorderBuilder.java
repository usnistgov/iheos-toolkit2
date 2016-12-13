package gov.nist.toolkit.errorrecordingold.factories;

import gov.nist.toolkit.errorrecordingold.ErrorRecorder;
import gov.nist.toolkit.errorrecordingold.GwtErrorRecorder;
import gov.nist.toolkit.errorrecordingold.TextErrorRecorder;

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

	public TextErrorRecorder buildNewErrorRecorder(ErrorRecorder parent) {
		TextErrorRecorder rec =  new TextErrorRecorder();
		rec.errorRecorderBuilder = this;
		parent.getChildren().add(rec);
		return rec;
	}

}
