package gov.nist.toolkit.xdstools2.client.tabs.directSenderTab;

import gov.nist.direct.client.config.SigningCertType;
import gov.nist.toolkit.results.client.AssertionResult;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.tk.client.PropertyNotFoundException;
import gov.nist.toolkit.xdstools2.client.PopupMessage;
import gov.nist.toolkit.xdstools2.client.TabContainer;
import gov.nist.toolkit.xdstools2.client.Xdstools2;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.BaseSiteActorManager;
import gov.nist.toolkit.xdstools2.client.siteActorManagers.NullSiteActorManager;
import gov.nist.toolkit.xdstools2.client.tabs.directSenderTab.view.DirectSenderView;
import gov.nist.toolkit.xdstools2.client.tabs.genericQueryTab.GenericQueryTab;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DirectSenderTab extends GenericQueryTab 
implements FormPanel.SubmitCompleteHandler, FormPanel.SubmitHandler {
	
	public interface Display {
		void setMessageSelections(List<String> names);
		String getFromAddress();
		void setSendingDomain(String domainName);
		String getToAddress();
		String getSelectedMessageName();
		void build();
		void addHTMLToResultPanel(String html);
		void clearResultPanel();
		boolean checkInputs();
		void displayStatus(boolean ok);
		void popupError(String msg);
		HasValueChangeHandlers<String> getToAddressTextBox();
	    HasClickHandlers getKnownCertSubmitButton();
		void setEncryptionCertAvailable(String domain, boolean avail);
		boolean isWrapped();
		void setAvailableSigningCerts(List<SigningCertType> signingCertTypes);
		SigningCertType getSigningCertType();
	}
	
	Display display;
	boolean submitComplete = true;  // needed to manage a race condition
	String sendingDomain;
	List<String> knownTargetDomains;

	public DirectSenderTab(BaseSiteActorManager siteActorManager) {
		super(new NullSiteActorManager());
	}

	public DirectSenderTab() {
		super(new NullSiteActorManager());
		disableEnvMgr();
		//disableTestSesMgr();
	}

	@Override
	public void onTabLoad(TabContainer container, boolean select,
			String eventName) {
		myContainer = container;
		topPanel = new VerticalPanel();
		display = new DirectSenderView(topPanel, this);
		
		try {
			sendingDomain = Xdstools2.tkProps().get("direct.toolkit.dns.domain");
			display.setSendingDomain(sendingDomain);
		} catch (PropertyNotFoundException e) {
			display.popupError("Property direct.toolkit.dns.domain missing from tk_props config file");
			return;
		}

		container.addTab(topPanel, "DirectSender", select);
		addCloseButton(container,topPanel, null);
		
		display.build();
		
	    display.getToAddressTextBox().addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> targetEmail) {
		    	final String email = targetEmail.getValue();
		    	if (email.indexOf('@') == -1) {
		    		display.setEncryptionCertAvailable("", false);
		    		return;
		    	}
				toolkitService.getEncryptionCertDomains(new AsyncCallback<List<String>>() {

					@Override
					public void onFailure(Throwable arg0) {
						new PopupMessage("Error: " + arg0.getMessage());
					}

					@Override
					public void onSuccess(List<String> arg0) {
						knownTargetDomains = arg0;
						String domain = email.substring(email.indexOf('@') + 1).trim();
						
						boolean found = knownTargetDomains.contains(domain);
						display.setEncryptionCertAvailable(domain, found);
					}
					
				});
			}
	    });
	    
	    display.getKnownCertSubmitButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				doSend();
			}
	    });

		new LoadTestdataList(toolkitService, "direct-messages", display).run();
		
		loadAvailableSigningCerts();
		
	}
	
	void loadAvailableSigningCerts() {
		toolkitService.getAvailableDirectSigningCerts(new AsyncCallback<List<SigningCertType>>() {

			@Override
			public void onFailure(Throwable caught) {
				new PopupMessage("Error loading available signing certs: " + caught.getMessage());
			}

			@Override
			public void onSuccess(List<SigningCertType> result) {
				display.setAvailableSigningCerts(result);
			}
			
		});
	}

	@Override
	public String getWindowShortName() {
		return "DirectSenderTab";
	}

	@Override
	public void onSubmitComplete(SubmitCompleteEvent arg0) {
		if (submitComplete)
			return;
		submitComplete = true;
		doSend();
	}

	String getDomain(String addr) {
		int i = addr.indexOf('@');
		if (i == -1)
			return "";
		return addr.substring(i+1);
	}
	
	void doSend() {
		Map<String, String> parms = new HashMap<String, String>();
		display.clearResultPanel();

		if (!display.checkInputs()) 
			return;
		
		parms.put("$direct_from_address$", display.getFromAddress() + "@" + sendingDomain);
		parms.put("$direct_server_name$", getDomain(display.getFromAddress()));
		parms.put("$direct_to_address$", display.getToAddress());
		parms.put("$direct_to_domain$", getDomain(display.getToAddress()));
		parms.put("$ccda_attachment_file$", display.getSelectedMessageName());
		parms.put("$send_wrapped$", (display.isWrapped()) ? "True" : "False");
		parms.put("$signing_cert$", display.getSigningCertType().name());
		
		new PopupMessage("signing cert chosen: " + parms.get("$signing_cert$"));

		toolkitService.directSend(parms, new AsyncCallback<List<Result>> () {
			public void onFailure(Throwable caught) {
				display.clearResultPanel();
				display.addHTMLToResultPanel("<font color=\"#FF0000\">" + "Error running validation: " + caught.getMessage() + "</font>");
			}

			public void onSuccess(List<Result> results) {
				boolean status = true;
				for (Result result : results) {
					for (AssertionResult ar : result.assertions.assertions) {
						String assertion = ar.assertion;
						if (assertion != null)
							assertion = assertion.replaceAll("\n", "<br />");
						if (ar.status) {
							display.addHTMLToResultPanel(assertion);
						} else {
							if (assertion.contains("EnvironmentNotSelectedException"))
								display.addHTMLToResultPanel("<font color=\"#FF0000\">" + "Environment Not Selected" + "</font>");
							else
								display.addHTMLToResultPanel("<font color=\"#FF0000\">" + assertion + "</font>");
							status = false;
						}
					}
				}
				display.displayStatus(status);
			}});
	}

	@Override
	public void onSubmit(SubmitEvent event) {
		if (!display.checkInputs()) 
			event.cancel();
		else
			submitComplete = false;
	}

}
