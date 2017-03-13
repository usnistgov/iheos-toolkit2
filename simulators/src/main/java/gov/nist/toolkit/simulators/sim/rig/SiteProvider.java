package gov.nist.toolkit.simulators.sim.rig;

import gov.nist.toolkit.sitemanagement.Sites;

import java.util.List;

/**
 * Created by davidmaffitt on 3/10/17.
 */
public interface SiteProvider {

    Sites getSites(List<String> siteNames) throws Exception;
}
