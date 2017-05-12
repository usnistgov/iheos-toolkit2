package gov.nist.toolkit.errorrecording.gwt;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;


public class GwtErrorRecorderBuilder implements IErrorRecorderBuilder {

    @Override
	public GwtErrorRecorder buildNewErrorRecorder() {
		GwtErrorRecorder rec =  new GwtErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

	@Override
	public IErrorRecorder buildNewErrorRecorder(Object o) {
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
