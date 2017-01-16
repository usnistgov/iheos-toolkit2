package gov.nist.toolkit.docAdmin.attType

/**
 *
 */
class Identifier extends Attribute {
    String value

    String toString() {
        "ID ${name}=${value}"
    }

    int compareTo(o) {
        if (!(o instanceof Identifier)) return -1
        Identifier id = (Identifier) o
        def i = name.compareTo(id.name)
        if (i != 0) return i
        return value.compareTo(id.value)
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        if (!super.equals(o)) return false

        Identifier that = (Identifier) o

        if (value != that.value) return false

        return true
    }

    int hashCode() {
        int result = super.hashCode()
        result = 31 * result + (value != null ? value.hashCode() : 0)
        return result
    }
}
