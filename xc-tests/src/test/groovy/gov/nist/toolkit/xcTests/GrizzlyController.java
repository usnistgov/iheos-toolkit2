package gov.nist.toolkit.xcTests;


import gov.nist.toolkit.grizzlySupport.AbstractGrizzlyController;

import java.util.Arrays;
import java.util.List;

/**
 * Define packages that need to be loaded into Grizzly
 *
 */
public class GrizzlyController extends AbstractGrizzlyController {
    public List<String> getPackages() {
        return Arrays.asList("gov.nist.toolkit.toolkitServices", "gov.nist.toolkit.transactionNotificationService");
    }

}

