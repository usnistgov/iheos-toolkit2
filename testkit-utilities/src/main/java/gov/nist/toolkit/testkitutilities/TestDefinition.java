package gov.nist.toolkit.testkitutilities;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TestDefinition {
	File testDir;

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
	public String getTestTitle() throws IOException {
		ReadMe readme = getTestReadme();
		if (readme == null) return "";
		return readme.line1.trim();
	}
	/**
	 * Get list of sections defined by the test.
	 * @return list of section names
	 * @throws IOException
	 */
	public List<String> getSectionIndex() throws IOException {
		List<String> names = new ArrayList<String>();
		
		String[] parts = Io.stringFromFile(new File(testDir, "index.idx")).split("\n");
		
		for (int i=0; i<parts.length; i++) {
			String name = parts[i];
			if (name == null)
				continue;
			name = name.trim();
			if (name.length() > 0)
				names.add(name);
		}
		
		return names;
	}

	public SectionDefinitionDAO getSection(String sectionName) throws XdsInternalException {
		if (sectionName == null) {
			return parseTestPlan(Util.parse_xml(new File(testDir, "testplan.xml")));
		}
		return parseTestPlan(Util.parse_xml(new File(new File(testDir, sectionName), "testplan.xml")));
	}

	private SectionDefinitionDAO parseTestPlan(OMElement sectionEle) {
		SectionDefinitionDAO section = new SectionDefinitionDAO();
		for (OMElement stepEle : XmlUtil.decendentsWithLocalName(sectionEle, "TestStep")) {
			StepDefinitionDAO step = new StepDefinitionDAO();
			step.setId(stepEle.getAttributeValue(new QName("id")));
			OMElement goalEle = XmlUtil.firstChildWithLocalName(stepEle, "Goal");
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

			section.addStep(step);
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

	SectionTestPlanFileMap getTestPlansFromIndex(File index) throws Exception {
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

	SectionTestPlanFileMap getTestPlanFromDir(File dir) throws Exception {
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

}
