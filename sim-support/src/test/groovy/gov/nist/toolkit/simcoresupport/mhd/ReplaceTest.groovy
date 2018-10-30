package gov.nist.toolkit.simcoresupport.mhd

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import gov.nist.toolkit.fhir.server.resourceMgr.ResourceCacheMgr
import gov.nist.toolkit.installation.server.Installation
import org.apache.log4j.BasicConfigurator
import org.hl7.fhir.dstu3.model.Binary
import org.hl7.fhir.dstu3.model.DocumentReference
import spock.lang.Shared
import spock.lang.Specification

class ReplaceTest extends Specification {
    @Shared FhirContext context = ToolkitFhirContext.get()
    ResourceCacheMgr cacheMgr = new ResourceCacheMgr()
    MhdGenerator gen = new MhdGenerator(null, cacheMgr)

    def setup() {
        BasicConfigurator.configure()
        Installation.setTestRunning(true)
    }

    def origDocRef = '''
<DocumentReference>
    <id value="1"/>
                <masterIdentifier>
                    <system value="urn:ietf:rfc:3986"/>
                    <value value="urn:oid:1.2.129.6.58.92.88336"/>
                </masterIdentifier>
    <identifier>
        <system value="urn:ietf:rfc:3986"/>
        <use value="official"/>
        <value value="urn:uuid:1e404af3-077f-4bee-b7a6-a9be97e1ce34"/>
    </identifier>
    <content>
        <attachment>
            <contentType value="text/plain"/>
            <url value="http://home.com/fhir/Binary/1"/>
        </attachment>
    </content>
</DocumentReference>
'''
    def replacement = '''
<DocumentReference>
                <masterIdentifier>
                    <system value="urn:ietf:rfc:3986"/>
                    <value value="urn:oid:1.2.129.6.58.92.88336.1"/>
                </masterIdentifier>
    <relatesTo>
        <code value="replaces"/>
        <target>
            <reference value="http://home.com/fhir/DocumentReference/1"/>
        </target> 
    </relatesTo>
    <content>
        <attachment>
            <contentType value="text/plain"/>
            <url value="http://home.com/fhir/Binary/1"/>
        </attachment>
    </content>
</DocumentReference>
'''
    def binary = '''
            <Binary>
                <id value="1e404af3-077f-4bee-b7a6-a9be97e1ce32"/>
                <meta>
                    <lastUpdated value="2013-07-01T13:11:33Z"/>
                </meta>
                <contentType value="text/plain"/>
                <content value="YXNkYXNkYXNkYXNkYXNk"/>
            </Binary>
'''

    def 'rplc'() {
        given:
        DocumentReference orig = context.newXmlParser().parseResource(origDocRef)
        DocumentReference rplc = context.newXmlParser().parseResource(replacement)
        String origFullUrl = 'http://home.com/fhir/DocumentReference/1'
        String binaryFullUrl = 'http://home.com/fhir/Binary/1'
        String rplcFullUrl = 'urn:uuid:1e404af3-077f-4bee-b7a6-a9be97e1ce32'
        Binary binary1 = context.newXmlParser().parseResource(binary)

        when: 'install original in cache so MhdGenerator find it'
        cacheMgr.addMemoryCacheElement(origFullUrl, orig)
        cacheMgr.addMemoryCacheElement(binaryFullUrl, binary1)
//        cacheMgr.addMemoryCacheElement(rplcFullUrl, rplc)

        and: 'build rplc'
        def resourceMap = [:]
        resourceMap[new URI(rplcFullUrl)] = rplc
        resourceMap[new URI(binaryFullUrl)] = binary1

        and: 'submit rplc'
        String rolString = gen.buildRegistryObjectList(resourceMap)
        println rolString
        def rol = new XmlSlurper().parseText(rolString)

        then:
        rol.ExtrinsicObject.@id == rol.Association.@sourceObject
        rol.Association.@targetObject == 'urn:uuid:1e404af3-077f-4bee-b7a6-a9be97e1ce34'


    }
}
