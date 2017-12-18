package gov.nist.toolkit.fhir.server.search

import gov.nist.toolkit.fhir.shared.searchModels.SearchModel

public class SearchGenerator  {

    public SearchGenerator() {}

    /**
     *
     * @return instance of search builder class named by resourceType.  For DocumentReference the class name
     * is DocumentReferenceSearch
     */
    BasicSearch getSearchBuilder(FhirBase fhirBase, SearchModel model) {
        assert model.resourceType
        Package pkg = this.class.package
        def inst = new GroovyClassLoader(this.class.classLoader).loadClass(pkg.name + '.' + model.resourceType.toString() + 'Search', false, true)?.newInstance(fhirBase, model.logicalId)
        assert inst
        assert inst instanceof BasicSearch
        return inst
    }
}
