package gov.nist.toolkit.docAdmin.objectType

import gov.nist.toolkit.docAdmin.attType.Attribute

/**
 *
 */
class Delta {
    MObject from
    MObject to
    List<Attribute> removed
    List<Attribute> added
    List<Attribute> changed
}
