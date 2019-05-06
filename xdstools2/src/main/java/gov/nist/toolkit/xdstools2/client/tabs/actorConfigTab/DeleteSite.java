package gov.nist.toolkit.xdstools2.client.tabs.actorConfigTab;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import gov.nist.toolkit.simcommon.client.SimIdFactory;
import gov.nist.toolkit.xdstools2.client.PasswordManagement;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.command.command.DeleteSiteCommand;
import gov.nist.toolkit.xdstools2.client.event.Xdstools2EventBus;
import gov.nist.toolkit.xdstools2.client.util.ClientUtils;
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
		if (!Xdstools2.getInstance().isSystemSaveEnabled()) {
			new PopupMessage("You don't have permission to delete a System in this Test Session");
			return;
		}

		if (SimIdFactory.isSimId(actorConfigTab.currentEditSite.getName())) {
			new PopupMessage("You cannot delete a simulator from this tool");
			return;
		}

		if (!actorConfigTab.currentEditSite.getOwner().equals(Xdstools2.getInstance().getTestSessionManager().getCurrentTestSession()) &&
				!PasswordManagement.isSignedIn) {
			new PopupMessage("You cannot delete a System you do not own");
			return;
		}

		if (PasswordManagement.isSignedIn) {
			deleteSignedInCallback.onSuccess(true);
		}
		else {
			if (Xdstools2.getInstance().multiUserModeEnabled) {
				if (! Xdstools2.getInstance().casModeEnabled) {
					deleteSignedInCallback.onSuccess(true);
				} else {
					new PopupMessage("You must be signed in as admin");
				}
			} else {
				// Owner of the site to be deleted is performing this action
				deleteSignedInCallback.onSuccess(true);
			}
		}
	}
	
	// Boolean data type ignored 
	private AsyncCallback<Boolean> deleteSignedInCallback = new AsyncCallback<Boolean> () {

		public void onFailure(Throwable ignored) {
		}

		public void onSuccess(Boolean ignored) {
			VerticalPanel body = new VerticalPanel();
			body.add(new HTML("<p>Are you sure you want to delete site '" + actorConfigTab.currentEditSite.getName() + "'?</p>"));

			Button actionButton = new Button("Yes");
		    actionButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent clickEvent) {
					new DeleteSiteCommand(){
						@Override
						public void onComplete(String result) {
							actorConfigTab.currentEditSite.changed = false;
							actorConfigTab.newActorEditGrid();
							actorConfigTab.loadExternalSites();
							((Xdstools2EventBus) ClientUtils.INSTANCE.getEventBus()).fireActorsConfigUpdatedEvent();
						}
					}.run(new DeleteSiteRequest(ClientUtils.INSTANCE.getCommandContext().withTestSession(actorConfigTab.currentEditSite.getTestSession().getValue()),
							actorConfigTab.currentEditSite.getName()));
				}
			});
			SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
			safeHtmlBuilder.appendHtmlConstant("<img src=\"icons2/garbage.png\" title=\"Delete\" height=\"16\" width=\"16\"/>&nbsp;");
			safeHtmlBuilder.appendHtmlConstant("Confirm Delete Site");
			new PopupMessage(safeHtmlBuilder.toSafeHtml() , body, actionButton);
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