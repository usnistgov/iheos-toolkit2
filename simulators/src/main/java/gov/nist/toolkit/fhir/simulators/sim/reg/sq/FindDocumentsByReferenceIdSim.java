package gov.nist.toolkit.fhir.simulators.sim.reg.sq;

import gov.nist.toolkit.fhir.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.FindDocumentsByReferenceId;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.StoredQuerySupport;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;


public class FindDocumentsByReferenceIdSim extends FindDocumentsByReferenceId {
    FindDocumentsSim fdsim;

    public void setRegIndex(RegIndex ri) {
        fdsim.setRegIndex(ri);
    }

    public FindDocumentsByReferenceIdSim(StoredQuerySupport sqs) throws MetadataValidationException {
        super(sqs);

        fdsim = new FindDocumentsSim(sqs);
    }

    @Override
    protected Metadata runImplementation() throws XdsException {

        fdsim.runImplementationFilter();

        runImplementationByRef();

        return fdsim.buildReturnFormat();
    }

    void runImplementationByRef() {
        fdsim.results = fdsim.mc.docEntryCollection.filterByReferenceId(referenceIdList, fdsim.results);
    }


}
