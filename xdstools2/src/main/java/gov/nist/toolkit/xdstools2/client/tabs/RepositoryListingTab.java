package gov.nist.toolkit.xdstools2.client.tabs;

import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RepositoryListingTab extends GenericQueryTab {
	FlexTable byNameTable = new FlexTable();
	FlexTable byUidTable = new FlexTable();
	
	String happyGif = "icons/happy0024.gif";
	String sadGif = "icons/sad0019.gif";
	
	String happyHtml = "<img src=\"" + happyGif + "\"/>";
	String sadHtml = "<img src=\"" + sadGif + "\"/>";


	public RepositoryListingTab() {
		super(new NullSiteActorManager());
	}

	public void onTabLoad(TabContainer container, boolean select, String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();


		container.addTab(topPanel, "Rep List", select);
		addCloseButton(container,topPanel, null);

		HTML title = new HTML();
		title.setHTML("<h2>Repository Listing</h2>");
		topPanel.add(title);

		topPanel.add(HtmlMarkup.html(HtmlMarkup.h3("By Name")));

		byNameTable.setBorderWidth(1);
		byNameTable.setCellSpacing(0);

		topPanel.add(byNameTable);

		topPanel.add(HtmlMarkup.html(HtmlMarkup.h3("By repositoryUniqueId")));

		topPanel.add(byUidTable);

		byUidTable.setBorderWidth(1);
		byUidTable.setCellSpacing(0);

		reload();

	}

	void reload() {

		byNameTable.clear();

		byUidTable.clear();

		toolkitService.getAllSites(loadSitesCallback);

	}

	AsyncCallback<Collection<Site>> loadSitesCallback = new AsyncCallback<Collection<Site>>() {

		public void onFailure(Throwable caught) {
			new PopupMessage("GetAllSites() failed: " + caught.getMessage());
		}

		public void onSuccess(Collection<Site> result) {
			display(result);
		}

	};

	void display(Collection<Site> sites) {
		Map<String, Site> byName = new HashMap<String, Site>();
		Map<String, Site> byUid = new HashMap<String, Site>();

		for (Site site : sites) {
			try {
				byUid.put(site.getRepositoryUniqueId(), site);
			} catch (Exception e) {}
			byName.put(site.getSiteName(), site);
		}

		List<String> names = new ArrayList<String>();
		names.addAll(byName.keySet());
		String[] namea = names.toArray(new String[1]);
		sort(namea);
		//		Collections.sort(names);

		List<String> uids = new ArrayList<String>();
		uids.addAll(byUid.keySet());
		String[] uida = uids.toArray(new String[1]);
		sort(uida);
		//		Collections.sort(uids);

		int row;
		int col;

		row=0;
		col = 0;
		byNameTable.setHTML(row, col++, HtmlMarkup.bold("Repository Name"));
		byNameTable.setHTML(row, col++, HtmlMarkup.bold("repositoryUniqueId"));
		row++;

		for (String name : namea) {
			col = 0;
			try {
				byNameTable.setText(row, 1, byName.get(name).getRepositoryUniqueId());
				byNameTable.setText(row, 0, name);
				row++;
			} catch (Exception e) {
			}
		}

		row=0;
		col=1;
		byUidTable.setHTML(row, col++, HtmlMarkup.bold("repositoryUniqueId"));
		byUidTable.setHTML(row, col++, HtmlMarkup.bold("Repository Name"));
		row++;

		for (String uid : uids)	 {
			for (String name : namea) {
				Site s = byName.get(name);
				try {
					String uidx = s.getRepositoryUniqueId();
					if (!uid.equals(uidx))
						continue;
					col=1;
					byUidTable.setText(row, col++, uid);
					byUidTable.setText(row, col++, s.getName());
					row++;
				} catch (Exception e) {}
			}
		}
		
		// duplicate uids get sad face
		for (int r=1; r<byUidTable.getRowCount()-1; r++) {
			if (byUidTable.getText(r, 1).equals(byUidTable.getText(r+1, 1))) {
				byUidTable.setHTML(r, 0, sadHtml);
				byUidTable.setHTML(r+1, 0, sadHtml);
			} else if (byUidTable.getHTML(r, 0) == null || byUidTable.getHTML(r, 0).equals("")){
				byUidTable.setHTML(r, 0, happyHtml);
			} else {
				byUidTable.setHTML(r, 0, sadHtml);
			}
		}
		
	}
	
	void sort(String[] a) {
		for (int i=0; i<a.length-1; i++) {
			for (int j=i+1; j<a.length; j++) {
				if (a[i].compareTo(a[j]) >= 0)
					swap(a, i, j);
			}
		}
	}
	
	void swap(String[] a, int i, int j) {
		String b;
		b = a[i];
		a[i] = a[j];
		a[j] = b;
	}


	public String getWindowShortName() {
		return "repositorylisting";
	}


}
