package gov.nist.toolkit.simcoresupport.mhd

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.server.resourceMgr.ResourceCacheMgr
import gov.nist.toolkit.installation.Installation
import org.hl7.fhir.dstu3.model.DocumentReference
import spock.lang.Shared
import spock.lang.Specification

class ReplaceTest extends Specification {
    @Shared FhirContext context = ToolkitFhirContext.get()
    ResourceCacheMgr cacheMgr = new ResourceCacheMgr()
    MhdGenerator gen = new MhdGenerator(null, cacheMgr)

    def setup() {
        Installation.setTestRunning(true)
    }

    def origDocRef = '''
<DocumentReference>
    <identifier>
        <system value="urn:ietf:rfc:3986"/>
        <use value="official"/>
        <value value="urn:uuid:1e404af3-077f-4bee-b7a6-a9be97e1ce34"/>
    </identifier>
    <content>
        <attachment>
            <contentType value="text/plain"/>
            <url value="http://localhost:9556/svc/fhir/Binary/1e404af3-077f-4bee-b7a6-a9be97e1ce32"/>
        </attachment>
    </content>
</DocumentReference>
'''
    def replacement = '''
<DocumentReference>
    <relatesTo>
        <target value="http://home.com/fhir/DocumentReference/1"/>
    </relatesTo>
    <content>
        <attachment>
            <contentType value="text/plain"/>
            <url value="http://localhost:9556/svc/fhir/Binary/1e404af3-077f-4bee-b7a6-a9be97e1ce32"/>
        </attachment>
    </content>
</DocumentReference>
'''

    def 'rplc'() {
        given:
        DocumentReference orig = context.newXmlParser().parseResource(origDocRef)
//        DocumentReference rplc = context.newXmlParser().parseResource(replacement)
        String origFullUrl = 'http://home.com/fhir/DocumentReference/1'
//        String rplcFullUrl = 'http://home.com/fhir/DocumentReference/2'

        when: 'install original in cache so MhdGenerator find it'
        cacheMgr.addMemoryCacheElement(origFullUrl, orig)
//        cacheMgr.addMemoryCacheElement(rplcFullUrl, rplc)

        and: 'translate to XDS'
        def eoString = Utils.DocumentReferenceToExtrinsicObject(gen, 'http://home.com/fhir/DocumentReference/1', replacement)
        println eoString
        def eo = new XmlSlurper().parseText(eoString)

        then:
        eo

    }
}
