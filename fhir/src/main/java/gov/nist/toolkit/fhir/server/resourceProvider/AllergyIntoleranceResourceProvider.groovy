package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.param.DateParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import ca.uhn.fhir.rest.param.ReferenceParam
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import gov.nist.toolkit.fhir.search.BaseQuery
import org.apache.lucene.document.DateTools
import org.apache.lucene.document.DateTools.Resolution
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.dstu3.model.AllergyIntolerance
import org.hl7.fhir.instance.model.api.IBaseResource

import static org.apache.lucene.search.TermRangeQuery.newStringRange

class AllergyIntoleranceResourceProvider implements IResourceProvider{
    @Override
    Class<? extends IBaseResource> getResourceType() {
        return AllergyIntolerance.class
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
    @Create()
    public MethodOutcome createObservation(@ResourceParam AllergyIntolerance theAllergy,
                                       RequestDetails requestDetails) {

        validateResource(theAllergy);

        return new ToolkitResourceProvider(AllergyIntolerance.class, requestDetails).createOperation(theAllergy)
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
    public AllergyIntolerance getResourceById(@IdParam IdType theId,
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
     * example searches by location city.
     *
     * @param thePatient
     * @return
     *    This method returns a list of AllergyIntolerance. This list may contain multiple
     *    matching resources, or it may be empty.
     */
    @Search()
    public List<AllergyIntolerance> getAllergies(
            @RequiredParam(name = AllergyIntolerance.SP_PATIENT) ReferenceParam thePatient,
            RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(AllergyIntolerance.SP_PATIENT, thePatient.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )

        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))
    }

    def validateResource(AllergyIntolerance theAllergy) {}
}
