package gov.nist.toolkit.docAdmin.operator;

import gov.nist.toolkit.docAdmin.objectType.Delta;
import gov.nist.toolkit.docAdmin.objectType.MObject;

/**
 *
 */
public interface Comparison {
    Delta compare(MObject from, MObject to);
}
