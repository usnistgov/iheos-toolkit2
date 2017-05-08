package gov.nist.toolkit.desktop.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.logging.client.HasWidgetsLogHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.logging.Logger;

/**
 *
 */
public class LogTab extends Composite {
    private static final Logger log = Logger.getLogger("");

    interface LogTabUiBinder extends UiBinder<Widget, LogTab> {
    }

    private static LogTabUiBinder binder = GWT.create(LogTabUiBinder.class);

    @UiField
    VerticalPanel logArea;

    public LogTab() {
        initWidget(binder.createAndBindUi(this));
        log.addHandler(new HasWidgetsLogHandler(logArea));
    }}