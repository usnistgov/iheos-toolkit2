package gov.nist.toolkit.xdstools2.client.tabs.conformanceTest;

/**
 *
 */
public class ActorAndOptions {
    private String actorId;
    private String optionId;
    private String optionTitle;

    public ActorAndOptions(String actorId, String optionId, String optionTitle) {
        this.actorId = actorId;
        this.optionId = optionId;
        this.optionTitle = optionTitle;
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
}
