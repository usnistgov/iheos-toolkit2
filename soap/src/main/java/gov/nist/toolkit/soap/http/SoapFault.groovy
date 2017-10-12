package gov.nist.toolkit.soap.http

import gov.nist.toolkit.commondatatypes.MetadataSupport
import gov.nist.toolkit.utilities.xml.OMFormatter
import org.apache.axiom.om.OMElement

public class SoapFault {
	String faultCode = null;
	String faultReason = null;
	List<String> details = new ArrayList<String>();

	public enum FaultCodes { VersionMismatch, MustUnderstand, DataEncodingUnknown, Sender, Receiver };

	public SoapFault(FaultCodes code, String reason) {
		setFaultCode(code);
		faultReason = reason;
	}

	public SoapFault(String code, String reason) {
		if (code.contains(':'))
			code = code.split(':')[1]
		FaultCodes cod = FaultCodes.values().find { it.name() == code}
		setFaultCode(cod)
		faultReason = reason
	}

	public void addDetail(String adetail) {
		details.add(adetail);
	}

	public SoapFault(String xml) {
		def fault = new XmlSlurper(false, true).parseText(xml)
		faultCode = fault?.Code?.Value
		if (faultCode?.contains(':'))
			faultCode = faultCode.substring(faultCode.indexOf(':') + 1)
		faultCode = faultCode?.trim()
		faultReason = fault?.Reason?.Text
		faultReason = faultReason?.trim()
	}


	String getCodeString(FaultCodes code) {
		switch (code) {
		case FaultCodes.VersionMismatch:
			return "VersionMismatch";
		case FaultCodes.MustUnderstand:
			return "MustUnderstand";
		case FaultCodes.DataEncodingUnknown:
			return "DataEncodingUnknown";
		case FaultCodes.Sender:
			return "Sender";
		case FaultCodes.Receiver:
			return "Receiver";
		}

		return "Unknown";
	}

	String formattedDetails() {
		StringBuilder buf = new StringBuilder();
		boolean first = true;
		for (String d : details) {
			if (!first) buf.append('\n');
			first = false;
			buf.append(d);
		}
		return buf.toString();
	}

	public OMElement getXML() {
		OMElement root = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_qnamens);

		OMElement code = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_code_qnamens);

		OMElement code_value = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_value_qnamens);
		code_value.setText(MetadataSupport.fault_pre + ":" + faultCode);
		code.addChild(code_value);
		root.addChild(code);

		OMElement reason = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_reason_qnamens);
		OMElement text = MetadataSupport.om_factory.createOMElement(MetadataSupport.fault_text_qnamens);
		text.addAttribute("lang", "en", MetadataSupport.xml_namespace);
		text.setText(faultReason + "\n" + formattedDetails());
		reason.addChild(text);
		root.addChild(reason);

		return root;
	}

	public String asString() {
		return new OMFormatter(getXML()).toString();
	}

	public void setFaultCode(FaultCodes code) {
		faultCode = getCodeString(code);
	}

	boolean equals(o) {
		if (this.is(o)) return true
		if (getClass() != o.class) return false

		SoapFault soapFault = (SoapFault) o

		if (faultCode != soapFault.faultCode) return false
		if (faultReason != soapFault.faultReason) return false

		return true
	}

	int hashCode() {
		int result
		result = (faultCode != null ? faultCode.hashCode() : 0)
		result = 31 * result + (faultReason != null ? faultReason.hashCode() : 0)
		return result
	}

	public String toString() {
		return "Fault: code=${faultCode?.trim()} reason=${faultReason?.trim()}"
	}
}
