package gov.nist.toolkit.xdstools2.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.selectors.SignInSelector;
import gov.nist.toolkit.xdstools2.client.widgets.AccessControlledMenuItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage details of user's password and authentication into the server. This is initialized 
 * when the HomeTab is launched - one of the first things done when creating the base window.
 * After that, AdminPasswordDialogBox is used to sign-in.
 * @author bill
 *
 */
public class PasswordManagement {
	static public String adminPassword = "";  // loaded when this tab is created (at launch)
	static public boolean isSignedIn = false;
	static private List<AsyncCallback<Boolean>> signInCallbacks = new ArrayList<AsyncCallback<Boolean>>();
	static public SignInSelector signInSelector;
	static public List<AccessControlledMenuItem> adminMenuItemList = new ArrayList<>();
	
	public static void addSignInCallback(AsyncCallback<Boolean> callback) {
		signInCallbacks.add(callback);
	}
	
//	public static void rmSignInCallback(AsyncCallback<Boolean> notify) {
//		signInCallbacks.remove(notify);
//	}
	
	/**
	 * Register notify to be called after authentication completes.  Authentication
	 * is an asynchronous event so the callbacks represent something to be
	 * done after authentication is successful.
	 */
	public static void callSignInCallbacks() {
		for (AsyncCallback<Boolean> callback : signInCallbacks) {
			if (isSignedIn)
				callback.onSuccess(true);
			else
				callback.onFailure(null);
		}
		clearSignInCallbacks();
	}
	
	static public void comparePassword(String password) {
		isSignedIn = adminPassword.equals(password);
//		return isSignedIn;
	}

	public static void clearSignInCallbacks() {
		signInCallbacks.clear();
	}

}
