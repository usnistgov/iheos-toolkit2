package gov.nist.toolkit.xdstools2.client.tabs;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import gov.nist.toolkit.http.client.HtmlMarkup;
import gov.nist.toolkit.sitemanagementui.client.Site;
import gov.nist.toolkit.sitemanagementui.client.TransactionBean;
import gov.nist.toolkit.xdstools2.client.command.command.GetAllSitesCommand;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.*;

public class RepositoryListingTab extends GenericQueryTab {
	FlexTable byNameTable = new FlexTable();
	FlexTable byUidTable = new FlexTable();

	FlexTable oddsByNameTable = new FlexTable();
	FlexTable oddsByUidTable = new FlexTable();


	String happyGif = "icons/happy0024.gif";
	String sadGif = "icons/sad0019.gif";

	String happyHtml = "<img src=\"" + happyGif + "\"/>";
	String sadHtml = "<img src=\"" + sadGif + "\"/>";


	public RepositoryListingTab() {
		super(new NullSiteActorManager());
	}

	@Override
	protected Widget buildUI() {
		FlowPanel container=new FlowPanel();
		HTML title = new HTML();
		title.setHTML("<h2>Repository Listing</h2>");
		container.add(title);

		container.add(HtmlMarkup.html(HtmlMarkup.h3("By Name")));

		byNameTable.setBorderWidth(1);
		byNameTable.setCellSpacing(0);

		container.add(byNameTable);

		container.add(HtmlMarkup.html(HtmlMarkup.h3("By repositoryUniqueId")));

		container.add(byUidTable);

		byUidTable.setBorderWidth(1);
		byUidTable.setCellSpacing(0);

		/* ODDS */
		HTML oddsTitle = new HTML();
		oddsTitle.setHTML("<hr><h2>On-Demand Document Source Listing</h2>");
		container.add(oddsTitle);

		container.add(HtmlMarkup.html(HtmlMarkup.h3("By Name")));

		oddsByNameTable.setBorderWidth(1);
		oddsByNameTable.setCellSpacing(0);

		container.add(oddsByNameTable);

		container.add(HtmlMarkup.html(HtmlMarkup.h3("By repositoryUniqueId")));

		container.add(oddsByUidTable);

		oddsByUidTable.setBorderWidth(1);
		oddsByUidTable.setCellSpacing(0);

		byNameTable.clear();
		byUidTable.clear();

		return container;
	}

	@Override
	protected void bindUI() {
		new GetAllSitesCommand() {

			@Override
			public void onComplete(Collection<Site> var1) {
				display(var1,TransactionBean.RepositoryType.REPOSITORY, byNameTable, byUidTable);
				display(var1,TransactionBean.RepositoryType.ODDS, oddsByNameTable, oddsByUidTable);
			}
		}.run(getCommandContext());
	}

	@Override
	protected void configureTabView() {
		// Doesn't need to do anything here
	}

	void display(Collection<Site> sites, TransactionBean.RepositoryType repositoryType, FlexTable byNameTbl, FlexTable byUidTbl) {
		Map<String, Site> byName = new HashMap<String, Site>();
		Map<String, Site> byUid = new HashMap<String, Site>();

		for (Site site : sites) {
			try {
				byUid.put(site.getRepositoryUniqueId(repositoryType), site); // TransactionBean.RepositoryType.REPOSITORY
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
		byNameTbl.setHTML(row, col++, HtmlMarkup.bold("Repository Name"));
		byNameTbl.setHTML(row, col++, HtmlMarkup.bold("repositoryUniqueId"));
		row++;

		for (String name : namea) {
			col = 0;
			try {
				byNameTbl.setText(row, 1, byName.get(name).getRepositoryUniqueId(repositoryType));
				byNameTbl.setText(row, 0, name);
				row++;
			} catch (Exception e) {
			}
		}

		row=0;
		col=1;
		byUidTbl.setHTML(row, col++, HtmlMarkup.bold("repositoryUniqueId"));
		byUidTbl.setHTML(row, col++, HtmlMarkup.bold("Repository Name"));
		row++;

		for (String uid : uids)	 {
			for (String name : namea) {
				Site s = byName.get(name);
				try {
					String uidx = s.getRepositoryUniqueId(repositoryType);
					if (!uid.equals(uidx))
						continue;
					col=1;
					byUidTbl.setText(row, col++, uid);
					byUidTbl.setText(row, col++, s.getName());
					row++;
				} catch (Exception e) {}
			}
		}

		// duplicate uids getRetrievedDocumentsModel sad face
		for (int r=1; r<byUidTbl.getRowCount()-1; r++) {
			if (byUidTbl.getText(r, 1).equals(byUidTbl.getText(r+1, 1))) {
				byUidTbl.setHTML(r, 0, sadHtml);
				byUidTbl.setHTML(r+1, 0, sadHtml);
			} else if (byUidTbl.getHTML(r, 0) == null || byUidTbl.getHTML(r, 0).equals("")){
				byUidTbl.setHTML(r, 0, happyHtml);
			} else {
				byUidTbl.setHTML(r, 0, sadHtml);
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