package gov.nist.toolkit.errorrecording.common;

import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.SelectedErrorRecorder;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.xml.XMLErrorRecorderBuilder;

import static gov.nist.toolkit.errorrecording.SelectedErrorRecorder.ErrorRecorderType.GWT_ERROR_RECORDER;
import static gov.nist.toolkit.errorrecording.SelectedErrorRecorder.ErrorRecorderType.UNDEFINED;
import static gov.nist.toolkit.errorrecording.SelectedErrorRecorder.ErrorRecorderType.XML_ERROR_RECORDER;

/**
 * Created by diane on 5/12/2017.
 */
public class ErrorRecorderUtils {

    public ErrorRecorderUtils(){
    }

    public IErrorRecorderBuilder getNewErrorRecorderBuilder() {
        switch (SelectedErrorRecorder.getSelectedErrorRecorder().getType()) {
            case GWT_ERROR_RECORDER:
                return new GwtErrorRecorderBuilder();
            case XML_ERROR_RECORDER:
                return new XMLErrorRecorderBuilder();
            case UNDEFINED:
                return null;
        }
        return null;
    }
}
