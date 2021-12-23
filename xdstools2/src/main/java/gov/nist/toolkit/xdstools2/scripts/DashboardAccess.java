package gov.nist.toolkit.xdstools2.scripts;

import gov.nist.toolkit.xdstools2.shared.RegistryStatus;
import gov.nist.toolkit.xdstools2.shared.RepositoryStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;

public class DashboardAccess {
	static Logger logger = Logger.getLogger(DashboardAccess.class.getName());

	String dashboard = "";

	public DashboardAccess(String dashboard) {
		this.dashboard = dashboard;
	}

	File getDashboardDirectory() throws  Exception {
		return new File(dashboard);
		//		String warHome = "";
		//		String outputDirStr = "";
		//		return new DashboardDaemon(warHome, outputDirStr).getDashboardDirectory();
	}

	public List<RepositoryStatus> getRepositoryData() throws Exception {
		List<RepositoryStatus> repStatus = new ArrayList<RepositoryStatus>();
		File repDir = new File(getDashboardDirectory() + File.separator + "Repository");
		logger.fine("Looking for Repository data in " + repDir.toString());
		File[] repStatusFiles = repDir.listFiles();
		if (repStatusFiles != null) {
			for (int i=0; i<repStatusFiles.length; i++) {
				String filename = repStatusFiles[i].toString();
				logger.fine("getRepositoryData from " + filename);
				RepositoryStatus reposStatus = loadRepositoryStatus(filename);
				repStatus.add(reposStatus);
			}
			for (RepositoryStatus rStatus : repStatus) {
				logger.fine(rStatus::toString);
			}
		}
		return repStatus;
	}

	public List<RegistryStatus> getRegistryData() throws Exception {
		List<RegistryStatus> regStatus = new ArrayList<RegistryStatus>();
		File regDir = new File(getDashboardDirectory() + File.separator + "Registry");
		logger.fine("Looking for Registry data in " + regDir.toString());
		File[] regStatusFiles = regDir.listFiles();
		if (regStatusFiles != null) {
			for (int i=0; i<regStatusFiles.length; i++) {
				String filename = regStatusFiles[i].toString();
				logger.fine("getRegistryData from " + filename);
				RegistryStatus rStatus = loadRegistryStatus(filename);
				regStatus.add(rStatus);
			}
			for (RegistryStatus rStatus : regStatus) {
				logger.fine(rStatus::toString);
			}
		}
		return regStatus;
	}

	RepositoryStatus loadRepositoryStatus(String filename) {
		try {
			FileInputStream fis = null;
			ObjectInputStream in = null;
			RepositoryStatus rstatus;
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			rstatus = (RepositoryStatus)in.readObject();
			in.close();
			return rstatus;
		} catch (Exception e) {
			return new RepositoryStatus();
		}
	}

	RegistryStatus loadRegistryStatus(String filename) {
		try {
			FileInputStream fis = null;
			ObjectInputStream in = null;
			RegistryStatus rstatus;
			fis = new FileInputStream(filename);
			in = new ObjectInputStream(fis);
			rstatus = (RegistryStatus)in.readObject();
			in.close();
			return rstatus;
		} catch (Exception e) {
			return new RegistryStatus();
		}
	}


}
