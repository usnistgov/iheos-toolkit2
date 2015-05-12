package gov.nist.toolkit.xdstools2.scripts;

import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.MetadataParser;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.sitemanagement.SeparateSiteLoader;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.testengine.LogMap;
import gov.nist.toolkit.testengine.LogMapItem;
import gov.nist.toolkit.testengine.TransactionSettings;
import gov.nist.toolkit.testengine.Xdstest2;
import gov.nist.toolkit.testenginelogging.LogFileContent;
import gov.nist.toolkit.testenginelogging.TestStepLogContent;
import gov.nist.toolkit.xdstools2.client.RegistryStatus;
import gov.nist.toolkit.xdstools2.client.RepositoryStatus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;

public class DashboardDaemon {
//	ToolkitServiceImpl toolkit = new ToolkitServiceImpl();
	String pid = "a1e4655b50754a2^^^&1.3.6.1.4.1.21367.2005.3.7&ISO";
	String output = "/Users/bill/tmp/dashboard";
	Sites sites;
	Session s;
	String environmentName;
	String warHome;
	String externalCache;
	List<RepositoryStatus> repositories = new ArrayList<RepositoryStatus>();
	Date date = new Date();

	public File getDashboardDirectory() {
		return new File(output);
	}
	
	public Session getSession() {
		return s;
	}

	public DashboardDaemon(String warHome, String outputDirStr, String environment, String externalCache)  {
		this.environmentName = environment;
		this.warHome = warHome;
		this.externalCache = externalCache;
//		toolkit.setWarHome(warHome);
		s = new Session(new File(warHome), SiteServiceManager.getSiteServiceManager());//		toolkit.setStandAloneSession(s);
		try {
			s.setEnvironment(environmentName, externalCache);
		} catch (Exception e) {
			System.out.println("Cannot set environment: " + e.getMessage());
			System.exit(-1);
		}
		output = outputDirStr;
	}

	private void run(String pid) throws FactoryConfigurationError, Exception, IOException {
		this.pid = pid;
		new File(output).mkdirs();
//		SiteServiceManager siteServiceManager = new SiteServiceManager(null);
//		siteServiceManager.loadExternalSites();
		sites = null;
		sites = new SeparateSiteLoader().load(new File(externalCache + File.separator + "actors"), sites);
//		sites = siteServiceManager.getSites();
		// experimental
//		s = toolkit.getSession();
		s.setTls(false);    // ignores this and still uses TLS
		scanRepositories();
		scanRegistries();
	}

