package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.SimId;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import org.apache.log4j.Logger;

import java.util.Date;
import java.util.List;

/**
 * Track simulator lifetimes and terminate expired simulators.
 * @author bill
 *
 */
public class SimInstanceTerminator {
	static Logger logger = Logger.getLogger(SimInstanceTerminator.class);

	public int run() throws Exception  {
		SimDb simdb = new SimDb();

		Date now = new Date();
		int deleted = 0;
		
		List<SimId> simIds = SimDb.getAllSimIds();
		for (SimId simId : simIds) {
			SimulatorConfig asc;
			try {
				asc = new SimCache().getSimulatorConfig(simId);
				Date expiration = asc.getExpiration();
				if (expiration.before(now)) {
					SimCommon.deleteSim(simId);
					deleted++;
				}
			} catch (Exception e) {
				logger.error("Cannot load Simulator Config for id " + simId, e);
			}
		}
		return deleted;
	}

}
