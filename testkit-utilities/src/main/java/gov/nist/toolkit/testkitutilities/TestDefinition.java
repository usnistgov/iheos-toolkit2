package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.installation.server.Installation;
import gov.nist.toolkit.interactionmodel.client.InteractingEntity;
import gov.nist.toolkit.interactionmodel.server.InteractionSequences;
import gov.nist.toolkit.interactionmodel.shared.TransactionSequenceNotFoundException;
import gov.nist.toolkit.testkitutilities.client.Gather;
import gov.nist.toolkit.testkitutilities.client.SectionDefinitionDAO;
import gov.nist.toolkit.testkitutilities.client.StepDefinitionDAO;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.utilities.io.LinesOfFile;
import gov.nist.toolkit.utilities.xml.Util;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import javax.xml.namespace.QName;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class TestDefinition {
	private File testDir;

	private static final String testPlanFileName = "testplan.xml";

	public TestDefinition(File testDir) {
		this.testDir = testDir;
	}

	public boolean exists() {
		return testDir.exists();
	}

	public String getId() {
		return testDir.getName();
	}

	public boolean isTest() {
		if ( new File(testDir + File.separator + "index.idx").exists())
			return true;
		if ( new File(testDir + File.separator + testPlanFileName).exists())
			return true;
		return false;

	}
	
	public String getFullTestReadme() throws IOException {
		return Io.stringFromFile(new File(testDir, "readme.txt"));
	}

	public String getFullSectionReadme(String section) throws IOException {
		File f = new File(new File(testDir, section), "readme.txt");
		return Io.stringFromFile(f);
	}

	/**
	 * Get test description from readme.txt file.  Description
	 * is the first line of the file.
	 * @return
	 * @throws IOException
	 */
	String getTestTitle() throws IOException {
		ReadMe readme = getTestReadme();
		if (readme == null) return "";
		return readme.line1.trim();
	}
	/**
	 * Get list of sections defined by the test.
	 * @return list of section names
	 * @throws IOException
	 */
	public List<String> getSectionIndex() {
		List<String> names = new ArrayList<String>();

		try {
			String[] parts = Io.stringFromFile(new File(testDir, "index.idx")).split("\n");

			for (int i = 0; i < parts.length; i++) {
				String name = parts[i];
				if (name == null)
					continue;
				name = name.trim();
				if (name.length() > 0)
					names.add(name);
			}
		} catch (Exception e) {}
		
		return names;
	}

	/**
	 * Get section definitions for all sections of this test
	 * @return
	 */
	public List<SectionDefinitionDAO> getSections() throws XdsInternalException {
		List<SectionDefinitionDAO> sections = new ArrayList<>();
		List<String> sectionNames = getSectionIndex();
		for (String sectionName : sectionNames) {
			SectionDefinitionDAO dao = getSection(sectionName);
			sections.add(dao);
		}
		return sections;
	}

	public SectionDefinitionDAO getSection(String sectionName) throws XdsInternalException {
		if (sectionName == null) {
			return parseTestPlan(Util.parse_xml(new File(testDir, "testplan.xml")), sectionName);
		}
		return parseTestPlan(Util.parse_xml(new File(new File(testDir, sectionName), "testplan.xml")), sectionName);
	}

	private final static QName TEST_QNAME = new QName("test");
	private final static QName ID = new QName("id");

	private SectionDefinitionDAO parseTestPlan(OMElement sectionEle, String sectionName) {
		SectionDefinitionDAO section = new SectionDefinitionDAO(sectionName);

		OMElement sutInitiatedEle = XmlUtil.firstDecendentWithLocalName(sectionEle, "SUTInitiates");
		if (sutInitiatedEle != null) section.sutInitiated();

		List<Gather> gathers = null;

		List<OMElement> gatherEles = XmlUtil.decendentsWithLocalName(sectionEle, "Gather");
		if (gatherEles.size() > 0) {
			gathers = new ArrayList<>();
			for (OMElement gatherEle : gatherEles) {
				gathers.add(new Gather(gatherEle.getAttributeValue(new QName("prompt")), null));
			}
			section.setGathers(gathers);
		}

		if (gathers == null) {
			for (OMElement stepEle : XmlUtil.decendentsWithLocalName(sectionEle, "TestStep")) {
				OMElement goalEle = XmlUtil.firstChildWithLocalName(stepEle, "Goal");
				StepDefinitionDAO step = new StepDefinitionDAO();
				step.setId(stepEle.getAttributeValue(ID));

				for (OMElement trans : XmlUtil.descendantsWithLocalNameEndsWith(stepEle, "Transaction")) {
				    if (trans.getParent() instanceof OMElement && !"Rule".equals(((OMElement) trans.getParent()).getLocalName())) {
						step.setTransaction(trans.getLocalName());
						OMElement interactionSeq = XmlUtil.firstChildWithLocalName(trans, "InteractionSequence");
						try {
							InteractionSequences.init(Installation.instance().getInteractionSequencesFile());
							String transactionKey = null;
							if (interactionSeq == null) {
								transactionKey = step.getTransaction();
							} else {
								String idVal = interactionSeq.getAttributeValue(new QName("useId"));
								if (idVal!=null && !"".equals(idVal)) {
								 	transactionKey = idVal;
								}
								// To use an embedded sequence uncomment this line.
								// transactionKey = InteractionSequences.xformSequenceToEntity(interactionSeq);
							}
							try {
								if (InteractionSequences.getInteractionSequenceById(transactionKey) != null) {
									List<InteractingEntity> src = InteractionSequences.getInteractionSequenceById(transactionKey);
									List<InteractingEntity> copyIeList = new ArrayList<>();

									if (src != null) {
										for (InteractingEntity ie : src) {
											copyIeList.add(ie.copy());
										}
										step.setInteractionSequence(copyIeList);
									}

									if (goalEle == null)
										section.addStep(step);
								}
							} catch (TransactionSequenceNotFoundException tsnfe) {
								InteractingEntity ie = new InteractingEntity();
								ie.setStatus(InteractingEntity.INTERACTIONSTATUS.UNKNOWN);
								ie.setErrors(Arrays.asList(new String[]{TransactionSequenceNotFoundException.class.getSimpleName() + ":" + trans.getLocalName() +  ": Transaction is not mappable." + tsnfe.toString()}));
								step.setInteractionSequence(new ArrayList<InteractingEntity>());
								step.getInteractionSequence().add(ie);
								section.addStep(step);
							}
						} catch (Exception ex) {}
						break; // limit one transaction diagram per step
					}
				}

				// parse goals
				if (goalEle == null) continue;
				String goalsString = goalEle.getText();
				if (goalsString != null) goalsString = goalsString.trim();
				Scanner scanner = new Scanner(goalsString);
				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();
					if (line == null) continue;
					line = line.trim();
					if (line.length() == 0) continue;
					step.addGoals(line);
				}

				for (OMElement trans : XmlUtil.descendantsWithLocalNameEndsWith(stepEle, "Transaction")) {
					for (OMElement useReport : XmlUtil.childrenWithLocalName(trans, "UseReport")) {
						String testId = useReport.getAttributeValue(TEST_QNAME);
						if (testId != null) {
							section.addTestDependency(testId);
						}
					}
				}

				section.addStep(step);
			}
		}

		return section;
	}

	public ReadMe getTestReadme()  {
		String contents = null;
		try {
			contents = getFullTestReadme();
		} catch (IOException e) {
			return null;
		}
		return parseReadme(contents);
	}

	public String getReadmeFirstLine() throws IOException {
		return getTestReadme().line1;
	}


	public ReadMe getSectionReadme(String section) {
		String contents = null;
		try {
			contents = getFullSectionReadme(section);
		} catch (IOException e) {
			return null;
		}
		return parseReadme(contents);
	}

	private ReadMe parseReadme(String readmeContents) {
		ReadMe rm = new ReadMe();

		StringBuilder buf = new StringBuilder();
		Scanner scanner = new Scanner(readmeContents);
		int i = 0;
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (i == 0) {
				rm.line1 = line;
				i++;
				continue;
			}
			buf.append(line).append('\n');
		}
		scanner.close();
		rm.rest = buf.toString();
		return rm;
	}

	public SectionTestPlanFileMap getTestPlans() throws Exception {

		File index = new File(testDir + File.separator + "index.idx");
		return getSectionTestPlanFileMap(index);

	}

	private SectionTestPlanFileMap getSectionTestPlanFileMap(File index) throws Exception {
		if (index.exists())
			return getTestPlansFromIndex(index);
		else
			return getTestPlanFromDir(testDir);
	}

	public File getIndexFile() {
		return new File(testDir + File.separator + "index.idx");
	}

	private SectionTestPlanFileMap getTestPlansFromIndex(File index) throws Exception {
		SectionTestPlanFileMap plans = new SectionTestPlanFileMap();

		for (LinesOfFile lof = new LinesOfFile(index); lof.hasNext(); ) {
			String dir = lof.next().trim();
			if (dir.length() ==0)
				continue;
			File path = new File(testDir + File.separator + dir + File.separatorChar + testPlanFileName);
			if ( ! path.exists() )
				throw new Exception("TestSpec " + toString() + " references sub-directory " + dir +
						" which does not exist or does not contain a " + testPlanFileName + " file");
			plans.put(dir, path);
		}
		return plans;
	}

	private SectionTestPlanFileMap getTestPlanFromDir(File dir) throws Exception {
		SectionTestPlanFileMap plans = new SectionTestPlanFileMap();

		File path = new File(dir + File.separator + testPlanFileName);
		if ( ! path.exists() )
			return plans;
//			throw new Exception("TestSpec " + toString() + " does not have index.idx or " + testPlanFileName + " file");
		plans.put(".", path);

		return plans;
	}

	public File getTestplanFile(String section) throws Exception {
		File path;

		if (section == null) {
			path = new File(testDir + File.separator + testPlanFileName);
		} else {
			path = new File(testDir + File.separator + section + File.separator + testPlanFileName);
		}
		if ( ! path.exists() )
			throw new Exception("Test Section " + section +
					" has been requested but does not exist or does not contain a " + testPlanFileName + " file (" + path + ")");
		return path;
	}

	public String getTestPlanText(String section) throws Exception {
		File file = getTestplanFile(section);
		return Io.stringFromFile(file);
	}

	public File getTestDir() {
		return testDir;
	}

	@Override
	public String toString() {
		return testDir.toString();
	}

	public enum TransactionType implements Serializable {
		PnR("ProvideAndRegisterTransaction"),
		REGISTER("RegisterTransaction");

		private String type;

		private TransactionType(String str){
			type=str;
		}

		public static TransactionType fromString(String transactionTypeAsAString){
			if (transactionTypeAsAString.equals(PnR.toString())){
				return PnR;
			}
			if (transactionTypeAsAString.equals(REGISTER.toString())){
				return REGISTER;
			}
			return null;
		}

		@Override
		public String toString() {
			return type;
		}

		public String getTransactionTypeTestRepositoryName(){
			if(this.equals(TransactionType.PnR)){
				return "testdata-repository";
			}else {
				return "testdata-registry";
			}
		}
	}

	public TestKitSourceEnum detectSource() {
			String testDir = getTestDir().toString();
			if ((testDir != null)) {
				if (testDir.startsWith(Installation.instance().warHome().toString())) {
					return TestKitSourceEnum.EMBEDDED;
				} else if (testDir.startsWith(Installation.instance().externalCache().toString())) {
					return TestKitSourceEnum.LOCAL;
				}
			}

		return TestKitSourceEnum.UNKNOWN;
	}

	public String getTestKitSection() {
		return getTestDir().getParentFile().getName();
	}
}

