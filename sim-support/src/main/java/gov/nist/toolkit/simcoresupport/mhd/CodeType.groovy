package gov.nist.toolkit.simcoresupport.mhd

class CodeType {
    String name
    String classScheme
    def codes = []

    CodeType(xml) {
        name = xml.@name
        classScheme = xml.@classScheme
    }
}
