package gov.nist.toolkit.testengine.assertionEngine


class XdsModelValidationSummary {
    int objectsProcessed = 0
    List<XdsModelValidationResult> violations = []

    XdsModelValidationSummary(XdsModelValidationResults results, int totalNumberObjects) {
        objectsProcessed = totalNumberObjects
        results.list.each { XdsModelValidationResult result ->
            if (result.hasErrors())
                violations << result
        }
    }

    @Override
    String toString() {
        StringBuilder buf = new StringBuilder()

        buf.append("Summary\n")
        buf.append("Processed $objectsProcessed objects\n")
        buf.append("Found ${violations.size()} violations:\n")
        violations.each { XdsModelValidationResult result ->
            buf.append(result.validater.getClass().simpleName).append(' ')
        }
        buf.append('\n')

        return buf.toString()
    }
}