	void scanRegistries()  {
		File dir = new File(output + "/Registry");
		dir.mkdirs();

		List<String> registrySiteNames;
		try {
			registrySiteNames = sites.getSiteNamesWithActor(ActorType.REGISTRY);
			System.out.println("Registry sites are " + registrySiteNames);
		} catch (Exception e1) {
			System.out.println("Exception: " + e1.getMessage());
			return;
		}
		for (String regSiteName : registrySiteNames) {
			RegistryStatus regStatus = new RegistryStatus();
			SiteSpec siteSpec = new SiteSpec(regSiteName, ActorType.REGISTRY, null);
			regStatus.name = siteSpec.name;
			Site site;
			try {
				site = sites.getSite(siteSpec.name);
			} catch (Exception e1) {
				regStatus.status = false;
				regStatus.fatalError = e1.getMessage();
				registrySave(regStatus, dir + File.separator + regSiteName + ".ser");
				continue;
			}
			boolean isSecure = true;
			try {
				regStatus.endpoint = site.getEndpoint(TransactionType.STORED_QUERY, isSecure, false);
			} catch (Exception e1) {
				regStatus.status = false;
				regStatus.fatalError = e1.getMessage();
				registrySave(regStatus, dir + File.separator + regSiteName + ".ser");
				continue;
			}

			Xdstest2 xdstest;
			try {
				xdstest = new Xdstest2(new File(warHome + File.separator + "toolkitx"), null);
			} catch (Exception e) {
				regStatus.status = false;
				regStatus.fatalError = e.getMessage();
				registrySave(regStatus, dir + File.separator + regSiteName + ".ser");
				continue;
			}
			xdstest.setSites(sites);
			xdstest.setSite(site);
			xdstest.setSecure(true);
			String[] areas = {"utilities"};
			List<String> sections = new ArrayList<String>();
			sections.add("XDS");
			try {
				xdstest.setTest("GetDocuments", sections, areas);
			} catch (Exception e1) {
				regStatus.status = false;
				regStatus.fatalError = e1.getMessage();
				registrySave(regStatus, dir + File.separator + regSiteName + ".ser");
				continue;
			}
			Map<String, String> parms = new HashMap<String, String>();
			parms.put("$returnType$", "ObjectRef");
			int idx=0;
			for (RepositoryStatus repStat : repositories) {
				if (repStat.docId == null)
					continue;
				parms.put("$id" + String.valueOf(idx) + "$", repStat.docId);
				idx++;
			}
			TransactionSettings ts = new TransactionSettings();
			ts.assignPatientId = false;
			ts.siteSpec = new SiteSpec();
			ts.siteSpec.isAsync = false;
			ts.securityParams = s; 
			try {
				xdstest.run(parms, null, true, ts);
			} catch (Exception e1) {
				regStatus.status = false;
				regStatus.fatalError = e1.getMessage();
				registrySave(regStatus, dir + File.separator + regSiteName + ".ser");
				continue;
			}
			LogMap logMap;
			try {
				logMap = xdstest.getLogMap();
			} catch (Exception e1) {
				regStatus.status = false;
				regStatus.fatalError = e1.getMessage();
				registrySave(regStatus, dir + File.separator + regSiteName + ".ser");
				continue;
			}
			LogMapItem item = logMap.getItems().get(0);
			LogFileContent logFile = item.log;
			List<TestStepLogContent> testStepLogs;
			try {
				testStepLogs = logFile.getStepLogs();
			} catch (Exception e1) {
				regStatus.status = false;
				regStatus.fatalError = e1.getMessage();
				registrySave(regStatus, dir + File.separator + regSiteName + ".ser");
				continue;
			}
			TestStepLogContent tsl = testStepLogs.get(0);

			try {
				OMElement ele = tsl.getRawResult();
				List<OMElement> objrefs = MetadataSupport.decendentsWithLocalName(ele, "ObjectRef");
				Metadata m = new Metadata();
				for (OMElement objref : objrefs) {
					String id = m.getId(objref);
					for (RepositoryStatus repStat : repositories) {
						if (id.equals(repStat.docId)) {
							repStat.registry = regSiteName;
							repositorySave(repStat);

						}
					}
				}
			} catch (Exception e) {

			}

			regStatus.status = logFile.isSuccess();
			regStatus.fatalError = logFile.getFatalError();

			try {
				regStatus.errors = tsl.getErrors();
			} catch (Exception e) {
			}

			registrySave(regStatus, dir + File.separator + regSiteName + ".ser");


		}
	}

