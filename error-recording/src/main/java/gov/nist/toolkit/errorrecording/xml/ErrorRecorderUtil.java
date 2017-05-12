package gov.nist.toolkit.errorrecording.xml;

import gov.nist.toolkit.errorrecording.IErrorRecorder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bill on 6/25/15.
 */
public class ErrorRecorderUtil {

    // ErrorRecorders are linked to form a tree.  This flattens the tree by doing
    // a depth first walk and returning the subsequent list
    static public List<IErrorRecorder> errorRecorderChainAsList(IErrorRecorder er) {
        return errorRecorderChainAsList(er, new ArrayList<IErrorRecorder>());
    }

    static private List<IErrorRecorder> errorRecorderChainAsList(IErrorRecorder er, List<IErrorRecorder> lst) {
        lst.add(er);
        for (IErrorRecorder er1 : er.getChildren()) {
            errorRecorderChainAsList(er1, lst);
        }
        return lst;
    }

    static public boolean hasErrors(IErrorRecorder er) {
        List<IErrorRecorder> erl = errorRecorderChainAsList(er);
        for (IErrorRecorder er1: erl) {
            if (er1.getNbErrors() > 0)
                return true;
        }
        return false;
    }
}
