package gov.nist.toolkit.services.server;

import gov.nist.toolkit.simcommon.client.NoSimException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.fhir.simulators.servlet.SimServlet;
import gov.nist.toolkit.metadataModel.DocEntry;
import gov.nist.toolkit.metadataModel.DocEntryCollection;
import gov.nist.toolkit.metadataModel.RegIndex;
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
        if (de.isPathIsRelative()) {
            de.setPathToMetadata(regIndex().getAbsolutePathForObject(de).toString());
            de.setPathIsRelative(false);
        }
        return de.getFullMetadata();
    }
}
