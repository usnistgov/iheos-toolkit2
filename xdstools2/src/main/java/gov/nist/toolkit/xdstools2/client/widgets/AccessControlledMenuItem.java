package gov.nist.toolkit.xdstools2.client.widgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;

public abstract class AccessControlledMenuItem<T extends FocusWidget>  extends Composite {
    private T fw;
    private final Image lockImg = new Image("icons2/lock-icon.png");
    private final HorizontalFlowPanel fp = new HorizontalFlowPanel();
    private IndicatorType indicatorType;

    public enum IndicatorType {LOCK_ICON, DISABLE_FEATURE};

    public abstract boolean isAccessible();

    public AccessControlledMenuItem(T fw, final ClickHandler protectedClickHandler, IndicatorType indicatorType) {
        this.fw = fw;

        this.indicatorType = indicatorType;

        lockImg.setAltText("Lock icon");
        lockImg.setTitle("Admin feature");
        lockImg.setWidth("8px");
        lockImg.setHeight("8px");

        fw.addClickHandler(new ClickHandler() {
                               @Override
                               public void onClick(final ClickEvent clickEvent) {
                                   if (isAccessible()) {
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
        if (IndicatorType.LOCK_ICON.equals(indicatorType))
            fp.add(lockImg);
//        GWT.log("Ami initWidget was called.");
        initWidget(fp);
    }


    public void updateIndicatorStatus() {
        try {
            if (IndicatorType.LOCK_ICON.equals(indicatorType)) {
                lockImg.setVisible(!isAccessible());
            } else if (IndicatorType.DISABLE_FEATURE.equals(indicatorType)) {
                fw.setEnabled(isAccessible());
            }
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
        updateIndicatorStatus();
//        GWT.log("doAttachChildren was called.");
        // Register this AMI Lock Image in PasswordManagement so that on SignIn, locks can updated
        if (PasswordManagement.adminMenuItemList != null) {
            PasswordManagement.adminMenuItemList.add(AccessControlledMenuItem.this);
        } else {
//            GWT.log("Pm adminset is null.");
        }
    }

}
