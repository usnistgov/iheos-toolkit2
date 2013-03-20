package gov.nist.toolkit.valsupport.errrec;

import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;


public class GwtErrorRecorderBuilder implements ErrorRecorderBuilder {

	public GwtErrorRecorder buildNewErrorRecorder() {
		GwtErrorRecorder rec =  new GwtErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

}
