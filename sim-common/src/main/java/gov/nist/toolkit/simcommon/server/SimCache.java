package gov.nist.toolkit.simcommon.server;


import gov.nist.toolkit.installation.shared.TestSession;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimIdFactory;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import java.util.logging.Logger;

import java.util.*;

/**
 * Cache of loaded simulators. This is maintained globally (covering all sessions) since one session
 * deleting a common simulator affects other sessions and should
 * be reflected immediately. Simulators are managed by sessionId keeping
 * a Map sessionId = SimManager.
 * @author bill
 *
 */
public class SimCache {
    static private Logger logger = Logger.getLogger(SimCache.class.getName());

    static public Collection<Site> getAllSites(TestSession testSession) throws Exception {
        Set<Site> sitesSet = new HashSet<>();
        sitesSet.addAll(SimManager.getAllSites(testSession).asCollection());
        return sitesSet;
    }

    public static Collection<String> getAllRepositoryUniqueIds(TestSession testSession) throws Exception {
        Collection<Site> sites = getAllSites(testSession);
        Set<String> ids = new HashSet<>();
        for (Site site : sites) {
            Set<String> siteIds = site.repositoryUniqueIds();
            ids.addAll(siteIds);
        }

        return ids;
    }

    static public SimManager getSimManagerForSession(String sessionId) {
        return getSimManagerForSession(sessionId, false);
    }

    static public Site getSite(String sessionId, String siteName, TestSession testSession) {
        try {
            Sites sites = SiteServiceManager.getSiteServiceManager().getCommonSites(testSession);
            Site site = sites.getSite(siteName, testSession);
            if (site != null) return site;
            SimId simId = new SimId(testSession, siteName);
            return SimManager.getSite(simId);
        } catch (Exception e) {
            try {
                SimId simId;
                try {
                    simId = SimIdFactory.simIdBuilder(siteName);
                } catch (Exception e1) {
                    simId = SimIdFactory.simIdBuilder(testSession, siteName);
                }
                Site s = SimManager.getSite(simId);
                return s;
            } catch (Exception e1) {
                return null;
            }
        }
    }

    static public Site getSite(String siteName, TestSession testSession) {
        return getSite(null, siteName, testSession);
    }

    static public SimManager getSimManagerForSession(String sessionId, boolean create) {
        return new SimManager(sessionId);
    }

}
