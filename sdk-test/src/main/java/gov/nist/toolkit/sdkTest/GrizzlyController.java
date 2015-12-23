package gov.nist.toolkit.sdkTest;


import gov.nist.toolkit.grizzlySupport.AbstractGrizzlyController;

import java.util.Arrays;
import java.util.List;

/**
 * Main class.
 *
 */
public class GrizzlyController extends AbstractGrizzlyController {
    public List<String> getPackages() {
        return Arrays.asList("gov.nist.toolkit.toolkitServices", "gov.nist.toolkit.transactionNotificationService");
    }
}