	void scanRepositories()  {
		List<String> repositorySiteNames;
		try {
			repositorySiteNames = sites.getSiteNamesWithRepository();
			System.out.println("Repository Sites are " + repositorySiteNames);
		} catch (Exception e1) {
			System.out.println("Exception: " + e1.getMessage());
			return;
		}
		for (String repSiteName : repositorySiteNames) {
			RepositoryStatus rstatus = new RepositoryStatus();
			repositories.add(rstatus);
			rstatus.date = date.toString();
			SiteSpec siteSpec = new SiteSpec(repSiteName, ActorType.REPOSITORY, null);
			rstatus.name = siteSpec.name;
			Site site;
			try {
				site = sites.getSite(siteSpec.name);
			} catch (Exception e1) {
				rstatus.status = false;
				rstatus.fatalError = e1.getMessage();
				repositorySave(rstatus);
				continue;
			}
			boolean isSecure = true;
			try {
				rstatus.endpoint = site.getEndpoint(TransactionType.PROVIDE_AND_REGISTER, isSecure, false);
			} catch (Exception e) {
				rstatus.status = false;
				rstatus.fatalError = e.getMessage();
				repositorySave(rstatus);
				continue;
			}
			
			System.out.println("PnR endpoint: " + rstatus.endpoint);

			Xdstest2 xdstest;
			try {
				xdstest = new Xdstest2(new File(warHome + File.separator + "toolkitx"), null);
			} catch (Exception e) {
				rstatus.status = false;
				rstatus.fatalError = e.getMessage();
				repositorySave(rstatus);
				continue;
			}
			xdstest.setSites(sites);
			xdstest.setSite(site);
			xdstest.setSecure(true);
			String[] areas = {"testdata-repository"};
			try {
				xdstest.setTest("SingleDocument", null, areas);
			} catch (Exception e1) {
				rstatus.status = false;
				rstatus.fatalError = e1.getMessage();
				repositorySave(rstatus);
				continue;
			}
			Map<String, String> parms = new HashMap<String, String>();
			parms.put("$patientid$", pid);
			TransactionSettings ts = new TransactionSettings();
			ts.siteSpec = new SiteSpec();
			ts.assignPatientId = false;
			ts.siteSpec.isAsync = false;
			ts.securityParams = s; 
			try {
				xdstest.run(parms, null, true, ts);
			} catch (Exception e1) {
				rstatus.status = false;
				rstatus.fatalError = e1.getMessage();
				repositorySave(rstatus);
				continue;
			}
			LogMap logMap;
			try {
				logMap = xdstest.getLogMap();
			} catch (Exception e1) {
				rstatus.status = false;
				rstatus.fatalError = e1.getMessage();
				repositorySave(rstatus);
				continue;
			}
			LogMapItem item = logMap.getItems().get(0);
			LogFileContent logFile = item.log;
			List<TestStepLogContent> testStepLogs;
			try {
				testStepLogs = logFile.getStepLogs();
			} catch (Exception e1) {
				rstatus.status = false;
				rstatus.fatalError = e1.getMessage();
				repositorySave(rstatus);
				continue;
			}
			TestStepLogContent tsl = testStepLogs.get(0);

			try {
				OMElement ele = tsl.getRawInputMetadata();
				Metadata m = MetadataParser.parseNonSubmission(ele);
				OMElement de = m.getExtrinsicObject(0);
				String docUUID = m.getId(de);
				rstatus.docId = docUUID;
			} catch (Exception e) {

			}

			rstatus.status = logFile.isSuccess();
			rstatus.fatalError = logFile.getFatalError();
			
			System.out.println("Fatal error is " + rstatus.fatalError);

			try {
				rstatus.errors = tsl.getErrors();
			} catch (Exception e) {
			}


			repositorySave(rstatus);

		}
	}

	void repositorySave(RepositoryStatus repStat)  {
		File dir = new File(output + "/Repository");
		dir.mkdirs();

		String repSiteName = repStat.name;

		String filename = dir + File.separator + repSiteName + ".ser";

		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try {
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(repStat);
			out.close();
		} catch (IOException e) {
			System.out.println("ERROR: cannot write out results");
		}

	}

	void registrySave(RegistryStatus regStat, String filename)  {
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		try{ 
			fos = new FileOutputStream(filename);
			out = new ObjectOutputStream(fos);
			out.writeObject(regStat);
			out.close();
		} catch (IOException e) {
			System.out.println("ERROR: cannot write out results");
		}

	}

	static public void main(String[] args) {

		if (args.length != 5) {
			System.out.println("Usage: DashboardDaemon <Patient ID> <warHome> <output directory> <environment_name> <external_cache>");
			System.exit(-1);
		}

		String pid = args[0];
		String warhom = args[1];
		String outdir = args[2];
		String env = args[3];
		String externalCache = args[4];

		try {
			DashboardDaemon dd = new DashboardDaemon(warhom, outdir, env, externalCache);
			dd.run(pid);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
