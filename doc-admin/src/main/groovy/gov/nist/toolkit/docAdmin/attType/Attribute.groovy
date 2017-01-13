package gov.nist.toolkit.docAdmin.attType

/**
 *
 */
abstract class Attribute implements Comparable {
    String name

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        Attribute attribute = (Attribute) o

        if (name != attribute.name) return false

        return true
    }

    int hashCode() {
        return (name != null ? name.hashCode() : 0)
    }

     @Override
     int compareTo(Object o) {
         if (o instanceof  Attribute) {
             Attribute a = (Attribute) o
             return name.compareTo(a.name)
         }
         return -1
     }
 }
