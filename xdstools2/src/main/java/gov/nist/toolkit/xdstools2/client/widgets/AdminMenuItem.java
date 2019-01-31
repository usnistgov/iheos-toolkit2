package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;

import java.util.Objects;

public class AdminMenuItem<T extends FocusWidget>  extends Composite  {
    private T fw;
    private final Image lockImg = new Image("icons2/lock-icon.png");
    private final HorizontalFlowPanel fp = new HorizontalFlowPanel();
    private String title;

    public AdminMenuItem(T fw, final ClickHandler protectedClickHandler) {
        this.fw = fw;
        title = fw.getTitle();

        lockImg.setAltText("Lock icon");
        lockImg.setTitle("Admin feature");
        lockImg.setWidth("8px");
        lockImg.setHeight("8px");

        fw.addClickHandler(new ClickHandler() {
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


                                               if (PasswordManagement.signInSelector != null) {
                                                   PasswordManagement.signInSelector.updateDisplay();
                                               }
                                               protectedClickHandler.onClick(clickEvent);
                                           }
                                       });
                                       new AdminPasswordDialogBox(fp);
                                   }
                               }
                           });

        fp.add(fw);
        fp.add(lockImg);
//        GWT.log("Ami initWidget was called.");
        initWidget(fp);
    }


    public void displayLockedFeature(boolean isLocked) {
        try {
            lockImg.setVisible(isLocked);
        } catch (Exception ex) {
//            GWT.log("display lock feature failed");
        }
    }



    @Override
    public Widget asWidget() {
        return getPanel();
    }

    public HorizontalFlowPanel getPanel() {
        return fp;
    }


    @Override
    public String toString() {
        try {
            return "AdminMenuItem{" +
                    "menuLabel='" + fw.getTitle() + '\'' +
                    "link isAttached='" + fw.isAttached() + '\'' +
                    ", lockImg isAttached =" + lockImg.isAttached() +
                    ", lockImg isVisible =" + lockImg.isVisible() +
//                "pm sis widget isAttached='" + PasswordManagement.signInSelector.asWidget().isAttached() +
                    '}';
        } catch (Exception ex) {
            return "ami toString Exception";
        }
    }


    @Override
    protected void doAttachChildren() {
        super.doAttachChildren();
//        GWT.log("lock is shown: " + !PasswordManagement.isSignedIn);
        displayLockedFeature(!PasswordManagement.isSignedIn);
//        GWT.log("doAttachChildren was called.");
        // Register this AMI Lock Image in PasswordManagement so that on SignIn, all locks are hidden
        if (PasswordManagement.adminMenuItemList != null /* && !PasswordManagement.adminMenuItemList.contains(AdminMenuItem.this)*/) {
            PasswordManagement.adminMenuItemList.add(AdminMenuItem.this);
        } else {
//            GWT.log("Pm adminset is null.");
        }
    }

}
