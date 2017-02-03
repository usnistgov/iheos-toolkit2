package gov.nist.toolkit.xdstools2.client.util;

/**
 * HTML Utilities
 */
public class HtmlUtil {

    static public boolean isHTML(String text) {
        if (text == null) return false;
        if (text.contains("<p>")) return true;
        if (text.contains("<h1>")) return true;
        if (text.contains("<h2>")) return true;
        if (text.contains("<h3>")) return true;
        return false;
    }
}
