package gov.nist.toolkit.valregmetadata.coding;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;

public class CodeUpdater {
	private static final QName classificationSchemeQName = new QName("classificationScheme");
	private static final QName nodeRepresentationQName = new QName("nodeRepresentation");
	private static final QName valueQName = new QName("value");

	// Each submission directory contains:
	//  testplan.xml
	//  metadata.xml
	// This loop creates a backup of metadata.xml in metadata.bak
	// metadata.xml becomes the new, cleaned up metadata file
	void walkTestData(File testDataDir, File codesFile) throws IOException, XdsInternalException, FactoryConfigurationError {
		init(codesFile);
		File[] submissions = testDataDir.listFiles();
		if (submissions == null) return;
		for (File submission : submissions) {
			File orig = new File(submission, "metadata.xml");
			File bak = new File(submission, "metadata.bak");
			if (!bak.exists()) {				
				// might be first time running - back up data
				if (!orig.exists()) {
					// not a valid submission - skip
					continue;
				}
				FileUtils.copyFile(orig, bak);
			}

			// now for processing read backup file and write original
			System.out.println(bak);
			OMElement bakEle = Util.parse_xml(bak);
			List<OMElement> badCodes = nonConformingCodes(bakEle);
			System.out.println(badCodes.size() + " bad codes");
			for (OMElement b : badCodes) {
				Code c = code(b);
				System.out.println("\t" + c);
			}
			updateCodes(badCodes);
			List<OMElement> badCodes2 = nonConformingCodes(bakEle);
			System.out.println("after repair, " + badCodes2.size() + " bad codes remain");
			Io.stringToFile(orig, new OMFormatter(bakEle).toString());
		}
	}

	AllCodes allCodes = null;

	void init(File codesFile) {
		allCodes = new CodesFactory().load(codesFile);
	}

	List<OMElement> nonConformingCodes(OMElement data) {
		List<OMElement> badCodes = new ArrayList<OMElement>();
		List<OMElement> classifications = MetadataSupport.decendentsWithLocalName(data, "Classification");
		for (OMElement classification : classifications) {
			String classificationScheme = classification.getAttributeValue(classificationSchemeQName); 
			if (classificationScheme == null || classificationScheme.equals(""))
				continue;
			Uuid classificationUuid = new Uuid(classificationScheme);
			if (!allCodes.isKnownClassification(classificationUuid))
				continue;
			Code code = code(classification);
			if (!allCodes.exists(classificationUuid, code)) 
				badCodes.add(classification);
		}
		return badCodes;
	}

	void updateCodes(List<OMElement> badCodes) throws XdsInternalException, FactoryConfigurationError {
		for (OMElement classification : badCodes) {
			String classificationScheme = classification.getAttributeValue(classificationSchemeQName); 
			Uuid classificationUuid = new Uuid(classificationScheme);
			Code newCode = allCodes.pick(classificationUuid);
			updateClassification(classification, newCode);
		}
	}

	void updateClassification(OMElement classification, Code code) {
		classification.getAttribute(nodeRepresentationQName).setAttributeValue(code.getCode());

		OMElement codeSystemElement = codeSystemElement(classification);
		if (codeSystemElement != null) codeSystemElement.setText(code.getScheme());
		updateDisplayName(classification, code.getDisplay());
	}

	Code code(OMElement classification) {
		String value = classification.getAttributeValue(nodeRepresentationQName);
		String displayName = displayName(classification);
		String codeSystem = "";
		OMElement codeSystemElement = codeSystemElement(classification);
		if (codeSystemElement == null) return new Code(value, codeSystem, displayName);
		codeSystem = codeSystemElement.getText();
		return new Code(value, codeSystem, displayName);
	}

	String displayName(OMElement classification) {
		OMElement nameElement = MetadataSupport.firstChildWithLocalName(classification, "Name");
		if (nameElement == null) return "";
		OMElement localizedStringElement = MetadataSupport.firstChildWithLocalName(nameElement, "LocalizedString");
		if (localizedStringElement == null) return "";
		return localizedStringElement.getAttributeValue(valueQName);
	}

	void updateDisplayName(OMElement classification, String displayName) {
		OMElement nameElement = MetadataSupport.firstChildWithLocalName(classification, "Name");
		if (nameElement == null) return;
		OMElement localizedStringElement = MetadataSupport.firstChildWithLocalName(nameElement, "LocalizedString");
		if (localizedStringElement == null) return;
		localizedStringElement.getAttribute(valueQName).setAttributeValue(displayName);
	}

	OMElement codeSystemElement(OMElement classification) {
		OMElement slot = MetadataSupport.firstChildWithLocalName(classification, "Slot");
		if (slot == null) return null;
		OMElement codeSystemElement = slotValueElement(slot);
		return codeSystemElement;
	}

	OMElement slotValueElement(OMElement slot) {
		OMElement valueList = MetadataSupport.firstChildWithLocalName(slot, "ValueList");
		if (valueList == null) return null;
		OMElement value = MetadataSupport.firstChildWithLocalName(valueList, "Value");
		if (value == null) return null;
		return value;
	}
}
