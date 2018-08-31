package gov.nist.toolkit.fhir.simulators.sim.reg.mu;

import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.fhir.simulators.support.DsSimCommon;
import gov.nist.toolkit.fhir.simulators.support.TransactionSimulator;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.valregmsg.message.MetadataContainer;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class RMSim extends TransactionSimulator {
    static Logger log = Logger.getLogger(RMSim.class);
    protected MessageValidatorEngine mvc;
    protected DsSimCommon dsSimCommon;
    protected Metadata m = null;
    private List<String> idsToDelete = new ArrayList<>();


    public RMSim(SimCommon common, SimulatorConfig simulatorConfig) {
        super(common, simulatorConfig);
        this.dsSimCommon = dsSimCommon;
    }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
        this.er = er;
        this.mvc = mvc;

        setup();

        if (er.hasErrors())
            return;

    }

    protected void setup() {

        // Pull metadata container off validation stack
        try {
            MetadataContainer metaCon = (MetadataContainer) dsSimCommon.getMessageValidatorIfAvailable(MetadataContainer.class);
            m = metaCon.getMetadata();
            if (m == null) throw new Exception("");
        } catch (Exception e) {
            er.err(XdsErrorCode.Code.XDSRegistryError, "Internal Error: cannot access input metadata", this, null);
        }

        if (m.getSubmissionSets().size() != 0) {
            er.err(XdsErrorCode.Code.XDSRegistryError, "Remove Metadata request cannot contain SubmissionSet objects", null, null);
            return;
        }
        if (m.getExtrinsicObjects().size() != 0) {
            er.err(XdsErrorCode.Code.XDSRegistryError, "Remove Metadata request cannot contain DocumentEntry objects", null, null);
            return;
        }
        if (m.getAssociations().size() != 0) {
            er.err(XdsErrorCode.Code.XDSRegistryError, "Remove Metadata request cannot contain Association objects", null, null);
            return;
        }
        if (m.getFolders().size() != 0) {
            er.err(XdsErrorCode.Code.XDSRegistryError, "Remove Metadata request cannot contain Folder objects", null, null);
            return;
        }

        List<String> missing = new ArrayList<>();
    }


    }
