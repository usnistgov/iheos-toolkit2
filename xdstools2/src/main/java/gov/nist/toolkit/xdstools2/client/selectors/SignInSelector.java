package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.AddTestSessionCommand;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.AdminMenuItem;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

public class SignInSelector implements IsWidget {
    private FlowPanel panel = new HorizontalFlowPanel();
    private HTML signInStatus = new HTML();
    private Anchor signIn = new Anchor("Sign In");
    private Anchor signOut = new Anchor("Sign Out");
    private final String signedIn = "You are signed-in";
    private final String signedOut = "You are not signed-in";

    public SignInSelector() {
//        panel.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"));
        panel.add(signInStatus);
        panel.add(signIn);
        panel.add(signOut);
        panel.addStyleName("right");
        updateDisplay();
        switchTestSession(ClientUtils.INSTANCE.getTestSessionManager().getCurrentTestSession());

        signIn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                PasswordManagement.isSignedIn = false;
                PasswordManagement.addSignInCallback(new AddTestSessionCommand() {
                    @Override
                    public void onComplete(Boolean result) {
                        updateDisplay();
                        //switchTestSession("default");
                    }
                });
                new AdminPasswordDialogBox(panel);
            }
        });

        signOut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                PasswordManagement.isSignedIn = false;
                updateDisplay();
                //switchTestSession("");
            }
        });
    }

    public void updateDisplay() {

        if (PasswordManagement.adminMenuItemList !=null && !PasswordManagement.adminMenuItemList.isEmpty()) {
            GWT.log("signinselector ami list size is: " + PasswordManagement.adminMenuItemList.size());
            for (AdminMenuItem ami : PasswordManagement.adminMenuItemList) {
//                GWT.log("sis ami lockimg isAttached? " + ami.getLockImg().isAttached());
//                   ami.getPanel().remove(ami.getLockImg());
                try {
                    if (ami.isAttached()) {
                        ami.displayLockedFeature(!PasswordManagement.isSignedIn);
                    } else {
                        GWT.log("ami is not attached.");
//                    PasswordManagement.adminMenuItemList.remove(ami);
                    }
                } catch (Exception ex) {
                    GWT.log("Error accessing ami");
                }
            }
        }

        if (PasswordManagement.isSignedIn) {
            signInStatus.setText(signedIn);
            signOut.setVisible(true);
            signIn.setVisible(false);
        } else {
            signInStatus.setText(signedOut);
            signOut.setVisible(false);
            signIn.setVisible(true);
//            Xdstools2.getInstance().exitTestSession();
        }
        Xdstools2.getInstance().enableTestSessionSelection();
    }

    private void switchTestSession(String testSession) {
        ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(testSession);
        ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, testSession, "SignInSelector"));
    }

    @Override
    public String toString() {
        return "SignInSelector{" +
                "signInStatus isAttached=" + signInStatus.isAttached() +
                ", signIn isAttached=" + signIn.isAttached() +
                ", signOut isAttached=" + signOut.isAttached() +
                '}';
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
