package gov.nist.toolkit.session.client;

import com.google.gwt.user.client.ui.HTML;

/**
 *
 */
public class Htmlize {
    public static HTML asHtml(String header, String in) {
        HTML h = new HTML(asString(header, in));
        return h;
    }

    public static String asString(String header, String in) {
        if (isHtml(header) || isHtml(in)) return header + in;
        return "<br />" +
                "<b>" + header + "</b><br /><br />" +
                asString(in);
    }

    public static String asString(String in) {
        if (isHtml(in)) return in;
        return in.replaceAll("<", "&lt;")
                .replaceAll("\t", "&nbsp;&nbsp;&nbsp;")
                .replaceAll(" ", "&nbsp;")
                .replaceAll("\n", "<br />");
    }

    private static boolean isHtml(String in) {
        return in.contains("<p>") || in.contains("<h2>") || in.contains("<br");
    }

}
