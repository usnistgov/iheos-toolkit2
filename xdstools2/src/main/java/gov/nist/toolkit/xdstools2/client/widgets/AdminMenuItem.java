package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;

import java.util.Objects;

import static gov.nist.toolkit.xdstools2.client.inspector.HyperlinkFactory.launchTool;

public class AdminMenuItem  extends Composite  {
    private String menuLabel;
    private Hyperlink maskedToolLink;
    private final Image lockImg = new Image("icons2/lock-icon.png");
    private final HorizontalFlowPanel fp = new HorizontalFlowPanel();

    public AdminMenuItem(final String html, final ClickHandler protectedClickHandler) {
        this.menuLabel = html;
        lockImg.setAltText("Lock icon");
        lockImg.setTitle("Admin feature");
        lockImg.setWidth("8px");
        lockImg.setHeight("8px");



        maskedToolLink = launchTool(html, new ClickHandler() {
            @Override
            public void onClick(final ClickEvent clickEvent) {
                if (PasswordManagement.isSignedIn) {
                    protectedClickHandler.onClick(clickEvent);
                } else {
                    PasswordManagement.addSignInCallback(new AsyncCallback<Boolean>() {
                        @Override
                        public void onFailure(Throwable ignored) {
                        }
                        @Override
                        public void onSuccess(Boolean ignored) {
                            // Register this AMI Lock Image in PasswordManagement so that on SignIn, all locks are hidden
                            if (PasswordManagement.adminMenuItemSet != null && !PasswordManagement.adminMenuItemSet.contains(AdminMenuItem.this)) {
                                PasswordManagement.adminMenuItemSet.add(AdminMenuItem.this);
                            } else {
                                GWT.log("Pm admiset is null.");
                            }

//                            Timer t = new Timer() {
//                                @Override
//                                public void run() {
                                    if (PasswordManagement.signInSelector != null) {
                                        PasswordManagement.signInSelector.updateDisplay();
                                    }
                                    protectedClickHandler.onClick(clickEvent);
//                                }
//                            };
//                            t.schedule(1);
                        }
                    });
                    new AdminPasswordDialogBox(fp);
                }
            }
        });
        fp.add(maskedToolLink);
        if (!PasswordManagement.isSignedIn) {
            fp.add(lockImg);
        }
        initWidget(fp);
    }


    public void displayLockedFeature(boolean isLocked) {
            lockImg.setVisible(isLocked);
    }



    @Override
    public Widget asWidget() {
        return getPanel();
    }

    public HorizontalFlowPanel getPanel() {
        return fp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdminMenuItem that = (AdminMenuItem) o;
        return menuLabel.equals(that.menuLabel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuLabel);
    }

    @Override
    public String toString() {
        return "AdminMenuItem{" +
                "menuLabel='" + menuLabel + '\'' +
                "link isAttached='" + maskedToolLink.isAttached() + '\'' +
                ", lockImg isAttached =" + lockImg.isAttached() +
                ", lockImg isVisible =" + lockImg.isVisible() +
//                "pm sis widget isAttached='" + PasswordManagement.signInSelector.asWidget().isAttached() +
                '}';
    }

    public Image getLockImg() {
        return lockImg;
    }


    @Override
    protected void doAttachChildren() {
        super.doAttachChildren();
    }
}
