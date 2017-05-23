package gov.nist.toolkit.desktop.shared.command;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;
import java.util.List;

/**
 *
 */
public class InitializationResponse implements Serializable, IsSerializable {
    private String defaultEnvironment;
    private List<String> environments;
    private List<String> testSessions;
    private String servletContextName;
    private String toolkitBaseUrl;
    private String wikiBaseUrl;

    public String getWikiBaseUrl() {
        return wikiBaseUrl;
    }

    public void setWikiBaseUrl(String wikiBaseUrl) {
        this.wikiBaseUrl = wikiBaseUrl;
    }

    public InitializationResponse() {

    }

    public String getDefaultEnvironment() {
        return defaultEnvironment;
    }

    public void setDefaultEnvironment(String defaultEnvironment) {
        this.defaultEnvironment = defaultEnvironment;
    }

    public List<String> getEnvironments() {
        return environments;
    }

    public void setEnvironments(List<String> environments) {
        this.environments = environments;
    }

    public List<String> getTestSessions() {
        return testSessions;
    }

    public void setTestSessions(List<String> testSessions) {
        this.testSessions = testSessions;
    }

    public String getServletContextName() {
        return servletContextName;
    }

    public void setServletContextName(String servletContextName) {
        this.servletContextName = servletContextName;
    }

    public String getToolkitBaseUrl() {
        return toolkitBaseUrl;
    }

    public void setToolkitBaseUrl(String toolkitBaseUrl) {
        this.toolkitBaseUrl = toolkitBaseUrl;
    }
}
