package gov.nist.toolkit.errorrecording.common;

import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.IErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.SelectedErrorRecorder;
import gov.nist.toolkit.errorrecording.gwt.GwtErrorRecorderBuilder;
import gov.nist.toolkit.errorrecording.xml.XMLErrorRecorderBuilder;

import static gov.nist.toolkit.errorrecording.SelectedErrorRecorder.ErrorRecorderType.GWT_ERROR_RECORDER;
import static gov.nist.toolkit.errorrecording.SelectedErrorRecorder.ErrorRecorderType.UNDEFINED;
import static gov.nist.toolkit.errorrecording.SelectedErrorRecorder.ErrorRecorderType.XML_ERROR_RECORDER;

/** Factory that creates on-demand ErrorRecorders matching the current registered ErrorRecorderType in
 * SelectedErrorRecorder class. Singleton type of access.
 * @see SelectedErrorRecorder
 * Created by diane on 5/12/2017.
 */
public class ErrorRecorderFactory {
    private static ErrorRecorderFactory factory = null;

    private ErrorRecorderFactory(){
    }

    /**
     * Singleton getter
     */
    public static ErrorRecorderFactory getErrorRecorderFactory(){
        if (factory == null){
            factory = new ErrorRecorderFactory();
        }
        return factory;
    }

    /**
     * Factory call that returns a new ErrorRecorder matching the current registered ErrorRecorderType in
     * SelectedErrorRecorder class. This function may return null if the ErrorRecorder type is not declared when
     * starting a validation run.
     * @see SelectedErrorRecorder
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
