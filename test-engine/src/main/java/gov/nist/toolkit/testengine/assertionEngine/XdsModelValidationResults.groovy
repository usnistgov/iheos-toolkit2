package gov.nist.toolkit.testengine.assertionEngine

class XdsModelValidationResults {
    List<XdsModelValidationResult> list = []

    XdsModelValidationResults(List<XdsModelValidationResult> list) {
        this.list = list
    }


    String toString() {
        StringBuilder buf = new StringBuilder()

        list.each { buf.append(it.toString()).append('\n')}

        return buf.toString()
    }
}
