package gov.nist.toolkit.docAdmin.objectType

import gov.nist.toolkit.docAdmin.attType.Attribute

/**
 *
 */
abstract class MObject {
    List<Attribute> atts = []

    String toString() {
        def names = atts.collect { it.name }.sort()
        StringBuilder b = new StringBuilder()
        b.append(nid()). append(': [')
        b.append(']\n')
        return b.toString()
    }

    // name or id
    private nid() {
        (atts['name']) ? atts[name] : atts['id']
    }

    public MObject add(Attribute a) {
        atts.add(a)
        return this
    }

    public Attribute contains(String attname) {
        Attribute a = atts.find { it.name == attname }
        return a
    }
}
