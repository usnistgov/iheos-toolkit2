package gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab;

import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.registrymetadata.client.AnyId;
import gov.nist.toolkit.registrymetadata.client.AnyIds;
import gov.nist.toolkit.registrymetadata.client.ObjectRef;
import gov.nist.toolkit.registrymetadata.client.ObjectRefs;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.sitemanagement.client.TransactionOfferings;
import gov.nist.toolkit.tk.client.TkProps;
import gov.nist.toolkit.xdstools2.client.CoupledTransactions;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.TabbedWindow;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Infrastructure for any tab that will allow a site to be chosen,
 * issue a transaction, get back results, 
 * and allow the results to be inspected
 * @author bill
 *
 */
public abstract class GenericQueryTab  extends TabbedWindow {
	GenericQueryTab me;

	protected FlexTable mainGrid;

	protected VerticalPanel resultPanel = null;
	public TabContainer myContainer;
	CheckBox doTls = new CheckBox("TLS?");
	ListBox samlListBox = new ListBox();
	List<RadioButton> byActorButtons = null;
//	public Map<TransactionType, List<RadioButton>> perTransTypeRadioButtons;

	List<Result> results;
	private Button inspectButton;
	private Button goButton;

	boolean asyncEnabled = false;
	public boolean doASYNC = false;
	BaseSiteActorManager siteActorManager;// = new SiteActorManager(this);
	boolean hasPatientIdParam = false;
	
	static TransactionOfferings transactionOfferings = null;  // Loaded from server

	protected QueryBoilerplate queryBoilerplate = null;
	

	HTML statusBox = new HTML();
	public TextBox pidTextBox;


	public GenericQueryTab(BaseSiteActorManager siteActorManager) {
		me = this;
		this.siteActorManager = siteActorManager;
		siteActorManager.setGenericQueryTab(this);
		

		// when called as HomeTab is built, the wrong session services this call, this
		// makes sure the job gets done
		//		EnvironmentSelector.SETENVIRONMENT(toolkitService);
	}

	public boolean isTLS() {
		return doTls.getValue();
	}
	
	public boolean isSaml() {
		int selection = samlListBox.getSelectedIndex();
		if (selection == 1)
			return true;  // first selection must be no saml
		return false;
	}

	protected AsyncCallback<List<Result>> queryCallback = new AsyncCallback<List<Result>> () {
		

		public void onFailure(Throwable caught) {
			resultPanel.clear();
			resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Error running validation: " + caught.getMessage() + "</font>"));
		}

		public void onSuccess(List<Result> theresult) {
			boolean status = true;
			results = theresult;
			for (Result result : results) {
				for (AssertionResult ar : result.assertions.assertions) {
					String assertion = ar.assertion.replaceAll("\n", "<br />");
					if (ar.status) {
						resultPanel.add(addHTML(assertion));
					} else {
						if (assertion.contains("EnvironmentNotSelectedException"))
							resultPanel.add(addHTML("<font color=\"#FF0000\">" + "Environment Not Selected" + "</font>"));
						else
							resultPanel.add(addHTML("<font color=\"#FF0000\">" + assertion + "</font>"));
						status = false;
					}
				}
			}
			if (status)
				setStatus("Status: Success", true);
			else
				setStatus("Status: Failure", false);
			getInspectButton().setEnabled(true);
			getGoButton().setEnabled(true);
		}

	};


	
	public void tabIsSelected() { 
		System.out.println("tab selected: " + getCommonSiteSpec());
		
		doTls.setValue(getCommonSiteSpec().isTls());
		samlListBox.setSelectedIndex((getCommonSiteSpec().isSaml) ? 1 : 0);
		if (pidTextBox != null)
			pidTextBox.setText(getCommonPatientId());

//		String defaultName = defaultSiteSpec.getName();
//		for (RadioButton rb : byActorButtons) {
//			String name = rb.getName();
//			if (defaultName.equals(name)) rb.setValue(true);
//		}

	}
	
	public void setSiteSpec(SiteSpec siteSpec) { setCommonSiteSpec(siteSpec); }
	
//	protected SiteSpec verifySiteSelection() {
//		setCommonSiteSpec(siteActorManager.verifySiteSelection());
//		return getCommonSiteSpec();
//	}

