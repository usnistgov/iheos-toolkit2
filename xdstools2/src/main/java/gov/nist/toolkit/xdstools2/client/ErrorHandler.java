package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.services.client.RawResponse;

/**
 *
 */
public class ErrorHandler {

    public static boolean handleError(final VerticalPanel hPanel, final RawResponse rawResponse) {
        if (rawResponse.isError()) {
            hPanel.add(new HTML(HtmlMarkup.red("<p>Error: " + rawResponse.getErrorMessage() + "</p>")));
            return true;
        }
        hPanel.add(new HTML("<p>Success</p>"));
        return false;
    }

    public static void handleError(final VerticalPanel hPanel, final Throwable e) {
        hPanel.add(new HTML(HtmlMarkup.red("<p>Error: " + e.getMessage() + "</p>")));
    }

    public static void handleError(final VerticalPanel hPanel, final String expectedClassName, final Class clas) {
        hPanel.add(new HTML(HtmlMarkup.red("<p>Error: expected result of type " + expectedClassName + " got " + clas.getName() + " instead </p>")));
    }
}
