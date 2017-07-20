package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import gov.nist.toolkit.fhir.search.BaseQuery
import groovy.transform.CompileStatic
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.TermQuery
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.dstu3.model.Location
import org.hl7.fhir.dstu3.model.PractitionerRole
import org.hl7.fhir.instance.model.api.IBaseResource
import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails

/**
 * Created by rmoult01 on 7/14/17.
 */

class PractitionerRoleResourceProvider implements IResourceProvider{

    @Override
    Class<? extends IBaseResource> getResourceType() {
        return PractitionerRole.class
    }

    @Create()
    public MethodOutcome createOrganization(@ResourceParam PractitionerRole thePractitionerRole,
                                            RequestDetails requestDetails) {

        validateResource(thePractitionerRole);

        return new ToolkitResourceProvider(getResourceType(), requestDetails).createOperation(thePractitionerRole)
    }

    @Read()
    public PractitionerRole getResourceById(@IdParam IdType theId, RequestDetails requestDetails) {

        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        File f = tk.readOperation(theId)

        try {
            return tk.jsonParser.parseResource(getResourceType(), new FileReader(f));
        } catch (FileNotFoundException e) {
            throw new InternalErrorException("File " + f + " not found");
        }
    }

    @Search()
    public List<PractitionerRole> getPractitionerRole(
            @RequiredParam(name = PractitionerRole.SP_ACTIVE) StringParam theActive, RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(PractitionerRole.SP_ACTIVE, theActive.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )


        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))

    }


    def validateResource(PractitionerRole theLocation) {}
}
