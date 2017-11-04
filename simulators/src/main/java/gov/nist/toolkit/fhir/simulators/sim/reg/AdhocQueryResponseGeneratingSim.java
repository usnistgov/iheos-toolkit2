package gov.nist.toolkit.fhir.simulators.sim.reg;

import gov.nist.toolkit.valregmsg.registry.AdhocQueryResponse;

public interface AdhocQueryResponseGeneratingSim extends RegistryResponseGeneratingSim {

	abstract public AdhocQueryResponse getAdhocQueryResponse();
	
}
