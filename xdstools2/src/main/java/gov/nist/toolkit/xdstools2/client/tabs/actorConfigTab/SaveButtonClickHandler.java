package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
import gov.nist.toolkit.xdstools2.client.widgets.PopupMessage;

class SaveButtonClickHandler implements ClickHandler {
	/**
	 * 
	 */
	private ActorConfigTab actorConfigTab;
	
	public SaveButtonClickHandler(ActorConfigTab actorConfigTab) {
		this.actorConfigTab = actorConfigTab;
	}
	
	public void onClick(ClickEvent event) {
		save();
	}

	public boolean save() {
		if (actorConfigTab.currentEditSite.getName().equals(actorConfigTab.newSiteName)) {
			new PopupMessage("You must give site a real name before saving");
			return false;
		}
		if (!Xdstools2.getInstance().isSystemSaveEnabled()) {
			new PopupMessage("You don't have permission to create a save/update a System in this Test Session");
			return false;
		}
		if (!actorConfigTab.currentEditSite.getOwner().equals(Xdstools2.getInstance().getTestSessionManager().getCurrentTestSession()) &&
				!PasswordManagement.isSignedIn) {
			new PopupMessage("You cannot update a System you do not own");
			return false;
		}

		actorConfigTab.currentEditSite.cleanup();
		StringBuffer errors = new StringBuffer();
		actorConfigTab.currentEditSite.validate(errors);
		if (errors.length() > 0) {
			new PopupMessage(errors.toString());
			return false;
		}

		if (PasswordManagement.isSignedIn) {
			if (!actorConfigTab.currentEditSite.hasOwner())
				actorConfigTab.currentEditSite.setOwner(actorConfigTab.currentEditSite.getTestSession().getValue());
			actorConfigTab.saveSignedInCallback.onSuccess(true);
			((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
		}
		else {
			if (Xdstools2.getInstance().multiUserModeEnabled && !Xdstools2.getInstance().casModeEnabled) {
				if (!actorConfigTab.currentEditSite.hasOwner())
					actorConfigTab.currentEditSite.setOwner(actorConfigTab.currentEditSite.getTestSession().getValue());
				actorConfigTab.saveSignedInCallback.onSuccess(true);
				actorConfigTab.loadExternalSites();
				((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
			} else {
				new PopupMessage("You must be signed in as admin");
				return false;
			}
		}
		return true;
	}

}