	public QueryBoilerplate getQueryBoilerplate() {
		return queryBoilerplate;
	}

	protected QueryBoilerplate addQueryBoilerplate(ClickHandler runner, List<TransactionType> transactionTypes, CoupledTransactions couplings, ActorType selectByActor) {
		if (queryBoilerplate != null) {
			queryBoilerplate.remove();
			queryBoilerplate = null;
		}
		queryBoilerplate = new QueryBoilerplate(
				this, runner, transactionTypes,
				couplings, selectByActor
				);
		return queryBoilerplate;
	}

	protected QueryBoilerplate addQueryBoilerplate(ClickHandler runner, List<TransactionType> transactionTypes, CoupledTransactions couplings) {
		if (queryBoilerplate != null) {
			queryBoilerplate.remove();
			queryBoilerplate = null;
		}
		queryBoilerplate = new QueryBoilerplate(
				this, runner, transactionTypes,
				couplings
				);
		return queryBoilerplate;

	}

	protected QueryBoilerplate addQueryBoilerplate(ClickHandler runner, List<TransactionType> transactionTypes, 
			CoupledTransactions couplings, boolean hasPatientIdParam) {
		if (queryBoilerplate != null) {
			queryBoilerplate.remove();
			queryBoilerplate = null;
		}
		this.hasPatientIdParam = hasPatientIdParam;
		queryBoilerplate = new QueryBoilerplate(
				this, runner, transactionTypes,
				couplings
				);
		return queryBoilerplate;
	}

	public String getSelectedValueFromListBox(ListBox lb) {
		int i = lb.getSelectedIndex();
		if ( i == -1)
			return null;
		return lb.getValue(i);
	}

	public HTML addHTML(String html) {		
		HTML msgBox = new HTML();
		msgBox.setHTML(html);
		return msgBox;		
	}

	HTML addText(String text) {		
		HTML msgBox = new HTML();
		msgBox.setText(text);
		return msgBox;		
	}

	protected void showMessage(Throwable caught) {
		showMessage(caught.getMessage());
	}

	protected void showMessage(String message) {		
		HTML msgBox = new HTML();
		msgBox.setHTML("<b>" + message + "</b>");
		topPanel.add(msgBox);		
	}

	protected List<String> formatIds(String value) {
		List<String> values = new ArrayList<String>();

		String[] parts = value.split("[,;() \t\n\r']");

		for (int i=0; i<parts.length; i++) {
			String v = parts[i];
			if (v != null) {
				v = v.trim();
				if (!v.equals(""))
					values.add(v);
			}
		}

		return values;
	}

	// all UUIDs or all UIDs
	protected boolean verifyUuids(List<String> ids) {
		if (ids.size() == 0)
			return true;
		boolean isUUID = (ids.get(0).startsWith("urn:uuid:"));
		for (String id : ids) {
			if (id.startsWith("urn:uuid:") != isUUID)
				return false;
		}
		return true;
	}

	protected ObjectRefs getObjectRefs(List<String> ids) {
		ObjectRefs or = new ObjectRefs();

		for (String id : ids) {
			or.objectRefs.add(new ObjectRef(id));
		}

		return or;
	}

	protected AnyIds getAnyIds(List<String> ids) {
		AnyIds aids = new AnyIds();

		for (String id : ids) {
			aids.add(new AnyId(id));
		}
		return aids;
	}

	String red(String msg, boolean status) {
		if (status)
			return msg;
		return red(msg);
	}

	public void setStatus(String message, boolean status) {
		statusBox.setHTML(bold(red(message,status)));
	}

	public String getRunningMessage() {
		return "Running (connection timeout is 30 sec) ...";
	}

	public void addStatusBox() {
		addStatusBox(getRunningMessage());
	}

	public void addStatusBox(String initialMessage) {
		setStatus(initialMessage, true);
		resultPanel.add(statusBox);
	}

	public Button getGoButton() {
		return goButton;
	}

	public void setGoButton(Button goButton) {
		this.goButton = goButton;
	}

	public Button getInspectButton() {
		return inspectButton;
	}

	public void setInspectButton(Button inspectButton) {
		this.inspectButton = inspectButton;
	}
	


}
