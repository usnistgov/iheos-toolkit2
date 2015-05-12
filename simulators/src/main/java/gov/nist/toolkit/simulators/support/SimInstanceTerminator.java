package gov.nist.toolkit.simulators.support;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SimDb;
import gov.nist.toolkit.actorfactory.client.Simulator;
import gov.nist.toolkit.actorfactory.client.SimulatorConfig;
import gov.nist.toolkit.session.server.Session;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Track simulator lifetimes and terminate expired simulators.
 * @author bill
 *
 */
public class SimInstanceTerminator {
	Session session;
	static Logger logger = Logger.getLogger(SimInstanceTerminator.class);

	public SimInstanceTerminator(Session session) {
		this.session = session;
	}
	
	public int run() throws Exception  {
		SimDb simdb;
		try {
			simdb = new SimDb(session.getDefaultSimId()); // SimManager.get(session.id()).getSimDb(session.getDefaultSimId());
		} catch (IOException e1) {
			logger.error("Cannot find Simulator Database", e1);
			throw new Exception("Cannot find Simulator Database", e1);
		}
		
		Date now = new Date();
		int deleted = 0;
		
		List<String> simIds = simdb.getAllSimIds();
		for (String simId : simIds) {
			SimulatorConfig asc;
			try {
//				asc = new SimDb().SimManager.getSimManager(session.id()).getSimulatorConfig(id);
				asc = new SimCache().getSimulatorConfig(simId);
				Date expiration = asc.getExpiration();
				if (expiration.before(now)) {
					session.deleteSim(simId);
					deleted++;
				}
			} catch (Exception e) {
				logger.error("Cannot load Simulator Config for id " + simId, e);
			}
		}
		return deleted;
	}

}
