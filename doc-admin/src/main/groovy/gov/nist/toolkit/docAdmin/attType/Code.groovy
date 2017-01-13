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
