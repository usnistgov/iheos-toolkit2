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
        if (isHtml(in))
            return allButFirstLine(in);
        return
                in.replaceAll("<", "&lt;")
                        .replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
                        .replaceAll(" ", "&nbsp;")
                        .replaceAll("\n", "<br />");
    }

    static boolean isHtml(String x) {
        return x.contains("<p>") ||
                x.contains("<h2>") ||
                x.contains("<br");
    }

    static String allButFirstLine(String x) {
        int eol = x.indexOf('\n');
        if (eol > 0)
            return x.substring(eol+1);
        return x;
    }
}
