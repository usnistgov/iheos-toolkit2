package gov.nist.toolkit.securityCommon;

import gov.nist.toolkit.xdsexception.client.EnvironmentNotSelectedException;

import java.io.File;
import java.io.IOException;

public interface SecurityParams {
	File getCodesFile() throws EnvironmentNotSelectedException;
	File getKeystore() throws EnvironmentNotSelectedException;
	String getKeystorePassword() throws IOException, EnvironmentNotSelectedException;
	
	File getKeystoreDir() throws EnvironmentNotSelectedException;
}
