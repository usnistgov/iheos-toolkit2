package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import gov.nist.toolkit.fhir.search.BaseQuery
import groovy.transform.CompileStatic
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.hl7.fhir.dstu3.model.HealthcareService
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.dstu3.model.Location
import org.hl7.fhir.instance.model.api.IBaseResource
import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails

/**
 * Created by rmoult01 on 7/14/17.
 */

class HealthcareServiceResourceProvider implements IResourceProvider{

    @Override
    Class<? extends IBaseResource> getResourceType() {
        return HealthcareService.class
    }

    @Create()
    public MethodOutcome createHealthcareService(@ResourceParam HealthcareService theHealthcareService,
                                            RequestDetails requestDetails) {

        validateResource(theHealthcareService);

        return new ToolkitResourceProvider(getResourceType(), requestDetails).createOperation(theHealthcareService)
    }

    @Read()
    public HealthcareService getResourceById(@IdParam IdType theId, RequestDetails requestDetails) {

        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        File f = tk.readOperation(theId)

        try {
            return tk.jsonParser.parseResource(getResourceType(), new FileReader(f));
        } catch (FileNotFoundException e) {
            throw new InternalErrorException("File " + f + " not found");
        }
    }

    @Search()
    public List<HealthcareService> getHealthcareService(
            @RequiredParam(name = HealthcareService.SP_LOCATION) StringParam theLocation, RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(HealthcareService.SP_LOCATION, theLocation.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )


        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))

    }


    def validateResource(HealthcareService theLocation) {}
}
