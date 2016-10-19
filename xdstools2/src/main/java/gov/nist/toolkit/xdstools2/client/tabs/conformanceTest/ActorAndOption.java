package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

/**
 *
 */
public class ActorAndOption {
    private String actorId;
    private String optionId;
    private String optionTitle;
    private boolean externalStart;

    public ActorAndOption(String actorId, String optionId, String optionTitle, boolean externalStart) {
        this.actorId = actorId;
        this.optionId = optionId;
        this.optionTitle = optionTitle;
        this.externalStart = externalStart;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getOptionId() {
        return optionId;
    }

    public void setOptionId(String optionId) {
        this.optionId = optionId;
    }

    public String getOptionTitle() {
        return optionTitle;
    }

    public void setOptionTitle(String optionTitle) {
        this.optionTitle = optionTitle;
    }

    public boolean isExternalStart() {
        return externalStart;
    }
}
