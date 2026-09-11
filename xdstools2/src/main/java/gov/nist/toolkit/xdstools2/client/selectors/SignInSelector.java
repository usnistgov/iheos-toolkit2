package gov.nist.toolkit.xdstools2.client.selectors;

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
import gov.nist.toolkit.xdstools2.client.widgets.AccessControlledMenuItem;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.widgets.HorizontalFlowPanel;

import java.util.Map;

public class SignInSelector implements IsWidget {
    private FlowPanel panel = new HorizontalFlowPanel();
    private HTML signInStatus = new HTML();
    private String signInLabel   = "Sign In.";
    private String signOutLabel  = "Sign Out.";
    private String signedIn      = "You are signed-in.";
    private String signedOut     = "You are not signed-in.";

    private Anchor signIn  = null;
    private Anchor signOut = null;

    public SignInSelector() {
        Map<String,String> tkPropMap = ClientUtils.INSTANCE.getTkPropMap();
        signInLabel  = tkPropMap.containsKey("Sign_in_label")  ? tkPropMap.get("Sign_in_label")  : signInLabel;
        signOutLabel = tkPropMap.containsKey("Sign_out_label") ? tkPropMap.get("Sign_out_label") : signOutLabel;
        signedIn     = tkPropMap.containsKey("Signed_in")      ? tkPropMap.get("Signed_in")      : signedIn;
        signedOut    = tkPropMap.containsKey("Signed_out")     ? tkPropMap.get("Signed_out")     : signedOut;

        signIn = new Anchor(signInLabel);
        signOut = new Anchor(signOutLabel);

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
                PasswordManagement.hash = "";
                PasswordManagement.isSignedIn = false;
                updateDisplay();
                //switchTestSession("");
            }
        });
    }

    public void updateDisplay() {

        if (PasswordManagement.adminMenuItemList !=null && !PasswordManagement.adminMenuItemList.isEmpty()) {
//            GWT.log("signinselector ami list size is: " + PasswordManagement.adminMenuItemList.size());
            for (AccessControlledMenuItem ami : PasswordManagement.adminMenuItemList) {
                try {
                    if (ami != null && ami.isAttached()) {
                        ami.updateIndicatorStatus();
                    } else {
//                        GWT.log("ami is not attached.");
                    }
                } catch (Exception ex) {
//                    GWT.log("Error accessing ami");
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
