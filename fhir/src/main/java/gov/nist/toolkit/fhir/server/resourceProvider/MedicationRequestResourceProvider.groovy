package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.rest.annotation.Create
import ca.uhn.fhir.rest.annotation.IdParam
import ca.uhn.fhir.rest.annotation.Read
import ca.uhn.fhir.rest.annotation.RequiredParam
import ca.uhn.fhir.rest.annotation.ResourceParam
import ca.uhn.fhir.rest.annotation.Search
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import gov.nist.toolkit.fhir.search.BaseQuery
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.hl7.fhir.dstu3.model.MedicationRequest
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.instance.model.api.IBaseResource

class MedicationRequestResourceProvider implements IResourceProvider{

    @Override
    Class<? extends IBaseResource> getResourceType() {
        return MedicationRequest.class
    }
    @Create()
    public MethodOutcome createOrganization(@ResourceParam MedicationRequest theMedicationRequest,
                                            RequestDetails requestDetails) {

        validateResource(theMedicationRequest);

        return new ToolkitResourceProvider(getResourceType(), requestDetails).createOperation(theMedicationRequest)
    }

    @Read()
    public MedicationRequest getResourceById(@IdParam IdType theId, RequestDetails requestDetails) {

        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        File f = tk.readOperation(theId)

        try {
            return tk.jsonParser.parseResource(getResourceType(), new FileReader(f));
        } catch (FileNotFoundException e) {
            throw new InternalErrorException("File " + f + " not found");
        }
    }


    @Search()
    public List<MedicationRequest> getDiagnosticReport(
            @RequiredParam(name = MedicationRequest.SP_SUBJECT) StringParam theSubject,
            RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(MedicationRequest.SP_SUBJECT, theSubject.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )


        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))

    }

    def validateResource(MedicationRequest theLocation) {}
}

