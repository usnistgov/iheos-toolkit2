package gov.nist.toolkit.errorrecording.common;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
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
public class ErrorRecorderFactory {

    public ErrorRecorderFactory(){
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

    /**
     * Factory call that returns a new ErrorRecorder matching the current registered ErrorRecorderType in
     * SelectedErrorRecorder class.
     * @return a custom ErrorRecorder
     */
    public IErrorRecorder getNewErrorRecorder() {
        return getBuilder().buildNewErrorRecorder();
    }

    /**
     * Factory call that returns the ErrorRecorderBuilder matching the current registered ErrorRecorderType in
     * SelectedErrorRecorder class.
     * @return a custom ErrorRecorderBuilder
     */
    private IErrorRecorderBuilder getBuilder(){
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
