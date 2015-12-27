package gov.nist.toolkit.sdkTest
import gov.nist.toolkit.grizzlySupport.AbstractGrizzlyController
import groovy.transform.TypeChecked

/**
 *
 *
 */
@TypeChecked
public class GrizzlyController extends AbstractGrizzlyController {
    public List<String> getPackages() {
        return Arrays.asList("gov.nist.toolkit.toolkitServices", "gov.nist.toolkit.transactionNotificationService");
    }
}

