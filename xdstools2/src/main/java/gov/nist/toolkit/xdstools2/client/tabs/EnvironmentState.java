package gov.nist.toolkit.xdstools2.client.tabs;

import java.util.List;

/**
 *
 */
public interface EnvironmentState {
    String getEnvironmentName();
    void setEnvironmentName(String environmentName);
    List<String> getEnvironmentNameChoices();
    void setEnvironmentNameChoices(List<String> environmentNameChoices);
}
