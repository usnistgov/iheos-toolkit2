package gov.nist.toolkit.xdstools2.client.tabs.SubmitResourceTab;


import com.google.gwt.user.client.ui.Widget;

public interface ILogger {
    void addLog(String content);
    void addLog(Widget content);
}
