package gov.nist.toolkit.sitemanagement;

import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.ActorType;
import gov.nist.toolkit.actortransaction.client.ATFactory.TransactionType;
import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;
import gov.nist.toolkit.sitemanagement.client.TransactionCollection;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

public abstract class SiteLoader {
	
	protected HashMap<String, Site> siteMap = new HashMap<String, Site>();

	public Site parseSite(OMElement ele) throws Exception {
		String site_name = ele.getAttributeValue(new QName("name"));
		if (site_name == null || site_name.equals(""))
			throw new Exception("Cannot parse Site with empty name from actors config file");
//		if (sites.containsKey(site_name)) 
//			throw new Exception("Site " + site_name + " is multiply defined in configuration file");
		Site site = new Site(site_name);
		parseSite(site, ele);
		putSite(site);
		return site;
	}
	
	@SuppressWarnings("unchecked")
	protected
	void parseSite(Site s, OMElement conf) throws Exception {
		if (!conf.getLocalName().equals("site"))
			throw new XdsInternalException("Site parser: top element of site definition must be site, " + 
					conf.getLocalName() + " found"	);
		s.setName(conf.getAttributeValue(new QName("name")));

		for (Iterator<OMElement> it=conf.getChildElements(); it.hasNext(); ) {
			OMElement ele = it.next();
			String ele_name = ele.getLocalName();
			String isSecureStr = ele.getAttributeValue(new QName("secure"));
			boolean isSecure = "1".equals(isSecureStr) || "true".equals(isSecureStr);
			String name = ele.getAttributeValue(new QName("name"));
			String uid = ele.getAttributeValue(new QName("uid"));
			String homeatt = ele.getAttributeValue(new QName("home"));
			String value = ele.getText();

			if (homeatt !=null)
				System.out.println("Warning: transaction element in actors.xml no longer supports the home attribute");

			String asyncStr = ele.getAttributeValue(new QName("async")); //not used
			boolean async = "1".equals(asyncStr) || "true".equals(asyncStr);
			if (name != null) {
				if (name.endsWith(".as")) {
					async = true;
					name = withoutSuffix(name, ".as");
					if (name.equals("pr") || name.equals("sq") || name.equals("r"))
						name = name + ".b";
				} 
				//name = withoutSuffix(name, ".as");
			}

			if (uid != null) {
				if (uid.endsWith(".as")) {
					async = true;
					uid = withoutSuffix(uid, ".as");
				} 
			}

			if ("transaction".equals(ele_name)) {
				s.transactions().add(name, value, isSecure, async);
			} else if ("repository".equals(ele_name)) {
				s.repositories().add(uid, value, isSecure, async);
			} else if ("home".equals(ele_name)) {
				s.home = value;
//			} else if ("PidAllocateEndpoint".equals(ele_name)) {
//				s.pidAllocateURI = ele.getText().trim();
			} else if ("patientIdFeed".equals(ele_name)) {
				String host = ele.getAttributeValue(new QName("host"));
				String port = ele.getAttributeValue(new QName("port"));
				
				s.pifHost = host;
				s.pifPort = port;
			} else {
				for (ActorType actorType : ATFactory.RetrieveActorTypes) {
					String label = actorType.getActorsFileLabel();
					if (label == null)
						continue;
					if (label.equals(ele_name)) {
						;
					}
				}
			}
		}
	}
	
	public OMElement siteToXML(Site s) {
		OMElement site_ele = MetadataSupport.om_factory.createOMElement("site", null);

		OMAttribute site_name_att = MetadataSupport.om_factory.createOMAttribute("name", null, s.getName());
		site_ele.addAttribute(site_name_att);

		TransactionCollection trans = s.transactions();
		TransactionCollection repos = s.repositories();

		if (s.home != null) {
			OMElement home_ele = MetadataSupport.om_factory.createOMElement("home", null);
			home_ele.setText(s.home);
			site_ele.addChild(home_ele);
		}
		
		for (TransactionBean tb : trans.transactions) {
			addTransactionXML(site_ele, tb);
		}
		

//		MetadataSupport.om_factory.createOMComment(site_ele, "REGISTRY");
//		for (TransactionBean tb : trans.transactions) {
//			if (ATFactory.RegistryActorType.hasTransaction(tb.getTransactionType())) {
//				addTransactionXML(site_ele, tb);
//			}
//		}
//
//		MetadataSupport.om_factory.createOMComment(site_ele, "RESPONDING GATEWAY");
//		for (TransactionBean tb : trans.transactions) {
//			if (ATFactory.RespondingGatewayActorType.hasTransaction(tb.getTransactionType())) {
//				addTransactionXML(site_ele, tb);
//			}
//		}
//
//		MetadataSupport.om_factory.createOMComment(site_ele, "INITIATING GATEWAY");
//		for (TransactionBean tb : trans.transactions) {
//			if (ATFactory.InitiatingGatewayActorType.hasTransaction(tb.getTransactionType())) {
//				addTransactionXML(site_ele, tb);
//			}
//		}
//		
//		MetadataSupport.om_factory.createOMComment(site_ele, "REPOSITORY");
//		for (TransactionBean tb : trans.transactions) {
//			if (ATFactory.RepositoryActorType.hasTransaction(tb.getTransactionType())) {
//				addTransactionXML(site_ele, tb);
//			}
//		}

		for (TransactionBean tb : repos.transactions) {
			OMElement trans_ele = MetadataSupport.om_factory.createOMElement("repository", null);
			String name_suffix = "";
			if (tb.isAsync) name_suffix = ".as";
			trans_ele.addAttribute("uid", tb.getName() + name_suffix, null);
			trans_ele.addAttribute("secure", (tb.isSecure) ? "1" : "0", null); 
			trans_ele.setText(tb.endpoint);
			site_ele.addChild(trans_ele);
		}

		if (s.pidAllocateURI != null) {
			MetadataSupport.om_factory.createOMComment(site_ele, "Patient ID allocation service to use with this site");
			OMElement pida_ele = MetadataSupport.om_factory.createOMElement("PidAllocateEndpoint", null);
			pida_ele.setText(s.pidAllocateURI);
			site_ele.addChild(pida_ele);
		}

		return site_ele;
	}

	void addTransactionXML(OMElement site_ele, TransactionBean tb) {
		boolean isTransaction = tb.getTransactionType() != null;
		OMElement trans_ele;
		
		if (isTransaction)
			trans_ele = MetadataSupport.om_factory.createOMElement("transaction", null);
		else
			trans_ele = MetadataSupport.om_factory.createOMElement("repository", null);
		String name_suffix = "";
		if (tb.isAsync) name_suffix = ".as";
		TransactionType tt = tb.getTransactionType();
		String nameValue;
		if (isTransaction) 
			nameValue = tt.getCode() + name_suffix;
		else
			nameValue = tb.getName();
		if (isTransaction) {
			trans_ele.addAttribute("name", nameValue, null);
		} else {
			trans_ele.addAttribute("uid", nameValue, null);
		}
		trans_ele.addAttribute("secure", (tb.isSecure) ? "1" : "0", null); 
		trans_ele.setText(tb.endpoint);
		site_ele.addChild(trans_ele);
	}

	
	protected void putSite(Site s) {
		siteMap.put(s.getName(), s);
	}

	protected String withoutSuffix(String in, String suffix) {
		if (in.endsWith(suffix))
			return in.substring(0, in.length() - suffix.length());
		return in;
	}

}