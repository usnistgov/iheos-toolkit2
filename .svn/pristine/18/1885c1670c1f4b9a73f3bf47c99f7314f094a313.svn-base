package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import gov.nist.toolkit.xdstools2.client.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.PopupMessage;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;

class DeleteSite implements ClickHandler {

	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;

	/**
	 * @param actorConfigTab
	 */
	DeleteSite(ActorConfigTab actorConfigTab) {
		this.actorConfigTab = actorConfigTab;
	}

	public void onClick(ClickEvent event) {
		if (this.actorConfigTab.currentEditSite == null) {
			new PopupMessage("Must choose site first");
			return;
		}
		if (PasswordManagement.isSignedIn) {
			deleteSignedInCallback.onSuccess(true);
		}
		else {
			PasswordManagement.addSignInCallback(deleteSignedInCallback);
			PasswordManagement.addSignInCallback(updateSignInStatusCallback);

			new AdminPasswordDialogBox(this.actorConfigTab.topPanel);

			//				PasswordManagement.rmSignInCallback(deleteSignedInCallback);
			//				PasswordManagement.rmSignInCallback(updateSignInStatusCallback);
		}
	}
	
	// Boolean data type ignored 
	AsyncCallback<Boolean> deleteSignedInCallback = new AsyncCallback<Boolean> () {

		public void onFailure(Throwable ignored) {
		}

		public void onSuccess(Boolean ignored) {
			actorConfigTab.toolkitService.deleteSite(actorConfigTab.currentEditSite.getName(), deleteSiteCallback);
		}

	};
	
	// Boolean data type ignored 
	AsyncCallback<Boolean> updateSignInStatusCallback = new AsyncCallback<Boolean> () {

		public void onFailure(Throwable ignored) {
			actorConfigTab.updateSignInStatus();
		}

		public void onSuccess(Boolean ignored) {
			actorConfigTab.updateSignInStatus();
		}

	};

	protected AsyncCallback<String> deleteSiteCallback = new AsyncCallback<String>() {

		public void onFailure(Throwable caught) {
			new PopupMessage(caught.getMessage());
		}

		public void onSuccess(String result) {
			actorConfigTab.currentEditSite.changed = false;
			actorConfigTab.newActorEditGrid();
			actorConfigTab.loadExternalSites();
		}

	};



}