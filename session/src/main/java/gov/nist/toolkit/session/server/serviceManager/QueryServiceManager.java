package gov.nist.toolkit.session.server.serviceManager;

import gov.nist.toolkit.actorfactory.SimCache;
import gov.nist.toolkit.actorfactory.SiteServiceManager;
import gov.nist.toolkit.actortransaction.client.ActorType;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.registrymetadata.client.*;
import gov.nist.toolkit.results.CommonService;
import gov.nist.toolkit.results.ResultBuilder;
import gov.nist.toolkit.results.client.*;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.session.server.services.*;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import gov.nist.toolkit.xdsexception.XdsException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryServiceManager extends CommonService {

	static Logger logger = Logger.getLogger(QueryServiceManager.class);
	Session session;

	public QueryServiceManager(Session session) {
		this.session = session;
	}
	
	public List<Result> registerAndQuery(SiteSpec site, String pid)  {
		logger.debug(session.id() + ": " + "registerAndQuery");
		try {
			return new RegisterAndQuery(session).run(site, pid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> lifecycleValidation(SiteSpec site, String pid)  {
		logger.debug(session.id() + ": " + "lifecycleValidation");
		try {
			return new LifecycleValidation(session).run(site, pid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> folderValidation(SiteSpec site, String pid)  {
		logger.debug(session.id() + ": " + "folderValidation");
		try {
			return new FolderValidation(session).run(site, pid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> submitRegistryTestdata(SiteSpec site,
			String datasetName, String pid)  {
		logger.debug(session.id() + ": " + "submitRegistryTestdata");
		try {
			return new SubmitRegistryTestdata(session).run(site, datasetName, pid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> submitRepositoryTestdata(String testSessionName, SiteSpec site,
												 String datasetName, String pid)  {
		logger.debug(session.id() + ": " + "submitRepositoryTestdata");
		try {
			session.setMesaSessionName(testSessionName);
			return new SubmitRepositoryTestdata(session).run(site, datasetName, pid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> submitXDRTestdata(SiteSpec site,
			String datasetName, String pid)  {
		logger.debug(session.id() + ": " + "submitXDRTestdata");
		try {
			return new SubmitXDRTestdata(session).run(site, datasetName, pid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> provideAndRetrieve(SiteSpec site, String pid) {
		logger.debug(session.id() + ": " + "provideAndRetrieve");
		try {
			return new ProvideAndRetrieve(session).run(site, pid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> findDocuments(SiteSpec site, String pid, boolean onDemand) {
		logger.debug(session.id() + ": " + "findDocuments");
		try {
			return new FindDocuments(session).run(site, pid, onDemand);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> findDocumentsByRefId(SiteSpec site, String pid, List<String> refIds) {
		logger.debug(session.id() + ": " + "findDocumentsByRefId");
		try {
			return new FindDocumentsByRefId(session).run(site, pid, refIds);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getDocuments(SiteSpec site, AnyIds aids) {
		logger.debug(session.id() + ": " + "getDocuments");
		if (site == null) site = session.siteSpec;
		try {
			return new GetDocuments(session).run(site, aids);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> findFolders(SiteSpec site, String pid) {
		logger.debug(session.id() + ": " + "findFolders");
		try {
			return new FindFolders(session).run(site, pid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getFolders(SiteSpec site, AnyIds aids) {
		logger.debug(session.id() + ": " + "getFolders");
		if (site == null) site = session.siteSpec;
		try {
			return new GetFolders(session).run(site, aids);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getFoldersForDocument(SiteSpec site, AnyIds aids) {
		logger.debug(session.id() + ": " + "getFoldersForDocument");
		if (site == null) site = session.siteSpec;
		try {
			return new GetFoldersForDocument(session).run(site, aids);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getFolderAndContents(SiteSpec site, AnyIds aids) {
		logger.debug(session.id() + ": " + "getFolderAndContents");
		if (site == null) site = session.siteSpec;
		try {
			return new GetFolderAndContents(session).run(site, aids);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}


	public List<Result> getAssociations(SiteSpec site, ObjectRefs ids) {
		logger.debug(session.id() + ": " + "getAssociations");
		if (site == null) site = session.siteSpec;
		try {
			return new GetAssociations(session).run(site, ids);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getObjects(SiteSpec site, ObjectRefs ids) {
		logger.debug(session.id() + ": " + "getObjects " + ids + " from " + site);
		if (site == null) { site = session.siteSpec; logger.debug("default site is " + site); }
		try {
			return new GetObjects(session).run(site, ids);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}
	
	public List<Result> getRelated(SiteSpec site, ObjectRef or,
			List<String> assocs) {
		logger.debug(session.id() + ": " + "getRelated");
		if (site == null) site = session.siteSpec;
		try {
			return new GetRelated(session).run(site, or, assocs);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getSubmissionSets(SiteSpec site, AnyIds aids) {
		logger.debug(session.id() + ": " + "getSubmissionSets");
		if (site == null) site = session.siteSpec;
		try {
			return new GetSubmissionSets(session).run(site, aids);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getSSandContents(SiteSpec site, String ssid) {
		logger.debug(session.id() + ": " + "getSSandContents");
		try {
			return new GetSSandContents(session).run(site, ssid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> srcStoresDocVal(SiteSpec site, String ssid) {
		logger.debug(session.id() + ": " + "srcStoresDocVal");
		try {
			return new SrcStoresDocVal(session).run(site, ssid);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> retrieveDocument(SiteSpec site, Uids uids) throws Exception {
		logger.debug(session.id() + ": " + "retrieveDocument");
		if (site == null) site = session.siteSpec;
		return new RetrieveDocument(session).run(site, uids);
	}

	public List<Result> retrieveImagingDocSet(SiteSpec site, Uids uids, String studyRequest, String transferSyntax) throws Exception {
		logger.debug(session.id() + ": " + "retrieveImagingDocSet");
		if (site == null) site = session.siteSpec;
		return new RetrieveImagingDocSet(session).run(site, uids, studyRequest, transferSyntax);
	}

	public List<Result> findPatient(SiteSpec site, String firstName,
			String secondName, String lastName, String suffix, String gender,
			String dob, String ssn, String pid, String homeAddress1,
			String homeAddress2, String homeCity, String homeState,
			String homeZip, String homeCountry, String mothersFirstName, String mothersSecondName,
			String mothersLastName, String mothersSuffix, String homePhone,
			String workPhone, String principleCareProvider, String pob,
			String pobAddress1, String pobAddress2, String pobCity,
			String pobState, String pobZip, String pobCountry) {
		logger.debug(session.id() + ": " + "findPatient");
		try {
			return new FindPatient(session).run(site, firstName, secondName,  lastName, suffix, gender, dob, ssn, pid,  
					homeAddress1, homeAddress2, homeCity, homeState, homeZip, homeCountry,
					mothersFirstName, mothersSecondName, mothersLastName, mothersSuffix, 
					homePhone, workPhone, principleCareProvider, 
					pob, pobAddress1, pobAddress2, pobCity,pobState, pobZip, pobCountry);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> mpqFindDocuments(SiteSpec site, String pid,
			List<String> classCodes, List<String> hcftCodes,
			List<String> eventCodes) {
		logger.debug(session.id() + ": " + "mpqFindDocuments");
		try {
			return new MpqFindDocuments(session).run(site, pid, classCodes, hcftCodes,
					eventCodes);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> mpqFindDocuments(SiteSpec site, String pid, Map<String, List<String>> selectedCodes) {
		logger.debug(session.id() + ": " + "mpqFindDocuments");
		List<String> classCodes = selectedCodes.get(CodesConfiguration.ClassCode);
		List<String> hcftCodes = selectedCodes.get(CodesConfiguration.HealthcareFacilityTypeCode);
		List<String> eventCodes = selectedCodes.get(CodesConfiguration.EventCodeList);

		try {
			return new MpqFindDocuments(session).run(site, pid, classCodes, hcftCodes,
					eventCodes);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getAll(SiteSpec site, String pid, Map<String, List<String>> selectedCodes) {
		logger.debug(session.id() + ": " + "getAll");

		try {
			return new GetAll(session).run(site, pid, selectedCodes);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

    /**
     * Service manager function for the new Find Documents tab
     * @param site the site selected by the user
     * @param pid the PID entered by the user
     * @param selectedCodes the other parameters of the query
     * @return a list of documents
     */
	public List<Result> findDocuments2(SiteSpec site, String pid, Map<String, List<String>> selectedCodes) {
		logger.debug(session.id() + ": " + "findDocuments2");

		try {
			return new FindDocuments2(session).run(site, pid, selectedCodes);
		} catch (XdsException e) {
			return buildResultList(e);
		}
	}

	public List<Result> getLastMetadata() {
		logger.debug(session.id() + ": " + "getLastMetadata");
		List<Result> results = new ArrayList<Result>();
		Result result = ResultBuilder.RESULT(new TestInstance("getLastMetadata"));
		results.add(result);

		try {
			Metadata m = session.getLastMetadata();
			if (m == null)
				return new ArrayList<Result>();
			MetadataCollection mc = MetadataToMetadataCollectionParser
					.buildMetadataCollection(m, "Metadata");
			StepResult sr = new StepResult();
			sr.setMetadata(mc);
			result.addStepResult(sr);
			return results;
		} catch (RuntimeException e) {
			result.assertions.add(ExceptionUtil.exception_details(e), false);
			return results;
		}
	}


	public List<Result> perRepositoryRetrieve(Uids uids, TestInstance testInstance,
			List<String> sections, Map<String, String> params) {
		List<Result> results = new ArrayList<Result>();

		Map<String, Uids> org = uids.organizeByRepository();
		for (String repuid : org.keySet()) {
			params.put("$repuid$", repuid);
			int i = 0;
			for (Uid uid : org.get(repuid).uids) {
				params.put("$uid" + i + "$", uid.uid);
				i++;
			}
			if (i > 10) { // query templates impose this limit
				results.add(new Result(
						new AssertionResults(
								"ToolkitServiceImpl#buildPerCommunityQuery: too many documents requested",
								false)));
			} else {
				Map<String, String> myparams = dup(params);
				myparams.put("$home$", org.get(repuid).uids.get(0).home);
				results.add(session.xdsTestServiceManager().xdstest(testInstance, sections, myparams, null, null, false));
			}
		}
		return results;
	}

	public List<Result> perRepositoryImagingDocSetRetrieve(Uids uids, TestInstance testInstance,
			List<String> sections, Map<String, String> params) {
		List<Result> results = new ArrayList<Result>();


		Map<String, Uids> org = uids.organizeByRepository();
		for (String repuid : org.keySet()) {
			params.put("$repuid$", repuid);
			int i = 0;
			for (Uid uid : org.get(repuid).uids) {
				//params.put("$uid" + i + "$", uid.uid);
				i++;
			}
			if (i > 10) { // query templates impose this limit
				results.add(new Result(
						new AssertionResults(
								"ToolkitServiceImpl#buildPerRepositoryImagingDocSetRetrieve: too many documents requested",
								false)));
			} else {
				Map<String, String> myparams = dup(params);
				myparams.put("$home$", org.get(repuid).uids.get(0).home);
				logger.debug("ToolkitServiceImpl#buildPerRepositoryImagingDocSetRetrieve: parameter dump");
				for (String param : myparams.keySet()) {
				    logger.debug("..." + param + ": " + myparams.get(param));
				}
				results.add(session.xdsTestServiceManager().xdstest(testInstance, sections, myparams, null, null, false));
			}
		}

		return results;
	}

	public List<Result> runPerCommunityQuery(AnyIds aids, Session s,
			TestInstance testInstance, List<String> sections, Map<String, String> params)
					throws Exception {
		if (s.siteSpec.isRG()) {
			sections.add("XCA");
		}
		else if (s.siteSpec.isIG()) {
			sections.add("IG");
		}
		else {
			sections.add("XDS");
			return asList(session.xdsTestServiceManager().xdstest(testInstance, sections, params, null, null, false));
		}
		return perCommunityQuery(aids, testInstance, sections, params);
	}

	public List<Result> perCommunityQuery(AnyIds ids, TestInstance testInstance,
			List<String> sections, Map<String, String> aparams) throws Exception {
		List<Result> results = new ArrayList<Result>();

		fillInHome(ids);

		// home => ids
		Map<String, AnyIds> org = organizeByHome(ids);
		for (String home : org.keySet()) {
			AnyIds orefs = org.get(home);
			Map<String, String> params = new HashMap<String, String>();
			params.putAll(aparams);
			int i = 0;
			for (AnyId or : orefs.ids) {
				if (or.isUUID()) {
					params.put("$id" + i + "$", or.id);
					params.put("$uuid$", or.id);
				} else
					params.put("$uid" + i + "$", or.id);
				i++;
			}
			if (i > 10) { // query templates impose this limit
				try {
					throw new Exception(
							"ToolkitServiceImpl#buildPerCommunityQuery: too many documents requested");
				} catch (Exception e) {
					return buildResultList(e);
				}
			}
			Map<String, String> myparams = dup(params);
			myparams.put("$home$", home);
			results.add(session.xdsTestServiceManager().xdstest(testInstance, sections, myparams, null, null, false));
		}
		return results;
	}

	/**
	 * Fill in home and repositoryUniqueId in AnyIds given the site registered with the Session.
	 * One or both may already be present in AnyId
	 * @param ids
	 * @return ids
	 * @throws Exception
	 */
	public AnyIds fillInHome(AnyIds ids) throws Exception {
		Session s = session;
		//		Site s2 = getSites().getSite(s.siteSpec.name);
		Site s2 = null;

		for (AnyId id : ids.ids) {
			if (id.repositoryUniqueId == null) {
				if (s2 == null) {
					try {
						s2 = new SimCache().getSimManagerForSession(s.id()).getAllSites().getSite(s.siteSpec.name);
					} catch (Throwable e) {}
				}
				if (s2 != null && s2.hasRepositoryB()) {
					try {
						id.repositoryUniqueId = s2.getRepositoryUniqueId();
					} catch (Exception e) {
					}
				}
			}
			if (id.home != null && !id.home.equals("")) {
				// don't let the code below overright home 
			}
			else if (s.siteSpec.isGW()) {
				if (s.siteSpec.actorType.equals(ActorType.INITIATING_GATEWAY)) {
					// IG is SiteSpec.name
					// RG is SiteSpec.homeName
					// Set homeCommunityId for SiteSpec.homeName
					String homeName = s.siteSpec.homeName;
					if (homeName == null || homeName.equals(""))
						throw new Exception("Cross Community request through IG " + s.siteSpec.name + ". No RG specified");
					Site rg = SiteServiceManager.getSiteServiceManager().getCommonSites().getSite(homeName);
					if (rg.getHome() == null || rg.getHome().equals(""))
						throw new Exception("Cross Community request but RG " + homeName + " has no homeCommunityId configured");
					id.home = rg.home;
				} else {
					if (s2 == null) {
						SiteServiceManager ssm = SiteServiceManager.getSiteServiceManager();
						s2 = ssm.getSite(ssm.getAllSites(session.id()), s.siteSpec.name);
					}
					if (s2.getHome() == null || s2.getHome().equals(""))
						throw new Exception("Cross Community request but site " + s.siteSpec.name + " has no homeCommunityId configured");
					id.home = s2.getHome();
				}
			}
		}
		return ids;
	}

	Uids fillInHome(Uids uids) throws Exception {
		Session s = session;
		Site s2 = SiteServiceManager.getSiteServiceManager().getCommonSites().getSite(s.siteSpec.name);
		for (Uid uid : uids.uids) {
			if (uid.repositoryUniqueId == null)
				uid.repositoryUniqueId = s2.getRepositoryUniqueId();
			if (s.siteSpec.isGW()) {
				if (s.siteSpec.homeId != null)
					uid.home = s.siteSpec.homeId;
				else
					uid.home = s2.getHome();
			}
		}
		return uids;
	}

	// Sort the ids by home and return the home => ids mapping
	// If no home then error
	public Map<String, AnyIds> organizeByHome(AnyIds ids) throws Exception {
		Map<String, AnyIds> perHome = new HashMap<String, AnyIds>();

		for (AnyId or : ids.ids) {
			String home = or.home;
			if (home == null)
				home = "";
			//			if (home == null || home.equals(""))
			//				throw new Exception("Id " + or + " does not have a homeCommunityId");

			// add it to the collection
			AnyIds homeOr = perHome.get(home);
			if (homeOr == null) {
				homeOr = new AnyIds();
				perHome.put(home, homeOr);
			}
			homeOr.ids.add(or);
		}

		return perHome;
	}



}
