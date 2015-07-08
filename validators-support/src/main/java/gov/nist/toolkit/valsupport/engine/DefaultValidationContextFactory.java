package gov.nist.toolkit.valsupport.engine;

import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.valsupport.client.ValidationContext;

/**
 * Created by bill on 7/8/15.
 */
public class DefaultValidationContextFactory {

    static public ValidationContext validationContext() {
        return new ValidationContext(EnvSetting.getEnvSetting(EnvSetting.DEFAULTSESSIONID).getCodesFile().toString());
    }

}
