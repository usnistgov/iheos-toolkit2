package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import gov.nist.toolkit.fhir.search.BaseQuery
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.hl7.fhir.dstu3.model.Immunization
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.instance.model.api.IBaseResource

class ImmunizationResourceProvider implements IResourceProvider{
    @Override
    Class<? extends IBaseResource> getResourceType() {
        return Immunization.class
    }

    @Create()
    public MethodOutcome createImmunization(@ResourceParam Immunization theImmunization,
                                       RequestDetails requestDetails) {

        validateResource(theImmunization);

        return new ToolkitResourceProvider(theImmunization.class, requestDetails).createOperation(theImmunization)
    }

    /**
     * The "@Read" annotation indicates that this method supports the
     * read operation. Read operations should return a single resource
     * instance.
     *
     * @param theId
     *    The read operation takes one parameter, which must be of type
     *    IdDt and must be annotated with the "@Read.IdParam" annotation.
     * @return
     *    Returns a resource matching this identifier, or null if none exists.
     */
    @Read()
    public Immunization getResourceById(@IdParam IdType theId,
                                   RequestDetails requestDetails) {

        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        File f = tk.readOperation(theId)

        try {
            return tk.jsonParser.parseResource(getResourceType(), new FileReader(f));
        } catch (FileNotFoundException e) {
            throw new InternalErrorException("File " + f + " not found");
        }
    }

    /**
     * The "@Search" annotation indicates that this method supports the
     * search operation. You may have many different method annotated with
     * this annotation, to support many different search criteria. This
     * example searches by patient reference.
     *
     * @param thePatient
     * @return
     *    This method returns a list of Condition. This list may contain multiple
     *    matching resources, or it may be empty.
     */
    @Search()
    public List<Immunization> getImmunization(
            @RequiredParam(name = Immunization.SP_PATIENT) ReferenceParam thePatient,
            RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(Immunization.SP_PATIENT, thePatient.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )

        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))
    }

    def validateResource(Immunization theImmunization) {}
}
