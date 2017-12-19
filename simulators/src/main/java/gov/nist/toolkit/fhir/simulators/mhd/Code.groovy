package gov.nist.toolkit.fhir.simulators.mhd

class Code {
    String code
    String codingScheme
    String display
    String system
    boolean deprecated

    Code (xml) {
        code = xml.@code
        codingScheme = xml.@codingScheme
        display = xml.@display
        system = xml.@system
        deprecated = xml.@deprecated
    }
}
