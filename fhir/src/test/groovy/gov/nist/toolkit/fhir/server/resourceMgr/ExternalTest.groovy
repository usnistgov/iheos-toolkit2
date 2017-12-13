package gov.nist.toolkit.fhir.server.resourceMgr

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.installation.Installation
import org.hl7.fhir.dstu3.model.Bundle
import org.hl7.fhir.dstu3.model.Observation
import org.hl7.fhir.dstu3.model.Patient
import spock.lang.Shared
import spock.lang.Specification

class ExternalTest extends Specification {
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
        mgr.addResourceCacheMgr(TestResourceCacheFactory.getResourceCacheMgr())

        // resources returned as [url, resource]
        expect:
        mgr.getResourcesByType(Observation.simpleName).size() == 1

        mgr.getResourcesByType(Observation.simpleName)[0][0].toString() == 'http://example.org/fhir/Observation/123'

        when:
        Installation.instance().setTestRunning(true)
        mgr.currentResource(mgr.getResourcesByType(Observation.simpleName)[0][1])

        then:
        mgr.fullUrl.toString() == 'http://example.org/fhir/Observation/123'

        when:
        def (url, resource) = mgr.resolveReference(mgr.fullUrl, new URI(null, 'Patient/23', null), new ResolverConfig())

        then:
        resource
        resource.class.simpleName == Patient.simpleName
        url.toString() == 'http://example.org/fhir/Patient/23'
    }
}
