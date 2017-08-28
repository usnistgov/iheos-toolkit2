package gov.nist.toolkit.simcommon.server;


import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.sitemanagement.Sites;
import gov.nist.toolkit.sitemanagement.client.Site;
import org.apache.log4j.Logger;

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
    static private Logger logger = Logger.getLogger(SimCache.class);

    static public Collection<Site> getAllSites() throws Exception {
        Set<Site> sitesSet = new HashSet<>();
        sitesSet.addAll(SimManager.getAllSites().asCollection());
        return sitesSet;
    }

    public static Collection<String> getAllRepositoryUniqueIds() throws Exception {
        Collection<Site> sites = getAllSites();
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

    static public Site getSite(String sessionId, String siteName) {
        try {
            Sites sites = SiteServiceManager.getSiteServiceManager().getCommonSites();
            return sites.getSite(siteName);
        } catch (Exception e) {
            try {
                SimId simId = new SimId(siteName);
                return SimManager.getSite(simId);
            } catch (Exception e1) {
                return null;
            }
        }
    }

    static public Site getSite(String siteName) {
        return getSite(null, siteName);
    }

    static public SimManager getSimManagerForSession(String sessionId, boolean create) {
        return new SimManager(sessionId);
    }

}
