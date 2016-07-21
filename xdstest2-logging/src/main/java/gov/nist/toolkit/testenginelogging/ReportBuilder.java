package gov.nist.toolkit.testenginelogging;

import gov.nist.toolkit.testenginelogging.client.ReportDTO;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;

public class ReportBuilder  {
	static QName name_qname = new QName("name");

	static public ReportDTO parse(OMElement rep) throws XdsInternalException {
		ReportDTO r = new ReportDTO();

		r.setName(rep.getAttributeValue(name_qname));
		r.setValue(rep.getText());

		if (r.getName() == null || r.getName().equals(""))
			throw new XdsInternalException("Cannot parse Report: " + rep.toString());

		return r;
	}

	static public List<ReportDTO> parseReports(OMElement ele) throws XdsInternalException {
		List<ReportDTO> reportDTOs = new ArrayList<>();

		for (OMElement rep : XmlUtil.childrenWithLocalName(ele, "Report")) {
			ReportDTO r = parse(rep);
			reportDTOs.add(r);
		}

		return reportDTOs;
	}

	static public void setSection(List<ReportDTO> reportDTOs, String section) {
		for (ReportDTO reportDTO : reportDTOs) {
			reportDTO.setSection(section);
		}
	}


}
