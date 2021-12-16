package gov.nist.toolkit.fhir.simulators.support;

import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.server.SimCommon;
import gov.nist.toolkit.simcommon.server.SimDb;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Date;
import java.util.List;

/**
 * Track simulator lifetimes and terminate expired simulators.
 * @author bill
 *
 */
public class SimInstanceTerminator {
	static Logger logger = Logger.getLogger(SimInstanceTerminator.class.getName());

	public int run(TestSession testSession) throws Exception  {
		Date now = new Date();
		int deleted = 0;
		
		List<SimId> simIds = SimDb.getAllSimIds(testSession);
		for (SimId simId : simIds) {
			SimulatorConfig asc;
			try {
				asc = new SimDb().getSimulator(simId);
				Date expiration = asc.getExpiration();
				if (expiration.before(now)) {
					SimCommon.deleteSim(simId);
					deleted++;
				}
			} catch (Exception e) {
				logger.log(Level.SEVERE, "Cannot load Simulator Config for id " + simId, e);
			}
		}
		return deleted;
	}

}
