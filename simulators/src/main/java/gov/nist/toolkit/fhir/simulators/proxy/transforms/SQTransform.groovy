package gov.nist.toolkit.fhir.simulators.proxy.transforms

import groovy.xml.MarkupBuilder

class SQTransform {
    // TODO - test encoding

    /**
     *
     * @param query is param1=value1;param2=value2...
     * @return
     */
    String run(String query) {
        List params = query.split(';')
        Map model = new SQParamTransform().run(params)
        return toXml(model, true)
    }

    String toXml(Map theModel, boolean leafClass) {
        Map model = [:] << theModel  // copy
        String queryType = model[SQParamTransform.queryType][0]
        model.remove(SQParamTransform.queryType)

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)

        xml.AdhocQueryRequest {
            ResponseOption(returnComposedObjects:'true', returnType:(leafClass) ? 'LeafClass' : 'ObjectRef')
            AdhocQuery(id:queryType) {
                model.each { paramName, paramValues ->
                    Slot(name: paramName) {
                        ValueList() {
                            paramValues.each { paramValue ->
                                if (SQParamTransform.codedTypes.contains(paramName))
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
