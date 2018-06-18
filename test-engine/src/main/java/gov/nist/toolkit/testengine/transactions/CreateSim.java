package gov.nist.toolkit.testengine.transactions;

import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimIdFactory;
import gov.nist.toolkit.simcommon.client.Simulator;
import gov.nist.toolkit.simcommon.server.AbstractActorFactory;
import gov.nist.toolkit.simcommon.server.GenericSimulatorFactory;
import gov.nist.toolkit.simcommon.server.SimManager;
import gov.nist.toolkit.testengine.engine.StepContext;
import gov.nist.toolkit.xdsexception.client.MetadataException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import javax.xml.namespace.QName;

public class CreateSim extends BasicTransaction {
    private final static Logger logger = Logger.getLogger(CreateSim.class);
    String type;
    String id;

    public CreateSim(StepContext s_ctx, OMElement instruction, OMElement instruction_output) throws XdsInternalException {
        super(s_ctx, instruction, instruction_output);
        type = instruction.getAttributeValue(new QName("type"));
        if (type == null) {
            fail("CreateSim: no type attribute");
            logger.error("CreateSim: no type attribute");
        }
        id = instruction.getAttributeValue(new QName("id"));
        if (id == null) {
            fail("CreateSim: no id attribute");
            logger.error("CreateSim: no id attribute");
        }
        ActorType actorType;
        actorType = ActorType.findActor(type);
        if (actorType == null) {
            fail("CreateSim: no actor type " + type);
            logger.error("CreateSim: no actor type " + type);
        }
    }

    @Override
    protected void run(OMElement request) throws Exception {
        SimManager simManager = new SimManager(null);
        SimId simId = SimIdFactory.simIdBuilder(s_ctx.getTestSession(), id);
        Simulator sim = new GenericSimulatorFactory().buildNewSimulator(simManager, type, simId);
        reportManager.add("simId", simId.toString());
    }

    @Override
    protected void parseInstruction(OMElement part) throws XdsInternalException, MetadataException {

    }

    @Override
    protected String getRequestAction() {
        return null;
    }

    @Override
    protected String getBasicTransactionName() {
        return "createsim";
    }
}
