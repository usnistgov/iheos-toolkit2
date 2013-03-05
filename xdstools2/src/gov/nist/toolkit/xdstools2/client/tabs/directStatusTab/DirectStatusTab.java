package gov.nist.toolkit.xdstools2.client.tabs.directStatusTab;

import gov.nist.direct.client.MessageLog;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DirectStatusTab  extends GenericQueryTab {
	MessageStatusView display;
	String user;

	public interface IMessageStatusView {
		public void build(List<MessageLog> statuss);
		public void addRow(MessageLog status);
	}

	public DirectStatusTab(BaseSiteActorManager siteActorManager) {
		super(siteActorManager);
		// TODO Auto-generated constructor stub
	}

	public DirectStatusTab() {
		super(new NullSiteActorManager());
		disableEnvMgr();
		//disableTestSesMgr();
	}

	@Override
	public void onTabLoad(TabContainer container, boolean select,
			String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		display = new MessageStatusView(topPanel, this);

		container.addTab(topPanel, "DirectStatus", select);
		addCloseButton(container,topPanel, null);
		addActorReloader();

		Button now = new Button("Load...");
		now.addClickHandler(nowClick);
		
		topPanel.add(now);
	}
	
	ClickHandler nowClick = new ClickHandler() {

		@Override
		public void onClick(ClickEvent event) {
			user = testSessionManager.getCurrentSelection();
			if (user.equals("")) 
				new PopupMessage("Select TestSession");
			else
				new MessageLoader(toolkitService, display).run(user);
		}

	};

	Anchor reload = null;

	public void addActorReloader() {
		if (reload == null) {
			reload = new Anchor();
			reload.setTitle("Reload message status");
			reload.setText("[reload]");
			addToMenu(reload);

			reload.addClickHandler(new ClickHandler() {

				public void onClick(ClickEvent event) {
					reloadStatus();
				}

			});
		}
	}

	void reloadStatus() {
		//List<String> msg_ids = new ArrayList<String>();
		//msg_ids.add("msg1");
		//msg_ids.add("msg2");
		//toolkitService.getDirectOutgoingMsgStatus("bill", msg_ids, new StatusLoadCallback(display));

		new MessageLoader(toolkitService, display).run(user);

	}

	@Override
	public String getWindowShortName() {
		// TODO Auto-generated method stub
		return "DirectStatus";
	}

}
