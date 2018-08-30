package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.util.TabWatcher;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

import java.util.List;

public abstract class ConfirmTestSessionChange {
    TabWatcher tabWatcher;
    abstract void doChange(String newValue);

    public ConfirmTestSessionChange(TabWatcher tabWatcher) {
        this.tabWatcher = tabWatcher;
    }

    public void confirm(final String newValue) {

            VerticalPanel body = new VerticalPanel();
            String alertMessage = "";
            if ((tabWatcher!=null && tabWatcher.getTabCount()>0)) {
                if (!PasswordManagement.isSignedIn)
                    alertMessage = "<b>Note</b>: This action will close " + tabWatcher.getTabCount() + " tab(s).";
            }
            body.add(new HTML("<p>Change test session?<br/>"
                    + alertMessage
                    + "</p>"));

            SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
            if (!"".equals(alertMessage)) {
                safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/ic_announcement_black_36dp_1x.png\" title=\"Alert\" height=\"16\" width=\"16\"/>&nbsp;");
            }
            safeHtmlBuilder.appendHtmlConstant("Confirm Change Test Session to " + newValue);

            Button actionBtn =  new Button("Ok");
            actionBtn.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent clickEvent) {
                    doChange(newValue);
                }
            });
            new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionBtn);
    }
}
