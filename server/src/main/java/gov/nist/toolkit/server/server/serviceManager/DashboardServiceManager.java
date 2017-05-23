package gov.nist.toolkit.server.server.serviceManager;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.server.scripts.DashboardAccess;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.client.XdsException;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.List;

public class DashboardServiceManager extends CommonService {

	
	static Logger logger = Logger.getLogger(DashboardServiceManager.class);
	Session session;
	
	public DashboardServiceManager(Session session) throws XdsException {
		this.session = session;
	}

	
	public List<RegistryStatus> getDashboardRegistryData() throws Exception  {
		logger.debug(session.id() + ": " + "getDashboardRegistryData(" + "" + ")");
		String dashboard = Installation.instance().propertyServiceManager().getPropertyManager().getExternalCache() + File.separator + "Dashboard";
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
		String dashboard = Installation.instance().propertyServiceManager().getPropertyManager().getExternalCache() + File.separator + "Dashboard";
		try {
			return new DashboardAccess(dashboard).getRepositoryData();
		} catch (Exception e) {
			logger.debug("Failed to load Repository Dashboard data from " + dashboard);
			logger.debug(ExceptionUtil.exception_details(e));
			throw new Exception("getDashBoardRepositoryData() failed - " + e.getMessage(), e);
		}
	}


}
