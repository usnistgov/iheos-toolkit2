package gov.nist.toolkit.fhir.server.resourceMgr

import ca.uhn.fhir.context.FhirContext
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Observation
import org.hl7.fhir.dstu3.model.Patient
import spock.lang.Shared
import spock.lang.Specification

class ReferenceTest extends Specification {
    @Shared FhirContext ctx = ResourceCache.ctx

    def 'relative url'() {
        setup:
        def xml = '''
<Bundle>
  <type value="collection"/>
    <entry>
    <fullUrl value="http://example.org/fhir/Patient/23"/>
    <resource>
      <Patient>
         <id value="23"/>
      </Patient>
    </resource>
  </entry>
  <entry>
    <fullUrl value="http://example.org/fhir/Observation/123"/>
    <resource>
      <Observation>
         <id value="123"/>
         <subject>
           <!-- this is reference to the first resource above -->
           <reference value="Patient/23"/>
         </subject>
      </Observation>
    </resource>
  </entry>
</Bundle>
'''
        Bundle bundle = ctx.newXmlParser().parseResource(xml)
        ResourceMgr mgr = new ResourceMgr(bundle, null)

        // resources returned as [url, resource]
        expect:
        mgr.getResourcesByType(Patient.simpleName).size() == 1
        mgr.getResourcesByType(Observation.simpleName).size() == 1

        mgr.getResourcesByType(Patient.simpleName)[0][0].toString() == 'http://example.org/fhir/Patient/23'
        mgr.isAbsolute(mgr.getResourcesByType(Patient.simpleName)[0][0])

        mgr.getResourcesByType(Observation.simpleName)[0][0].toString() == 'http://example.org/fhir/Observation/123'

        mgr.url(mgr.getResourcesByType(Patient.simpleName)[0][1]).toString() == 'http://example.org/fhir/Patient/23'

        when:
        mgr.currentResource(mgr.getResourcesByType(Observation.simpleName)[0][1])

        then:
        mgr.fullUrl.toString() == 'http://example.org/fhir/Observation/123'

        when:
        def (url, resource) = mgr.resolveReference(new URI(null, 'Patient/23', null))

        then:
        resource.class.simpleName == Patient.simpleName
        url.toString() == 'http://example.org/fhir/Patient/23'
    }

    def 'absolute url'() {
        setup:
        def xml = '''
<Bundle>
  <type value="collection"/>
    <entry>
    <fullUrl value="http://example.org/fhir/Patient/23"/>
    <resource>
      <Patient>
         <id value="23"/>
      </Patient>
    </resource>
  </entry>
  <entry>
    <fullUrl value="http://example.org/fhir/Observation/123"/>
    <resource>
      <Observation>
         <id value="123"/>
         <subject>
           <!-- this is reference to the first resource above -->
           <reference value="http://example.org/fhir/Patient/23"/>
         </subject>
      </Observation>
    </resource>
  </entry>
</Bundle>
'''
        Bundle bundle = ctx.newXmlParser().parseResource(xml)
        ResourceMgr mgr = new ResourceMgr(bundle, null)

        // resources returned as [url, resource]
        expect:
        mgr.getResourcesByType(Patient.simpleName).size() == 1
        mgr.getResourcesByType(Observation.simpleName).size() == 1

        mgr.getResourcesByType(Patient.simpleName)[0][0].toString() == 'http://example.org/fhir/Patient/23'
        mgr.isAbsolute(mgr.getResourcesByType(Patient.simpleName)[0][0])

        mgr.getResourcesByType(Observation.simpleName)[0][0].toString() == 'http://example.org/fhir/Observation/123'

        mgr.url(mgr.getResourcesByType(Patient.simpleName)[0][1]).toString() == 'http://example.org/fhir/Patient/23'

        when:
        mgr.currentResource(mgr.getResourcesByType(Observation.simpleName)[0][1])

        then:
        mgr.fullUrl.toString() == 'http://example.org/fhir/Observation/123'

        when:
        def (url, resource) = mgr.resolveReference(new URI(null,'Patient/23', null))

        then:
        resource.class.simpleName == Patient.simpleName
        url.toString() == 'http://example.org/fhir/Patient/23'
    }
}
