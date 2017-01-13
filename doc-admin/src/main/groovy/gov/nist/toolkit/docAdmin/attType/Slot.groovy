package gov.nist.toolkit.docAdmin.attType

import gov.nist.toolkit.docAdmin.util.ListUtil

/**
 *
 */
class Slot extends Attribute {
    List<String> values

    String toString() {
        "Identifer: name=${name} value=${values}"
    }

    int compareTo(o) {
        if (!(o instanceof Slot)) return -1
        Slot s = (Slot) o
        def i = name.compareTo(s.name)
        if (i != 0) return i
        return ListUtil.compare(values, s.values)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        if (!super.equals(o)) return false

        Slot slot = (Slot) o

        if (values != slot.values) return false

        return true
    }

    int hashCode() {
        int result = super.hashCode()
        result = 31 * result + (values != null ? values.hashCode() : 0)
        return result
    }
}
