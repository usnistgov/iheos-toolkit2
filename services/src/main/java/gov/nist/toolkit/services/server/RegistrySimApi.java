package gov.nist.toolkit.services.server;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actortransaction.shared.SimId;
import gov.nist.toolkit.simulators.servlet.SimServlet;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntry;
import gov.nist.toolkit.simulators.sim.reg.store.DocEntryCollection;
import gov.nist.toolkit.simulators.sim.reg.store.RegIndex;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import java.io.IOException;
import java.util.List;

/**
 *
 */
public class RegistrySimApi {
    SimId simId;

    public RegistrySimApi(SimId simId) {
        this.simId = simId;
    }

    private RegIndex regIndex() throws IOException, NoSimException {
        return SimServlet.getRegIndex(simId);
    }

    public List<DocEntry> findDocsByPid(String pid) throws IOException, NoSimException {
        return regIndex().mc.docEntryCollection.findByPid(pid);
    }

    public List<String> findDocsByPidObjectRef(String pid) throws IOException, NoSimException {
        return DocEntryCollection.getIds(findDocsByPid(pid));
    }

    public OMElement getDocEle(String id) throws IOException, NoSimException, MetadataException, XdsInternalException {
        DocEntry de = regIndex().mc.docEntryCollection.getById(id);
        if (de == null) return null;
        return de.getFullMetadata();
    }
}
