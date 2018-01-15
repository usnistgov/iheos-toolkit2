package gov.nist.toolkit.simcoresupport.mhd

import ca.uhn.fhir.context.FhirContext
import gov.nist.toolkit.fhir.context.ToolkitFhirContext
import groovy.xml.MarkupBuilder
import org.hl7.fhir.dstu3.model.DocumentReference

class Utils {

    static String DocumentReferenceToExtrinsicObject(MhdGenerator gen, String fullUrl, String docRef) {
        FhirContext context = ToolkitFhirContext.get()

        def writer = new StringWriter()
        def builder = new MarkupBuilder(writer)
        DocumentReference dr = context.newXmlParser().parseResource(docRef)

        gen.addExtrinsicObject(builder, fullUrl, dr)
        writer.toString()
    }

}
