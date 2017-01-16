package gov.nist.toolkit.docAdmin.attType

/**
 *
 */
class Code extends Attribute {
    String codeValue
    String codeName
    String codeSystem

    String toString() {
        "name=${codeName} value=${codeValue} system=${codeSystem}"
    }

    int compareTo(o) {
        if (!(o instanceof Code)) return -1
        Code c = (Code) o
        def i = name.compareTo(c.name)
        if (i != 0) return i;
        i = codeValue.compareTo(c.codeValue)
        if (i != 0) return i;
        i = codeName.compareTo(c.codeName)
        if (i != 0) return i;
        i = codeSystem.compareTo(c.codeSystem)
        if (i != 0) return i;
        return 0
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false
        if (!super.equals(o)) return false

        Code code = (Code) o

        if (codeName != code.codeName) return false
        if (codeSystem != code.codeSystem) return false
        if (codeValue != code.codeValue) return false

        return true
    }

    int hashCode() {
        int result = super.hashCode()
        result = 31 * result + (codeValue != null ? codeValue.hashCode() : 0)
        result = 31 * result + (codeName != null ? codeName.hashCode() : 0)
        result = 31 * result + (codeSystem != null ? codeSystem.hashCode() : 0)
        return result
    }
}
