package gov.nist.toolkit.fhir.resourceMgr

import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Observation
import org.hl7.fhir.dstu3.model.Patient
import spock.lang.Shared
import spock.lang.Specification

class ContainedTest extends Specification {
    @Shared FhirContext ctx = ResourceCache.ctx

    def 'relative url'() {
        setup:
        def xml = '''
<Bundle>
  <type value="collection"/>
  <entry>
    <fullUrl value="http://example.org/fhir/Observation/123"/>
    <resource>
      <Observation>
        <contained>
          <Patient>
            <id value="23"/>
           </Patient>
         </contained>
         <id value="123"/>
         <subject>
           <!-- this is reference to the first resource above -->
           <reference value="#23"/>
         </subject>
      </Observation>
    </resource>
  </entry>
</Bundle>
'''
        Bundle bundle = ctx.newXmlParser().parseResource(xml)
        ResourceMgr mgr = new ResourceMgr(bundle)

        // resources returned as [url, resource]
        expect:
        mgr.getResourcesByType(Patient.simpleName).size() == 0
        mgr.getResourcesByType(Observation.simpleName).size() == 1

        mgr.getResourcesByType(Observation.simpleName)[0][0] == 'http://example.org/fhir/Observation/123'

        when:
        mgr.currentResource(mgr.getResourcesByType(Observation.simpleName)[0][1])

        then:
        mgr.fullUrl == 'http://example.org/fhir/Observation/123'

        when:
        def (url, resource) = mgr.resolveReference('#23')

        then:
        resource.class.simpleName == Patient.simpleName
        url == '#23'
    }
}
