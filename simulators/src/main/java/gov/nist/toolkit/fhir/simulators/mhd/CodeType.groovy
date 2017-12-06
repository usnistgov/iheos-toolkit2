package gov.nist.toolkit.fhir.simulators.mhd

class CodeType {
    String name
    String classScheme
    def codes = []

    CodeType(xml) {
        name = xml.@name
        classScheme = xml.@classScheme
    }
}
