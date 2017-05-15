package gov.nist.toolkit.toolkitFramework.client.util;

import com.google.gwt.user.client.ui.HTML;

/**
 *
 */
public class HtmlUtil {

    static public HTML addHTML(String html) {
        HTML msgBox = new HTML();
        msgBox.setHTML(html);
        return msgBox;
    }

    static public HTML addText(String text) {
        HTML msgBox = new HTML();
        msgBox.setText(text);
        return msgBox;
    }

}
