package gov.nist.toolkit.desktop.client.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import gov.nist.toolkit.desktop.client.widget.ContentPanel;

import java.util.logging.Logger;

/**
 *
 */
public class TkShell extends ResizeComposite {
    private static final Logger log = Logger.getLogger(TkShell.class.getName());

    interface TkShellUiBinder extends UiBinder<Widget, TkShell> {
    }

    private static TkShellUiBinder binder = GWT.create(TkShellUiBinder.class);

    @UiField
    ContentPanel contentPanel;

    public TkShell() {
        initWidget(binder.createAndBindUi(this));
        contentPanel.addTab("Home", new DateBox());
//        if (!GWT.isProdMode()) {
            contentPanel.addTab("Log", new LogTab());
//        }

        log.info("Module loaded. BaseURL - " + GWT.getModuleBaseURL());
    }
}