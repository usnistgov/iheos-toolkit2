package gov.nist.toolkit.fhir.simulators.mhd

import groovy.xml.MarkupBuilder

class SQTranslator {
    // TODO - test encoding

    /**
     *
     * @param query is param1=value1;param2=value2...
     * @return StoredQuery
     */
    String run(String query) {
        List params = query.split(';')
        Map model = new SQParamTranslator().run(params)
        return toXml(model, true)
    }

    private static String toXml(Map theModel, boolean leafClass) {
        Map model = [:] << theModel  // copy
        String queryType = model[SQParamTranslator.queryType][0]
        model.remove(SQParamTranslator.queryType)

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.AdhocQueryRequest {
            ResponseOption(returnComposedObjects:'true', returnType:(leafClass) ? 'LeafClass' : 'ObjectRef')
            AdhocQuery(id:queryType) {
                model.each { paramName, paramValues ->
                    Slot(name: paramName) {
                        ValueList() {
                            paramValues.each { paramValue ->
                                if (SQParamTranslator.codedTypes.contains(paramName))
                                    paramValue = "('${paramValue}')"
                                Value(paramValue)
                            }
                        }
                    }
                }
            }
        }
        return writer.toString()
    }
}
