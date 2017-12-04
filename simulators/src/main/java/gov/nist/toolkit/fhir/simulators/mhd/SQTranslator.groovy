package gov.nist.toolkit.fhir.simulators.mhd

import groovy.xml.MarkupBuilder

class SQTranslator {
    // TODO - test encoding

    /**
     *
     * @param query is param1=value1;param2=value2...
     * @return StoredQuery
     */
    Map fhirQueryToSQModel(String query) {
        List params = query.split(';')
        new SQParamTranslator().run(params)
//        return toXml(model, true)
    }

    String run(String query) {
        toXml(fhirQueryToSQModel(query), true)
    }

    // model is [queryParamName: [values]]
    static String toXml(Map theModel, boolean leafClass) {
        Map model = [:] << theModel  // copy
        String queryType = model[SQParamTranslator.queryType][0]
        model.remove(SQParamTranslator.queryType)

        def writer = new StringWriter()
        def xml = new MarkupBuilder(writer)
//xmlns:query="urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"
        xml.'query:AdhocQueryRequest'('xmlns:query':'urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0') {
            'query:ResponseOption'(returnComposedObjects:'true', returnType:(leafClass) ? 'LeafClass' : 'ObjectRef')
            'rim:AdhocQuery'('xmlns:rim':'urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0', id:queryType) {
                model.each { paramName, paramValues ->
                    'rim:Slot'(name: paramName) {
                        'rim:ValueList'() {
                            paramValues.each { paramValue ->
                                if (SQParamTranslator.codedTypes.contains(paramName))
                                    paramValue = "('${paramValue}')"
                                else
                                    paramValue = "'${paramValue}'"
                                if (SQParamTranslator.acceptsMultiple.contains(paramName))
                                    paramValue = "(${paramValue})"
                                'rim:Value'(paramValue)
                            }
                        }
                    }
                }
            }
        }
        return writer.toString()
    }
}
