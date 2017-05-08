package gov.nist.toolkit.desktop.client.modules.toolkit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import gov.nist.toolkit.desktop.client.event.MenuEvent;
import gov.nist.toolkit.desktop.client.event.TkEventBus;
import gov.nist.toolkit.desktop.client.widget.ClosePanel;

/**
 *
 */
public class ToolkitViewImpl extends ResizeComposite implements ToolkitView, ToolkitPresenter.Display, MenuEvent.MenuHandler, CloseHandler<ClosePanel> {
    interface ToolkitUiBinder extends UiBinder<Widget, ToolkitViewImpl> {}

    private static ToolkitUiBinder ourUiBinder = GWT.create(ToolkitUiBinder.class);

    private Presenter presenter;
    private String name;

    @UiField
    TabLayoutPanel tab;

    public ToolkitViewImpl() {
        initWidget(ourUiBinder.createAndBindUi(this));
        TkEventBus.get().addHandler(MenuEvent.TYPE, this);
    }

    public void addTab(String text, Composite content) {
        ClosePanel closePanel = new ClosePanel();
        closePanel.setText(text);
        closePanel.addCloseHandler(this);
        tab.add(content, closePanel);
        tab.selectTab(tab.getWidgetCount() - 1);
    }

    @Override
    public void onMenuSelection(MenuEvent menuEvent) {
        String contentName = menuEvent.getMenu();
        addTab(contentName, new DateBox());
    }

    @Override
    public void onClose(CloseEvent<ClosePanel> event) {
        if (tab.getWidgetCount() > 1) {
            event.getTarget().removeFromParent();
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }
}