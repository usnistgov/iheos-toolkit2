package gov.nist.toolkit.xdstools2.server.serviceManager;

import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdstools2.shared.RegistryStatus;
import gov.nist.toolkit.xdstools2.shared.RepositoryStatus;
import gov.nist.toolkit.xdstools2.scripts.DashboardAccess;

import java.io.File;
import java.util.List;

import java.util.logging.Logger;

public class DashboardServiceManager extends CommonService {
	
	static Logger logger = Logger.getLogger(DashboardServiceManager.class.getName());
	Session session;
	
	public DashboardServiceManager(Session session) throws XdsException {
		this.session = session;
	}

	
	public List<RegistryStatus> getDashboardRegistryData() throws Exception  {
		logger.fine(session.id() + ": " + "getDashboardRegistryData(" + "" + ")");
		String dashboard = Installation.instance().propertyServiceManager().getPropertyManager().getExternalCache() + File.separator + "Dashboard";
		try {
			return new DashboardAccess(dashboard).getRegistryData();
		} catch (Exception e) {
			logger.fine("Failed to load Registry Dashboard data from " + dashboard);
			logger.fine(ExceptionUtil.exception_details(e));
			throw new Exception("getDashBoardRegistryData() failed - " + e.getMessage() , e);
		}
	}

	public List<RepositoryStatus> getDashboardRepositoryData() throws Exception {
		logger.fine(session.id() + ": " + "getDashboardRepositoryData(" + "" + ")");
		String dashboard = Installation.instance().propertyServiceManager().getPropertyManager().getExternalCache() + File.separator + "Dashboard";
		try {
			return new DashboardAccess(dashboard).getRepositoryData();
		} catch (Exception e) {
			logger.fine("Failed to load Repository Dashboard data from " + dashboard);
			logger.fine(ExceptionUtil.exception_details(e));
			throw new Exception("getDashBoardRepositoryData() failed - " + e.getMessage(), e);
		}
	}


}
