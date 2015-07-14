package gov.nist.toolkit.xdstools2.client.widgets.SiteSelectionWidget;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.StringSort;
import gov.nist.toolkit.xdstools2.client.ToolkitServiceAsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SiteSelectionWidget extends Composite   {
	VerticalPanel panel = new VerticalPanel();
	FlexTable selectGrid = new FlexTable();
	VerticalPanel siteContainer = new VerticalPanel();
	List<RadioButton> byActorButtons = null;
	ListBox samlListBox;
	CheckBox doTls;
	Map<TransactionType, List<RadioButton>> rbMap = new HashMap<TransactionType, List<RadioButton>>();

	List<TransactionType> transactionTypes;  
	CoupledTransactions couplings;
	ActorType actorType;
	TransactionOfferings transactionOfferings;
	ToolkitServiceAsync toolkitService;
	
	boolean samlSelected = false;
	boolean tlsSelected = true;
	
	/**
	 * 
	 * @param transactionOfferings
	 * @param couplings - Coupled transactions. Pass null if none.
	 * @param actorType
	 */
	public SiteSelectionWidget(CoupledTransactions couplings, ActorType actorType,
			ToolkitServiceAsync toolkitService
			) {
		this.couplings = couplings;
		this.actorType = actorType;
		this.toolkitService = toolkitService;
		
		transactionTypes = actorType.getTransactions();
		if (this.couplings == null)
			this.couplings = new CoupledTransactions();
		
		reloadTransactionOfferings();  // get transactionOfferings from server
		
		panel.add(selectGrid);
		panel.add(siteContainer);
		
		int row = 0;
				
		samlListBox = new ListBox();
		samlListBox.addItem("SAML OFF", "0");
		samlListBox.addItem("NHIN SAML", "1");
		samlListBox.setVisibleItemCount(1);
		samlListBox.addChangeHandler(new SamlSelector());
		selectGrid.setWidget(row, 1, samlListBox);

		doTls = new CheckBox("TLS?");
		doTls.setValue(tlsSelected);
		doTls.addClickHandler(new TlsSelector());
		selectGrid.setWidget(row, 2, doTls);		
		row++;
		
		
	}
	
	public VerticalPanel getTopPanel() { return panel; }
	
	
	public void redisplay(TransactionOfferings transactionOfferings) {
		this.transactionOfferings = transactionOfferings;
		
		redisplay();
	}
	
	void redisplay() {
		
		siteContainer.clear();
		
		if (actorType != null) {
			addSitesForActor(actorType, siteContainer);
		} else {
			addSitesForTransaction(transactionTypes, siteContainer);
		}
	}
		
	void addSitesForActor(ActorType at, VerticalPanel parent) {

		Grid bigGrid = new Grid(1, 2);
		
		Set<Site> sites = new HashSet<Site>();

		HTML label = new HTML();
		label.setHTML("Choose Site: ");
		bigGrid.setWidget(0, 0, label);

		List<String> siteNames = new ArrayList<String>();
		for (Site site : sites) 
			siteNames.add(site.getName());
		siteNames = new StringSort().sort(siteNames);

		for (TransactionType tt : at.getTransactions()) {
			sites.addAll(findSites(tt, true  /* tls */));
			sites.addAll(findSites(tt, false /* tls */));
		}

		int cols = 5;
		int row=0;
		int col=0;
		
		Grid sitesGrid = new Grid( sites.size()/cols + 1 , cols);
		byActorButtons = new ArrayList<RadioButton>();
		for (Site site : sites) {
			String name = site.getName();
			RadioButton rb = new RadioButton(name, /*at.getName(),*/ name);
			byActorButtons.add(rb);
			sitesGrid.setWidget(row, col, rb);
			col++;
			if (col >= cols) {
				col = 0;
				row++;
			}
		}
		
		bigGrid.setWidget(0, 1, sitesGrid);
		
		parent.add(bigGrid);

	}
	
	public String getSelectedSite() {
		for (RadioButton rb : byActorButtons) {
			if (rb.getValue())
				return rb.getName();
		}
		return null;
	}
	
	public SiteSpec getSelectedSiteSpec() {
		String siteName = getSelectedSite();
		if (siteName == null) return null;
		return new SiteSpec(siteName, actorType, null);
	}

	void addSitesForTransaction(List<TransactionType> transactionTypes, VerticalPanel parent) {
		for (TransactionType tt : transactionTypes) {
			ActorType at = ActorType.getActorType(tt);
			HTML label = new HTML();
			label.setHTML(at.getName());
			parent.add(label);

			List<Site> sites = findSites(tt, tlsSelected);

			List<String> siteNames = new ArrayList<String>();
			for (Site site : sites) 
				siteNames.add(site.getName());
			siteNames = new StringSort().sort(siteNames);

			int cols = 5;
			int row=0;
			int col=0;
			Grid sitesGrid = new Grid( sites.size()/cols + 1 , cols);
			ClickHandler ch= new ActorClickHandlerByTransaction(tt);
			List<RadioButton> buttons = new ArrayList<RadioButton>();
			for (String name : siteNames) {
				//				String name = site.getName();
				RadioButton rb = new RadioButton(tt.getName(), name);
				buttons.add(rb);
				rb.addClickHandler(ch);
				sitesGrid.setWidget(row, col, rb);
				col++;
				if (col >= cols) {
					col = 0;
					row++;
				}
			}
			rbMap.put(tt, buttons);

			parent.add(sitesGrid);
		}
		
	}

	// since to has come over from server and tt was generated here, they
	// don't align hashvalues.  Search must be done the old fashion way
	List<Site> findSites(TransactionType tt, boolean tls) {
		Map<TransactionType, List<Site>> map;

		if (tls) {
			map = transactionOfferings.tmap;
		} else {
			map = transactionOfferings.map;
		}

		for (TransactionType t : map.keySet()) {
			if (t.getName().equals(tt.getName()))
				return map.get(t);
		}
		return new ArrayList<Site>();
	}

	
	class SamlSelector implements ChangeHandler {

		public void onChange(ChangeEvent event) {
			int selectedIndex= ((ListBox)event.getSource()).getSelectedIndex();
			if( selectedIndex == 0) samlSelected = false;
			else if(selectedIndex == 1) samlSelected = true;

		}
	}
	
	class TlsSelector implements ClickHandler {

		public void onClick(ClickEvent event) {
			tlsSelected = ((CheckBox) event.getSource()).getValue();
		}

	}
	
	public boolean isTlsSelected() { return tlsSelected; }

	class ActorClickHandlerByTransaction implements ClickHandler {
		TransactionType transactionType;

		public ActorClickHandlerByTransaction(TransactionType tt) {
			this.transactionType = tt;
		}

		public void onClick(ClickEvent event) {
			for (TransactionType t : rbMap.keySet()) {
				if (t == transactionType)
					continue;

				if (couplings.isCoupled(t, transactionType))
					continue;

				for (RadioButton rb : rbMap.get(t)) {
					rb.setValue(false);
				}
			}
		}

	}

	void reloadTransactionOfferings() {
		try {
			toolkitService.getTransactionOfferings(new AsyncCallback<TransactionOfferings> () {

				public void onFailure(Throwable caught) {
					if (!isEmpty(caught))
						new PopupMessage(caught.getMessage());
				}

				public void onSuccess(TransactionOfferings to) {
					redisplay(to);
				}

			});
		} catch (Exception e) {
			if (!isEmpty(e))
				new PopupMessage(e.getMessage());
		}
	}

	boolean samlSelected() { return samlSelected; }
	boolean isEmpty(String x) { return x == null || x.equals(""); }
	boolean isEmpty(Throwable t) { return isEmpty(t.getMessage()); }
	

}
