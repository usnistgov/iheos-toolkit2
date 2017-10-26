package gov.nist.toolkit.simcommon.client;

public class SimLogEventLinkBuilder {

    static public String build(String toolkitBaseUrl, String simIdString, String actor, String trans, String eventId) {
        String token = buildToken(simIdString, actor, trans, eventId);
        return build(toolkitBaseUrl, token);
    }

    static public String build(String toolkitBaseUrl, String token) {
        String relativeUrl = "#SimMsgViewer:" + token;
        return toolkitBaseUrl + relativeUrl;
    }

    static public String buildToken(String simIdString, String actor, String trans, String eventId) {
        return simIdString + "/" + actor + "/" + trans + "/" + eventId;
    }
}
