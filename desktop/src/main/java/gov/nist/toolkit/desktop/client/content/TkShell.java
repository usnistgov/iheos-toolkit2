package gov.nist.toolkit.desktop.client.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import gov.nist.toolkit.desktop.client.event.EventBus;
import gov.nist.toolkit.desktop.client.event.StatusEvent;
import gov.nist.toolkit.desktop.client.widget.ContentPanel;
import gov.nist.toolkit.desktop.client.widget.Status;

import java.util.logging.Logger;

/**
 *
 */
public class TkShell extends ResizeComposite implements StatusEvent.StatusHandler {

    private static final Logger log = Logger.getLogger(TkShell.class.getName());

    interface TkShellUiBinder extends UiBinder<Widget, TkShell> {
    }

    private static TkShellUiBinder binder = GWT.create(TkShellUiBinder.class);

    @UiField
    ContentPanel contentPanel;

    @UiField
    Status status;

    public TkShell() {
        initWidget(binder.createAndBindUi(this));
        contentPanel.addTab("Home", new DateBox());
//        if (!GWT.isProdMode()) {
            contentPanel.addTab("Log", new LogTab());
//        }
        EventBus.get().addHandler(StatusEvent.TYPE, this);
        EventBus.get().fireEvent(new StatusEvent("your message"));
        log.info("Module loaded. BaseURL - " + GWT.getModuleBaseURL());
    }

    @Override
    public void onStatusChange(StatusEvent event) {
        status.setText(event.getStatus());
    }
}