package gov.nist.toolkit.testengine.assertionEngine

class XdsModelValidationResults {
    List<XdsModelValidationResult> list = []

    XdsModelValidationResults(List<XdsModelValidationResult> list) {
        this.list = list
    }

    Set<AbstractXdsModelValidater> getFailingValidaters() {
        list.findAll {XdsModelValidationResult result ->
            result.hasErrors()
        }.collect { XdsModelValidationResult theResult ->
            theResult.validater
        }
    }

    Set<String> getFailingValidaterNames() {
        getFailingValidaters().collect { AbstractXdsModelValidater validater ->
            validater.getClass().simpleName
        } as Set<String>
    }

    String toString() {
        toString(XdsModelValidationResult.Level.ALL)
    }

    String toString(XdsModelValidationResult.Level level) {
        StringBuilder buf = new StringBuilder()

        list.each { buf.append(it.toString(level)).append('\n')}

        return buf.toString()
    }
}
