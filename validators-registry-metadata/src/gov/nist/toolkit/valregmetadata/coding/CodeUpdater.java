package gov.nist.toolkit.valregmetadata.coding;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.xml.OMFormatter;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.FactoryConfigurationError;

import org.apache.axiom.om.OMElement;
import org.apache.commons.io.FileUtils;

public class CodeUpdater {
	private static final QName classificationSchemeQName = new QName("classificationScheme");
	private static final QName nodeRepresentationQName = new QName("nodeRepresentation");
	private static final QName valueQName = new QName("value");

	// walk the file tree looking for testplan.xml files 
	// when one is found, edit all referenced metadata files updating their codes
	// all metadata files are backed up by creating .bak file in same directory
	void walkTestData(File testDataDir, File codesFile) {
		init(codesFile);
		walkTestData2(testDataDir);
	}
	
	void walkTestData2(File dir) {
//		System.out.println("walking " + dir);
		if (!dir.isDirectory()) return;
//		System.out.println(dir + "  is a directory");
		if (isSection(dir)) {
			processSection(dir);
			return;
		}
		System.out.println(dir);
		File[] files = dir.listFiles();
		if (files == null) return;
		for (File file : files) {
			walkTestData2(file);
		}
	}

	void processSection(File section) {
		System.out.println("Section: " + section);
		try {
			if (!isSection(section)) return;
			Map<String, File> perStepMetadataFiles = metadataFileMap(section);
//			System.out.println("Section has steps: " + perStepMetadataFiles.keySet());
			for (String stepName : perStepMetadataFiles.keySet()) {
				File stepMetadataFile = perStepMetadataFiles.get(stepName);
				File stepMetadataBackupFile = new File(stepMetadataFile.toString() + ".bak");
				if (!stepMetadataBackupFile.exists()) {				
					// might be first time running - back up data
					FileUtils.copyFile(stepMetadataFile, stepMetadataBackupFile);
				}
				// now for processing  - read backup file and write original
				OMElement bakEle = Util.parse_xml(stepMetadataBackupFile);
				List<OMElement> badCodes = nonConformingCodes(bakEle);
//				System.out.println(badCodes.size() + " bad codes");
				for (OMElement b : badCodes) {
					Code c = code(b);
//					System.out.println("\t" + c);
				}
				updateCodes(badCodes);
				List<OMElement> badCodes2 = nonConformingCodes(bakEle);
//				System.out.println("after repair, " + badCodes2.size() + " bad codes remain");
				Io.stringToFile(stepMetadataFile, new OMFormatter(bakEle).toString());
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	boolean isSection(File here) {
		if (!here.isDirectory()) return false;
		if (!(new File(here, "testplan.xml").exists())) return false;
		return true;
	}

	// returns stepName ==> metadata file name
	Map<String, File> metadataFileMap(File section) {
		Map<String, File> fileMap = new HashMap<String, File>();

		OMElement testplanEle;
		try {
			testplanEle = Util.parse_xml(new File(section, "testplan.xml"));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		List<OMElement> testSteps = MetadataSupport.decendentsWithLocalName(testplanEle, "TestStep");
		for (OMElement testStep : testSteps) {
			String stepName = testStep.getAttributeValue(MetadataSupport.id_qname);
			OMElement metadataFileEle = MetadataSupport.firstDecendentWithLocalName(testStep, "MetadataFile");
			if (metadataFileEle == null) continue;
			String metadataFileName = metadataFileEle.getText();
			if (metadataFileName == null || metadataFileName.equals("")) continue;
			File metadataFile = new File(section, metadataFileName);
			if (!(metadataFile.exists())) continue;
			fileMap.put(stepName, metadataFile);
		}

		return fileMap;
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
