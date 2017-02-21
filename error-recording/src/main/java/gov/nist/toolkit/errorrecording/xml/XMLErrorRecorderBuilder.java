package gov.nist.toolkit.errorrecording.xml;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.ErrorRecorderBuilder;


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
