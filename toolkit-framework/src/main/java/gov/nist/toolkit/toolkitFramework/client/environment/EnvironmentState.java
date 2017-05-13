package gov.nist.toolkit.toolkitFramework.client.environment;

import java.util.List;

/**
 *
 */
public interface EnvironmentState {
    String getEnvironmentName();
    void setEnvironmentName(String environmentName);
    List<String> getEnvironmentNameChoices();
    void setEnvironmentNameChoices(List<String> environmentNameChoices);
    void addManager(EnvironmentManager environmentManager);
    void deleteManager(EnvironmentManager environmentManager);
    int getManagerIndex(EnvironmentManager source);
    boolean isValid();
    boolean isFirstManager();
    void updated(EnvironmentManager source);
}
