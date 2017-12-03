package gov.nist.toolkit.simcommon.client;

public class SimLogEventLinkBuilder {

    static public String buildUrl(String toolkitBaseUrl, String simIdString, String actor, String trans, String eventId) {
        String token = buildTokenPath(simIdString, actor, trans, eventId);
        return build(toolkitBaseUrl, token);
    }

    static public String build(String toolkitBaseUrl, String token) {
        String relativeUrl = buildInternal(token); //"#SimMsgViewer:" + token;
        return toolkitBaseUrl + relativeUrl;
    }

    static public String buildToken(String simIdString, String actor, String trans, String eventId) {
        return buildTokenPath(simIdString, actor, trans, eventId);
    }

    static public String buildInternal(String token) {
        return "#SimMsgViewer:" + token;
    }

    static public String buildTokenPath(String simIdString, String actor, String trans, String eventId) {
        return simIdString + "/" + actor + "/" + trans + "/" + eventId;
    }
}
