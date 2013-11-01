package gov.nist.toolkit.securityCommon;

import gov.nist.toolkit.xdsexception.EnvironmentNotSelectedException;

import java.io.File;
import java.io.IOException;

public interface SecurityParams {
	File getCodesFile() throws EnvironmentNotSelectedException;
	File getKeystore() throws EnvironmentNotSelectedException;
	String getKeystorePassword() throws IOException, EnvironmentNotSelectedException;
	public String getKeystoreAlias() throws IOException, EnvironmentNotSelectedException;
	public File getKeystoreDir() throws EnvironmentNotSelectedException;
}
