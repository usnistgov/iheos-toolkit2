package gov.nist.toolkit.errorrecording;

/**
 * Created by diane on 3/9/2017.
 * Indicates the type of the current ErrorRecorder (GWT or XML). For temporary use while refactoring the code. Singleton.
 */

public class SelectedErrorRecorder {
    public enum ErrorRecorderType {UNDEFINED, GWT_ERROR_RECORDER, XML_ERROR_RECORDER};
    static SelectedErrorRecorder selectedErrorRecorder = null;
    ErrorRecorderType type = ErrorRecorderType.UNDEFINED;


    private SelectedErrorRecorder() {
        selectedErrorRecorder = this;
    }

        public static SelectedErrorRecorder getSelectedErrorRecorder(){
        if (selectedErrorRecorder != null) {
            return selectedErrorRecorder;
        }
        return new SelectedErrorRecorder();
    }

    public void setSelectedErrorRecorder(ErrorRecorderType _type){
        type = _type;
    }

    public ErrorRecorderType getType(){
        return type;
    }

}
