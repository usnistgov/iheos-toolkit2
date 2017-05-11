package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteSiteCommand;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.initialization.FrameworkInitialization;
import gov.nist.toolkit.xdstools2.client.widgets.AdminPasswordDialogBox;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;
import gov.nist.toolkit.xdstools2.shared.command.request.DeleteSiteRequest;

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
//			((Xdstools2EventBus) FrameworkInitialization.data().getEventBus()).fireActorsConfigUpdatedEvent();
		}
		else {
			PasswordManagement.addSignInCallback(deleteSignedInCallback);
			PasswordManagement.addSignInCallback(updateSignInStatusCallback);

			new AdminPasswordDialogBox(actorConfigTab.getTabTopPanel());

			//				PasswordManagement.rmSignInCallback(deleteSignedInCallback);
			//				PasswordManagement.rmSignInCallback(updateSignInStatusCallback);
		}
	}
	
	// Boolean data type ignored 
	AsyncCallback<Boolean> deleteSignedInCallback = new AsyncCallback<Boolean> () {

		public void onFailure(Throwable ignored) {
		}

		public void onSuccess(Boolean ignored) {
			new DeleteSiteCommand(){
				@Override
				public void onComplete(String result) {
					actorConfigTab.currentEditSite.changed = false;
					actorConfigTab.newActorEditGrid();
					actorConfigTab.loadExternalSites();
					((Xdstools2EventBus) FrameworkInitialization.data().getEventBus()).fireActorsConfigUpdatedEvent();
				}
			}.run(new DeleteSiteRequest(FrameworkInitialization.data().getCommandContext(),actorConfigTab.currentEditSite.getName()));
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

}