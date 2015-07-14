package gov.nist.toolkit.xdstools2.server.serviceManager;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdstools2.client.RegistryStatus;
import gov.nist.toolkit.xdstools2.client.RepositoryStatus;
import gov.nist.toolkit.xdstools2.scripts.DashboardAccess;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

public class DashboardServiceManager extends CommonServiceManager {
	
	static Logger logger = Logger.getLogger(DashboardServiceManager.class);
	Session session;
	
	public DashboardServiceManager(Session session) throws XdsException {
		this.session = session;
	}

	
	public List<RegistryStatus> getDashboardRegistryData() throws Exception  {
		logger.debug(session.id() + ": " + "getDashboardRegistryData(" + "" + ")");
		String dashboard = Installation.installation().propertyServiceManager().getPropertyManager().getExternalCache() + File.separator + "Dashboard";
		try {
			return new DashboardAccess(dashboard).getRegistryData();
		} catch (Exception e) {
			logger.debug("Failed to load Registry Dashboard data from " + dashboard);
			logger.debug(ExceptionUtil.exception_details(e));
			throw new Exception("getDashBoardRegistryData() failed - " + e.getMessage() , e);
		}
	}

	public List<RepositoryStatus> getDashboardRepositoryData() throws Exception {
		logger.debug(session.id() + ": " + "getDashboardRepositoryData(" + "" + ")");
		String dashboard = Installation.installation().propertyServiceManager().getPropertyManager().getExternalCache() + File.separator + "Dashboard";
		try {
			return new DashboardAccess(dashboard).getRepositoryData();
		} catch (Exception e) {
			logger.debug("Failed to load Repository Dashboard data from " + dashboard);
			logger.debug(ExceptionUtil.exception_details(e));
			throw new Exception("getDashBoardRepositoryData() failed - " + e.getMessage(), e);
		}
	}


}
