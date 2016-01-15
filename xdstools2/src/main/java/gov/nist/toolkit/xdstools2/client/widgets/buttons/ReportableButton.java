package gov.nist.toolkit.xdstools2.client.widgets.buttons;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.services.client.RawResponse;
import gov.nist.toolkit.xdstools2.client.ErrorHandler;

/**
 *
 */
abstract public class ReportableButton implements ClickHandler {
    final VerticalPanel panel = new VerticalPanel();

    public ReportableButton(Panel topPanel, String label) {
        final Button button = new Button(label);
        panel.add(button);
        topPanel.add(panel);
        button.addClickHandler(this);
    }

    public abstract void handleClick(ClickEvent clickEvent);

    @Override
    public void onClick(ClickEvent clickEvent) {
        while (panel.getWidgetCount() > 1)
            panel.remove(1);
        handleClick(clickEvent);
    }

    public void handleError(Throwable throwable) {
        ErrorHandler.handleError(panel, throwable);
    }

    public boolean handleError(RawResponse rawResponse, Class clas) {
        if (ErrorHandler.handleError(panel, rawResponse)) return true;
        if (rawResponse.getClass().equals(clas)) return false;
        ErrorHandler.handleError(panel, clas.getName(), clas);
        return true;
    }

    public VerticalPanel panel() { return panel; }

}
