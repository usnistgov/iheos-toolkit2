package gov.nist.toolkit.xdstools2.client.tabs.simMsgViewerTab;

/**
 *
 */
public class EventInfo {
    private String id;
    private String display;

    public EventInfo(String id, String display) {
        this.id = id;
        this.display = display;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
