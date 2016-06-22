package gov.nist.toolkit.errorrecording;

import gov.nist.toolkit.errorrecording.factories.ErrorRecorderBuilder;


public class XMLErrorRecorderBuilder implements ErrorRecorderBuilder {

    @Override
	public XMLErrorRecorder buildNewErrorRecorder() {
		XMLErrorRecorder rec =  new XMLErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

	@Override
	public ErrorRecorder buildNewErrorRecorder(Object o) {
		return null;
	}

}
