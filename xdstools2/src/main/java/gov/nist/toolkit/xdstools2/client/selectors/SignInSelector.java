package gov.nist.toolkit.xdstools2.client.selectors;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.command.command.AddMesaTestSessionCommand;
import gov.nist.toolkit.xdstools2.client.event.testSession.TestSessionChangedEvent;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;

public class SignInSelector implements IsWidget {
    private FlowPanel panel = new FlowPanel();
    private HTML signInStatus = new HTML();
    private Anchor signIn = new Anchor("Sign In");
    private Anchor signOut = new Anchor("Sign Out");
    private final String signedIn = "You are signed-in as admin";
    private final String signedOut = "You are not signed-in as admin";

    public SignInSelector() {
        panel.add(signInStatus);
        panel.add(signIn);
        panel.add(signOut);
        updateDisplay();

        signIn.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent clickEvent) {
                PasswordManagement.isSignedIn = false;
                PasswordManagement.addSignInCallback(new AddMesaTestSessionCommand() {
                    @Override
                    public void onComplete(Boolean result) {
                        updateDisplay();
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
            }
        });
    }

    private void updateDisplay() {
        if (PasswordManagement.isSignedIn) {
            signInStatus.setText(signedIn);
            signOut.setVisible(true);
            signIn.setVisible(false);
            switchTestSession("default");
        } else {
            signInStatus.setText(signedOut);
            signOut.setVisible(false);
            signIn.setVisible(true);
            switchTestSession("");
        }
    }

    private void switchTestSession(String testSession) {
        ClientUtils.INSTANCE.getTestSessionManager().setCurrentTestSession(testSession);
        ClientUtils.INSTANCE.getEventBus().fireEvent(new TestSessionChangedEvent(TestSessionChangedEvent.ChangeType.SELECT, testSession));
    }

    @Override
    public Widget asWidget() {
        return panel;
    }
}
