package gov.nist.toolkit.registrysupport.logging;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.xml.OMFormatter;

import java.util.ArrayList;
import java.util.List;

import org.apache.axiom.om.OMElement;

public class RegistryResponseLog {
	OMElement resp;
	boolean success;
	List<RegistryErrorLog> errors;

	public RegistryResponseLog(OMElement res) throws Exception {
		this.resp = res;
		if (res == null)
			throw new Exception("null response");

		if (resp.getLocalName().equals("RetrieveDocumentSetResponse"))
			resp = MetadataSupport.firstChildWithLocalName(resp, "RegistryResponse");

		String status = lastPart(resp.getAttributeValue(MetadataSupport.status_qname), ":");
		if ("Success".equals(status))
			success = true;
		else if ("Failure".equals(status))
			success = false;
		else
			throw new Exception("RegistryResponse: do not understand status = " + status +
					"\nin response\n" + resp);

		errors = new ArrayList<RegistryErrorLog>();

		OMElement regErrList = MetadataSupport.firstChildWithLocalName(resp, "RegistryErrorList");
		if (regErrList == null)
			return;
		List<OMElement> errEles = MetadataSupport.childrenWithLocalName(regErrList, "RegistryError");
		for (OMElement errEle : errEles) {
			errors.add(new RegistryErrorLog(errEle));
		}

	}

	public RegistryResponseLog(List<RegistryErrorLog> errors) {
		success = false;
		this.errors = errors;
	}

	public int size() {
		return errors.size();
	}

	public RegistryErrorLog getError(int i) throws Exception {
		if (i < 0 || i >= errors.size()) 
			throw new Exception("RegistryResponseLog#getError: step index " + i + " does not exist");
		return errors.get(i);
	}

	public String getErrorSummary() {
		StringBuffer buf = new StringBuffer();

		for (RegistryErrorLog err : errors) {
			buf.append(lastPart(err.severity, ":"));
			buf.append(" : ");
			buf.append(err.errorCode);
			buf.append(" : ");
			buf.append(err.codeContext);
			buf.append('\n');
			buf.append("location:\n");
			buf.append(err.location);
			buf.append('\n');
		}

		return buf.toString();
	}

	public List<RegistryErrorLog> getErrorsDontMatch(String matchString) {
		List<RegistryErrorLog> errs = new ArrayList<RegistryErrorLog>();

		for (RegistryErrorLog re : errors) {
			String codeContext = re.codeContext;
			String errorCode = re.errorCode;

			if (codeContext == null || errorCode == null) {
				errs.add(re);
			}
			else if (matchString == null || codeContext.indexOf(matchString) == -1 && errorCode.indexOf(matchString) == -1)
				errs.add(re);
		}

		return errs;
	}

	String lastPart(String stringToBeParsed, String separator) {
		if (stringToBeParsed == null)
			return "";
		String[] parts = stringToBeParsed.split(separator);
		if (parts.length == 0)
			return "";
		return parts[parts.length - 1];
	}

	public String toString() {
		try {
			return new OMFormatter(resp.toString()).toString();
		} catch (Exception e) {
			return "";
		}
	}
}
