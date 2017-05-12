package gov.nist.toolkit.errorrecording.xml;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;


public class XMLErrorRecorderBuilder implements IErrorRecorderBuilder {

    @Override
	public XMLErrorRecorder buildNewErrorRecorder() {
		XMLErrorRecorder rec =  new XMLErrorRecorder();
		rec.errorRecorderBuilder = this;
		return rec;
	}

	@Override
	public IErrorRecorder buildNewErrorRecorder(Object o) {
		return null;
	}

}
