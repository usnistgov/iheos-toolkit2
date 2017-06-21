package gov.nist.toolkit.actorfactory;

import gov.nist.toolkit.actorfactory.client.NoSimException;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.actorfactory.factories.GenericSimulatorFactory;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.xdsexception.client.ToolkitRuntimeException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class RuntimeManager {
    static Logger logger = Logger.getLogger(RuntimeManager.class);

    public static BaseActorSimulator getSimulatorRuntime(SimId simId) throws Exception, IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        SimulatorConfig config = GenericSimulatorFactory.getSimConfig(simId);
        String actorTypeName = config.getActorType();
        ActorType actorType = ActorType.findActor(actorTypeName);
        String actorSimClassName = actorType.getSimulatorClassName();
        if (StringUtils.isBlank(actorSimClassName)) return null;
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
            throw new ToolkitRuntimeException("Cannot find no-argument constructor for " + actorSimClassName);
        Object obj = constructor.newInstance();
        if (!(obj instanceof BaseActorSimulator)) {
            throw new ToolkitRuntimeException("Received message for actor type " + actorTypeName + " which has a handler/simulator that does not extend BaseActorSimulator");
        }
        return (BaseActorSimulator) obj;
    } 
    
    /**
     * Returns an instance of the http server for a given simulator
    * @param simId simulator to be loaded.
    * @return instance of the http server for this simulator, or null if this
    * simulator does not have an http server class
    * @throws NoSimException if the simulator does not exist
    * @throws IOException on error reading class definition
    * @throws ClassNotFoundException if the http simulator class does not exist or can't be accessed
    * @throws IllegalAccessException on security fault
    * @throws InvocationTargetException on error creating the http server instance
    * @throws InstantiationException if not able to create the http server instance
    */
   public static BaseActorSimulator getHttpSimulatorRuntime(SimId simId) throws NoSimException, Exception, ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
       SimulatorConfig config = GenericSimulatorFactory.getSimConfig(simId);
       String actorTypeName = config.getActorType();
       ActorType actorType = ActorType.findActor(actorTypeName);
       String actorSimClassName = actorType.getHttpSimulatorClassName();
       if (StringUtils.isBlank(actorSimClassName)) return null;
       logger.info("Loading runtime for sim " + simId + " of type " + actorTypeName + " of class " + actorSimClassName);
       Class<?> clas = Class.forName(actorSimClassName);

       // find correct constructor - no parameters
       Constructor<?>[] constructors = clas.getConstructors();
       Constructor<?> constructor = null;
       for (int i=0; i<constructors.length; i++) {
           Constructor<?> cons = constructors[i];
           Class<?>[] parmTypes = cons.getParameterTypes();
           if (parmTypes.length != 0) continue;
           constructor = cons;
       }
       if (constructor == null)
           throw new ToolkitRuntimeException("Cannot find no-argument constructor for " + actorSimClassName);
       Object obj = constructor.newInstance();
       if (!(obj instanceof BaseActorSimulator)) {
           throw new ToolkitRuntimeException("Received message for actor type " + actorTypeName + " which has a handler/simulator that does not extend BaseActorSimulator");
       }
       return (BaseActorSimulator) obj;
   }

}
