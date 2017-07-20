package gov.nist.toolkit.fhir.server.resourceProvider

import ca.uhn.fhir.rest.annotation.*
import ca.uhn.fhir.rest.api.MethodOutcome
import ca.uhn.fhir.rest.method.RequestDetails
import ca.uhn.fhir.rest.param.DateParam
import ca.uhn.fhir.rest.param.ParamPrefixEnum
import ca.uhn.fhir.rest.param.StringParam
import ca.uhn.fhir.rest.server.IResourceProvider
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException
import gov.nist.toolkit.fhir.search.BaseQuery
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import org.apache.lucene.search.TermRangeQuery
import org.hl7.fhir.dstu3.model.IdType
import org.hl7.fhir.dstu3.model.Observation
import org.hl7.fhir.instance.model.api.IBaseResource
import org.apache.lucene.document.DateTools
import org.apache.lucene.document.DateTools.Resolution

import static org.apache.lucene.search.TermRangeQuery.*

class ObservationResourceProvider implements IResourceProvider{
    @Override
    Class<? extends IBaseResource> getResourceType() {
        return Observation.class
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
    public MethodOutcome createObservation(@ResourceParam Observation theObservation,
                                       RequestDetails requestDetails) {

        validateResource(theObservation);

        return new ToolkitResourceProvider(Observation.class, requestDetails).createOperation(theObservation)
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
    public Observation getResourceById(@IdParam IdType theId,
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
     * example searches by patient and category.
     *
     * @param thePatient
     * @param theCategory
     *    This operation takes one parameter which is the search criteria. It is
     *    annotated with the "@Required" annotation. This annotation takes one argument,
     *    a string containing the name of the search criteria. The datatype here
     *    is StringParam, but there are other possible parameter types depending on the
     *    specific search criteria.
     * @return
     *    This method returns a list of Observations. This list may contain multiple
     *    matching resources, or it may be empty.
     */
    @Search()
    public List<Observation> getObservation(
            @RequiredParam(name = Observation.SP_PATIENT) StringParam thePatient,
            @RequiredParam(name = Observation.SP_CATEGORY) StringParam theCategory,
            RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(Observation.SP_PATIENT, thePatient.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )

        term = new Term(Observation.SP_CATEGORY, theCategory.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )


        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))

    }

    /**
     * Search on patient, and category, and code.
     *
     * @param thePatient
     * @param theCategory
     * @param theCode
     * @param requestDetails
     * @return
     */
    @Search()
    public List<Observation> getObservation(
            @RequiredParam(name = Observation.SP_PATIENT) StringParam thePatient,
            @RequiredParam(name = Observation.SP_CATEGORY) StringParam theCategory,
            @RequiredParam(name = Observation.SP_CODE_VALUE_STRING) StringParam theCode,
            RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        TermQuery termQuery

        term = new Term(Observation.SP_PATIENT, thePatient.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )

        term = new Term(Observation.SP_CATEGORY, theCategory.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )

        term = new Term(Observation.SP_CODE_VALUE_STRING, theCode.value)
        termQuery = new TermQuery(term)
        builder.add ( termQuery, org.apache.lucene.search.BooleanClause.Occur.MUST )


        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))

    }

    @Search()
    public List<Observation> getObservation(
            @RequiredParam(name = Observation.SP_PATIENT) StringParam thePatient,
            @RequiredParam(name = Observation.SP_CATEGORY) StringParam theCategory,
            @RequiredParam(name = Observation.SP_DATE) DateParam theDate,
            RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        Query query

        term = new Term(Observation.SP_PATIENT, thePatient.value)
        query = new TermQuery(term)
        builder.add ( query, org.apache.lucene.search.BooleanClause.Occur.MUST )

        term = new Term(Observation.SP_CATEGORY, theCategory.value)
        query = new TermQuery(term)
        builder.add ( query, org.apache.lucene.search.BooleanClause.Occur.MUST )

        query = getDateRangeQuery( theDate);
        builder.add ( query, org.apache.lucene.search.BooleanClause.Occur.MUST )

        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))
    }

    @Search()
    public List<Observation> getObservation(
            @RequiredParam(name = Observation.SP_PATIENT) StringParam thePatient,
            @RequiredParam(name = Observation.SP_CATEGORY) StringParam theCategory,
            @RequiredParam(name = Observation.SP_CODE_VALUE_STRING) StringParam theCode,
            @RequiredParam(name = Observation.SP_DATE) DateParam theDate,
            RequestDetails requestDetails) {
        ToolkitResourceProvider tk = new ToolkitResourceProvider(getResourceType(), requestDetails)

        BooleanQuery.Builder builder = new BooleanQuery.Builder()

        Term term
        Query query

        term = new Term(Observation.SP_PATIENT, thePatient.value)
        query = new TermQuery(term)
        builder.add ( query, org.apache.lucene.search.BooleanClause.Occur.MUST )

        term = new Term(Observation.SP_CATEGORY, theCategory.value)
        query = new TermQuery(term)
        builder.add ( query, org.apache.lucene.search.BooleanClause.Occur.MUST )

        term = new Term(Observation.SP_CODE_VALUE_STRING, theCode.value)
        query = new TermQuery(term)
        builder.add ( query, org.apache.lucene.search.BooleanClause.Occur.MUST )

        query = getDateRangeQuery( theDate);
        builder.add ( query, org.apache.lucene.search.BooleanClause.Occur.MUST )

        return tk.searchResults(new BaseQuery(tk.simContext).execute(builder))
    }


    // TODO: This is broken.  The applicable index may be SP_DATE, or it may be "start" or "end" (see ObservationIndexer.groovy),
    // depending on particular Observations.
    Query getDateRangeQuery(DateParam dateParam) {
        Query query = null;
        switch( dateParam.getPrefix()) {
        // missing prefix implies 'EQ'
            case null:
            case ParamPrefixEnum.EQUAL:
                Term term = new Term(Observation.SP_DATE, dateParam.value)
                query = new TermQuery(term)
                break;
            case ParamPrefixEnum.GREATERTHAN:
                String lowerTerm = DateTools.dateToString( dateParam.value, Resolution.MILLISECOND);
                String upperTerm = null;
                query = newStringRange(Observation.SP_DATE,lowerTerm, upperTerm, false, true)
                break;
            case ParamPrefixEnum.GREATERTHAN_OR_EQUALS:
                String lowerTerm = DateTools.dateToString( dateParam.value, Resolution.MILLISECOND);
                String upperTerm = null;
                query = newStringRange(Observation.SP_DATE,lowerTerm, upperTerm, true, true)
                break;
            case ParamPrefixEnum.LESSTHAN:
                String lowerTerm = null;
                String upperTerm = DateTools.dateToString( dateParam.value, Resolution.MILLISECOND);
                query = newStringRange(Observation.SP_DATE, lowerTerm, upperTerm, true, false)
                break;
            case ParamPrefixEnum.LESSTHAN_OR_EQUALS:
                String lowerTerm = null;
                String upperTerm = DateTools.dateToString( dateParam.value, Resolution.MILLISECOND);
                query = newStringRange(Observation.SP_DATE, lowerTerm, upperTerm, true, true)
                break;
            default:
                // Unknown or unsupported request prefix:
                break;
        }
        return query;
    }

    def validateResource(Observation theObservation) {}
}
