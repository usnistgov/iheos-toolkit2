package gov.nist.toolkit.docAdmin.operator

import gov.nist.toolkit.docAdmin.objectType.Delta
import gov.nist.toolkit.docAdmin.objectType.MObject

/**
 *
 */
class MCompare implements Comparison {
    @Override
    Delta compare(MObject from, MObject to) {
        Delta d = new Delta()
        d.from = from
        d.to = to



        return d
    }
}
