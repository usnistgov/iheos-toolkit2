package gov.nist.toolkit.registrymetadata.deletion.support

import gov.nist.toolkit.registrymetadata.deletion.ErrorType
import gov.nist.toolkit.registrymetadata.deletion.MultiResponse
import gov.nist.toolkit.registrymetadata.deletion.Registry
import gov.nist.toolkit.registrymetadata.deletion.RemoveMetadata
import gov.nist.toolkit.registrymetadata.deletion.Response
import gov.nist.toolkit.registrymetadata.deletion.Uuid
import gov.nist.toolkit.registrymetadata.deletion.objects.RO

import java.lang.reflect.Method
/**
 * Mock Registry engine
 */
class Engine {
    RegistryMock regMock = new RegistryMock()
    Registry reg = regMock
    RemoveMetadata rm = new RemoveMetadata()
    List<Uuid> removeSet
    List<Response> responses = []

    Engine(List<RO> content, List<Uuid> removeSet) {
        regMock.load(content)
        this.removeSet = removeSet
        rm.r = reg
        rm.removeSet = removeSet
    }

    List<String> rules() {
        responses.collect { it.rule }.unique()
    }

    Engine run() {
        Method[] methods = rm.getClass().methods

        removeSet.each { Uuid rmUuid ->
            methods.each { Method method ->
                if (method.name.startsWith('rule')) {
                    println "${method.name} (${rmUuid})"
                    def rspn = method.invoke(rm, rmUuid)
                    if (rspn instanceof Response) {
                        Response response = rspn as Response
                        response.rule = method.name
                        responses.add(response)
                    } else if (rspn instanceof MultiResponse) {
                        MultiResponse mr = rspn as MultiResponse
                        mr.responses.each { it.rule = method.name}
                        responses.addAll(mr.responses)
                    } else {
                        assert false
                    }
                }
            }
        }
        responses = responses.findAll { it.errorType != ErrorType.None }
        return this
    }
}
