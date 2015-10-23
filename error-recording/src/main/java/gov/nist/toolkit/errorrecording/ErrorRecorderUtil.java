package gov.nist.toolkit.errorrecording;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill on 6/25/15.
 */
public class ErrorRecorderUtil {

    // ErrorRecorders are linked to form a tree.  This flattens the tree by doing
    // a depth first walk and returning the subsequent list
    static public List<ErrorRecorder> errorRecorderChainAsList(ErrorRecorder er) {
        return errorRecorderChainAsList(er, new ArrayList<ErrorRecorder>());
    }

    static private List<ErrorRecorder> errorRecorderChainAsList(ErrorRecorder er, List<ErrorRecorder> lst) {
        lst.add(er);
        for (ErrorRecorder er1 : er.getChildren()) {
            errorRecorderChainAsList(er1, lst);
        }
        return lst;
    }

    static public boolean hasErrors(ErrorRecorder er) {
        List<ErrorRecorder> erl = errorRecorderChainAsList(er);
        for (ErrorRecorder er1: erl) {
            if (er1.getNbErrors() > 0)
                return true;
        }
        return false;
    }
}
