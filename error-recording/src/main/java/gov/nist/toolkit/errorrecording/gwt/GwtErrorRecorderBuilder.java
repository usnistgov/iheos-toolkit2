package gov.nist.toolkit.errorrecording.gwt;

import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorder;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.ErrorRecorderBuilder;


public class GwtErrorRecorderBuilder implements ErrorRecorderBuilder {

    @Override
	public GwtErrorRecorder buildNewErrorRecorder() {
		GwtErrorRecorder rec =  new GwtErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

	@Override
	public ErrorRecorder buildNewErrorRecorder(Object o) {
		return null;
	}

//    @Override
//	public GwtErrorRecorder buildNewErrorRecorder(ErrorRecorder parent) {
//		GwtErrorRecorder rec =  new GwtErrorRecorder();
//		rec.errorRecorderBuilder = this;
//		parent.getChildren().add(rec);
//		return rec;
//	}

}
