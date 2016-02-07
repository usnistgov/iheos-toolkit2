package gov.nist.toolkit.xdstools2.client.tabs.GatewayTestsTabs;

/**
 *
 */
public class Util {
    public static String htmlize(String header, String in) {
        return
                "<h2>" + header + "</h2>" +

                        in.replaceAll("<", "&lt;")
                                .replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
                                .replaceAll(" ", "&nbsp;")
                                .replaceAll("\n", "<br />");
    }

    public static String htmlize(String in) {
        return
                in.replaceAll("<", "&lt;")
                        .replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
                        .replaceAll(" ", "&nbsp;")
                        .replaceAll("\n", "<br />");
    }
}
