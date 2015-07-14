package gov.nist.toolkit.session.server.services;

import gov.nist.toolkit.actorfactory.CommonServiceManager;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.SiteSpec;
import gov.nist.toolkit.session.server.Session;
import gov.nist.toolkit.xdsexception.XdsException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FindPatient extends CommonServiceManager {
	Session session;
	
	public FindPatient(Session session) throws XdsException {
		this.session = session;;
	}

	public List<Result> run(SiteSpec site, String firstName, String secondName, String lastName, String suffix, String gender, String dob, String ssn, String pid, String homeAddress1, String homeAddress2, String homeCity, String homeState, String homeZip, String homeCountry, String mothersFirstName, String mothersSecondName, String mothersLastName, String mothersSuffix, String homePhone, String workPhone, String principleCareProvider, String pob, String pobAddress1, String pobAddress2, String pobCity, String pobState, String pobZip, String pobCountry) {
		try {
			session.setSiteSpec(site);
			session.transactionSettings.assignPatientId = false;
			String testName = "FindPatient";
			List<String> sections = new ArrayList<String>();
			sections.add("XCA");
			Map<String, String> params = new HashMap<String, String>();
			params.put("$firstName_id$", firstName);
			params.put("$lastName_id$", lastName);
			params.put("$gender_id$", gender);
			params.put("$dob_id$", dob);
			
			System.out.println("Gavin: \'" + secondName + "\'");
			// Non required
			if (! secondName.equals("")) {
		      System.out.println("Gavin: HERE");
			  params.put("$secondName_id$", secondName);
			}
			if (! suffix.equals(""))
			  params.put("$suffix_id$", suffix);
			if (! ssn.equals(""))
			  params.put("$ssn_id$", ssn);
			if (! pid.equals(""))
			  params.put("$pid_id$", pid);
			if (! homeAddress1.equals(""))
			  params.put("$homeAddress1_id$", homeAddress1);
			if (! homeAddress2.equals(""))
			  params.put("$homeAddress2_id$", homeAddress2);
			if (! homeCity.equals(""))
			  params.put("$homeCity_id$", homeCity);
			if (! homeState.equals(""))
			  params.put("$homeState_id$", homeState);
			if (! homeZip.equals(""))
			  params.put("$homeZip_id$", homeZip);
			if (! homeCountry.equals(""))
			  params.put("$homeCountry_id$", homeCountry);
			if (! mothersFirstName.equals(""))
			  params.put("$mothersFirstName_id$", mothersFirstName);
			if (! mothersSecondName.equals(""))
			  params.put("$mothersSecondName_id$", mothersSecondName);
			if (! mothersLastName.equals(""))
			  params.put("$mothersLastName_id$", mothersLastName);
			if (! mothersSuffix.equals(""))
			  params.put("$mothersSuffix_id$", mothersSuffix);
			if (! homePhone.equals(""))
			  params.put("$homePhone_id$", homePhone);
			if (! workPhone.equals(""))
			  params.put("$workPhone_id$", workPhone);
			if (! principleCareProvider.equals(""))
			  params.put("$principleCareProvider_id$", principleCareProvider);
			if (! pob.equals(""))
			  params.put("$pob_id$", pob);
			if (! pobAddress1.equals(""))
			  params.put("$pobAddress1_id$", pobAddress1);
			if (! pobAddress2.equals(""))
			  params.put("$pobAddress2_id$", pobAddress2);
			if (! pobCity.equals(""))
			  params.put("$pobCity_id$", pobCity);
			if (! pobState.equals(""))
			  params.put("$pobState_id$", pobState);
			if (! pobZip.equals(""))
			  params.put("$pobZip_id$", pobZip);
			if (! pobCountry.equals(""))
			  params.put("$pobCountry_id$", pobCountry);

			Result r = session.xdsTestServiceManager().xdstest(testName, sections, params, null, null, true);
			return asList(r);
		} catch (Exception e) {
			return buildExtendedResultList(e);
		} finally {
			session.clear();
		}
	}


}
