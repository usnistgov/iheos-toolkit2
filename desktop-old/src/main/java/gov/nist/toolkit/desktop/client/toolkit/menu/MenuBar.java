package gov.nist.toolkit.desktop.client.toolkit.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.StackLayoutPanel;

/**
 *
 */
public class MenuBar extends Composite {
    interface MenuBarUiBinder extends UiBinder<StackLayoutPanel, MenuBar> {
    }

    private static MenuBarUiBinder ourUiBinder = GWT.create(MenuBarUiBinder.class);

    public MenuBar() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }
}