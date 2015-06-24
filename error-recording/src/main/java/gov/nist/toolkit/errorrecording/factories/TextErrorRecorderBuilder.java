package gov.nist.toolkit.errorrecording.factories;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorder;
import gov.nist.toolkit.errorrecording.TextErrorRecorder;

public class TextErrorRecorderBuilder implements ErrorRecorderBuilder {

	public TextErrorRecorder buildNewErrorRecorder() {
		TextErrorRecorder rec =  new TextErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

	public TextErrorRecorder buildNewErrorRecorder(ErrorRecorder parent) {
		TextErrorRecorder rec =  new TextErrorRecorder();
		rec.errorRecorderBuilder = this;
		parent.getChildren().add(rec);
		return rec;
	}

}
