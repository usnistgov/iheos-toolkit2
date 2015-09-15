package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.xdsexception.ToolkitRuntimeException;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by bill on 9/14/15.
 */
public class RuntimeManager {
    static Logger logger = Logger.getLogger(RuntimeManager.class);

    public static BaseActorSimulator getSimulatorRuntime(SimId simId) throws NoSimException, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        SimDb db = new SimDb();
        SimulatorConfig config = GenericSimulatorFactory.getSimConfig(db.getRoot(), simId);
        String actorTypeName = config.getType();
        ActorType actorType = ActorType.findActor(actorTypeName);
        String actorSimClassName = actorType.getSimulatorClassName();
        logger.info("Loading runtime for sim " + simId + " of type " + actorTypeName + " of class " + actorSimClassName);
        Class<?> clas = Class.forName(actorSimClassName);

        // find correct constructor - no parameters
        Constructor<?>[] constructors = clas.getConstructors();
        Constructor<?> constructor = null;
        for (int i=0; i<constructors.length; i++) {
            Constructor<?> cons = constructors[i];
            Class<?>[] parmTypes = cons.getParameterTypes();
            if (parmTypes.length != 0) continue;
//				if (!parmTypes[0].getSimpleName().equals(dsSimCommon.getClass().getSimpleName())) continue;
//				if (!parmTypes[1].getSimpleName().equals(asc.getClass().getSimpleName())) continue;
            constructor = cons;
        }
        if (constructor == null)
            throw new ToolkitRuntimeException("Cannot find correct constructor for " + actorSimClassName);
        Object obj = constructor.newInstance();
        if (!(obj instanceof BaseActorSimulator)) {
            throw new ToolkitRuntimeException("Received message for actor type " + actorTypeName + " which has a handler/simulator that does not extend AbstractDsActorSimulator");
        }
        return (BaseActorSimulator) obj;
    }

}
