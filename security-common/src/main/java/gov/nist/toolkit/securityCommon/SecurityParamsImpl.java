package gov.nist.toolkit.securityCommon;

import gov.nist.toolkit.envSetting.EnvSetting;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class SecurityParamsImpl implements SecurityParams {
    String environmentName;

    public SecurityParamsImpl(String environName) {
        environmentName = environName;
    }

    @Override
    public File getCodesFile() throws EnvironmentNotSelectedException {
        return new EnvSetting(environmentName).getCodesFile();
    }

    @Override
    public File getKeystore() throws EnvironmentNotSelectedException {
        if (environmentName == null)
            throw new EnvironmentNotSelectedException("Environment not specified in call");
        File keystoreFile = Installation.installation().getKeystore(environmentName);
        if (!keystoreFile.exists())
            throw new EnvironmentNotSelectedException("Environment " + environmentName + " does not have a keystore (file is " + keystoreFile + ")");
        return keystoreFile;
    }

    @Override
    public String getKeystorePassword() throws IOException, EnvironmentNotSelectedException {
        return Installation.installation().getKeystorePassword(environmentName);
    }

    @Override
    public File getKeystoreDir() throws EnvironmentNotSelectedException {
        File f = Installation.installation().getKeystoreDir(environmentName);
        if (f.exists() && f.isDirectory())
            return f;
        throw new EnvironmentNotSelectedException("Environment " + environmentName + " does not have a keystore");
    }

    private File getEnvironmentDir() throws EnvironmentNotSelectedException {
        File env = Installation.installation().environmentFile(environmentName);
        if (!env.exists() || !env.isDirectory())
            throw new EnvironmentNotSelectedException("Environment " + environmentName + " does not exist");
        return env;
    }

}
