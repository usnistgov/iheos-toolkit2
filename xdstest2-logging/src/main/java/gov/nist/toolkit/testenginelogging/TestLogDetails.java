package gov.nist.toolkit.testenginelogging;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.testenginelogging.client.LogFileContentDTO;
import gov.nist.toolkit.testenginelogging.client.SectionLogMapDTO;
import gov.nist.toolkit.testenginelogging.logrepository.LogRepository;
import gov.nist.toolkit.utilities.io.LinesOfFile;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestLogDetails {

	private File testkit;
	private LogRepository logRepository = null;
	private String area;  // examples, tests etc
	private TestInstance testInstance;
	public SectionTestPlanFileMap testPlanFileMap;   // sectionName ==> testplan.xml file
	public SectionLogMapDTO sectionLogMapDTO;
	private String[] areas;

	private static Logger logger = Logger.getLogger(TestLogDetails.class);


	private static final String testPlanFileName = "testplan.xml";

	public TestLogDetails(File testkit, TestInstance testInstance) throws Exception {
		this.testkit = testkit;
		this.testInstance = testInstance;
		areas = Installation.defaultAreas;
		sectionLogMapDTO = new SectionLogMapDTO(testInstance);
		verifyCurrentTestExists(testkit, testInstance);
		testPlanFileMap = getTestPlans();
//		logRepository = LogRepositoryFactory.getRepository(Installation.installation().testLogCache(), testInstance.getUser(), LogIdIOFormat.JAVA_SERIALIZATION, LogIdType.SPECIFIC_ID, testInstance);
	}

	public TestLogDetails(File testkit, TestInstance testInstance, String[] areas) throws Exception {
		this.testkit = testkit;
		this.testInstance = testInstance;
		this.areas = areas;
		sectionLogMapDTO = new SectionLogMapDTO(testInstance);
		verifyCurrentTestExists(testkit, testInstance);
		testPlanFileMap = getTestPlans();
	}

//	public TestLogDetails(File testkit) throws Exception {
//		if (new File(testkit + File.separator + "tests").exists()) {
//			// this is the testkit
//			this.testkit = testkit;
//			areas = defaultAreas;
//		} else  {
//			// this is a test directory
//			this.testkit = null;
//			testPlanFileMap = getTestPlanFromDir(testkit);
//		}
//	}


	public String toString() { return "[TestLogDetails: testkit=" + testkit + " area=" + area +
		"<br />testnum=" + testInstance +
		"<br />sections= " + testPlansToString() +
		"<br />logs= " + sectionLogMapDTO.toString() +
		"]";
	}
	
	public SectionLogMapDTO getSectionLogMapDTO() {
		return sectionLogMapDTO;
	}
	
	public void addTestPlanLog(String section, LogFileContentDTO lf) throws XdsInternalException {
		if (sectionLogMapDTO == null)
			sectionLogMapDTO = new SectionLogMapDTO(testInstance);
		sectionLogMapDTO.put(section, lf);
	}
	
	public void resetLogs() {
		sectionLogMapDTO = new SectionLogMapDTO(testInstance);
	}
	
	public SectionLogMapDTO getTestPlanLogs() {
		return sectionLogMapDTO;
	}


	public void setLogRepository(LogRepository logRepository) {
		this.logRepository = logRepository;
	}

	String testPlansToString() {
		StringBuffer buf = new StringBuffer();

		if (testPlanFileMap == null) {
			buf.append("null");
			return buf.toString();
		}

		buf.append("[");
		boolean first = true;
		for (String section : testPlanFileMap.getSectionNames()) {
			File tp = testPlanFileMap.getPlanFile(section);
			if (!first)
				buf.append(", ");
			first=false;
			String parentdir = tp.getParent();
			int lastSlash = parentdir.lastIndexOf(File.separatorChar);
			if (lastSlash == -1)
				buf.append(parentdir);
			else
				buf.append(parentdir.substring(lastSlash+1));
		}
		buf.append("]");

		return buf.toString();
	}

	/**
	 * Remove from the path of a test, the testkit prefix. Useful for creating same
	 * directory structure in different place for output files.
	 * @param testPath
	 * @param testkit
	 * @return relative path
	 * @throws Exception
	 */
	static public String getLogicalPath(File testPath, File testkit) throws Exception {
        return getLogicalPath(testPath.toPath(), testkit.toPath()).toFile().toString();
	}

    static public Path getLogicalPath(Path testPath, Path testkit) throws Exception {
        if (!testPath.startsWith(testkit))
            throw new Exception("Path does not target contents of testkit");
        int testPathSize = testPath.getNameCount();
        int testkitSize = testkit.getNameCount();
        return testPath.subpath(testkitSize, testPathSize);
    }


	File simpleTestDir() throws Exception {
		File file = new File(getTestDir() + File.separator + testPlanFileName);
		if ( !file.exists())
			throw new Exception("Test plan " + file + " does not exist");
		return file;
	}

	private void verifyCurrentTestExists(File testkit, TestInstance testInstance)
			throws Exception {
		for (int i=0; i<areas.length; i++) {
			area = areas[i];
			if (exists())
				break;
		}
		if ( ! exists() ) {
			String msg = "TestSpec (testkit=" + testkit + " testId=" + testInstance + ", no " + testPlanFileName + " files found";
			logger.error(msg);
//			throw new Exception(msg);
		} else {
            logger.info("Using (testkit=" + testkit + " testId=" + testInstance + ")");
        }
	}

	static public void listTestKitContents(File testkit) throws Exception {
//		TestLogDetails ts = new TestLogDetails(testkit);
//
//		for (int i=0; i<defaultAreas.length; i++) {
//			ts.area = defaultAreas[i];
//			File sectionDir = ts.getSectionDir();
//			if ( !sectionDir.exists())
//				continue;
//			System.out.println("======================  " +  ts.area + "  ======================");
//
//			String[] files = sectionDir.list();
//			if (files == null)
//				continue;
//			for (int j=0; j<files.length; j++) {
//				if (files[j].startsWith("."))
//					continue;
//				File file = new File(sectionDir + File.separator + files[j]);
//				if ( !file.isDirectory())
//					continue;
//				ts.testInstance = new TestInstance(file.getName());
//				if (ts.isTestDir()) {
//					File readme = ts.getReadme();
//					String firstline = "";
//					if (readme.exists() && readme.isFile())
//						firstline = firstLineOfFile(readme);
//					System.out.println(ts.testInstance + "\t" + firstline.trim());
//				}
//			}
//		}
	}

	static public Map<String, String> getTestKitReadMe(File testkit) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
//		TestLogDetails ts = new TestLogDetails(testkit);
//
//		for (int i=0; i<defaultAreas.length; i++) {
//			ts.area = defaultAreas[i];
//			File sectionDir = ts.getSectionDir();
//			if ( !sectionDir.exists())
//				continue;
//
//			String[] files = sectionDir.list();
//			if (files == null)
//				continue;
//			for (int j=0; j<files.length; j++) {
//				if (files[j].startsWith("."))
//					continue;
//				File file = new File(sectionDir + File.separator + files[j]);
//				if ( !file.isDirectory())
//					continue;
//				ts.testInstance = new TestInstance(file.getName());
//				if (ts.isTestDir()) {
//					File readme = ts.getReadme();
//					String firstline = "";
//					if (readme.exists() && readme.isFile())
//						firstline = firstLineOfFile(readme);
//					map.put(ts.testInstance.getId(), firstline.trim());
//				}
//			}
//		}
		return map;
	}

	static String firstLineOfFile(File file) throws IOException {
		LinesOfFile lof = new LinesOfFile(file);
		return lof.next();
	}

	public File getReadme() {
		return new File(getTestDir() + File.separator + "readme.txt");
	}

	public String getReadmeFirstLine() throws IOException {
		return new LinesOfFile(getReadme()).next();
	}

	File getSectionDir() {
		return new File(testkit + File.separator + area);
	}

	public File getTestDir() {
		return new File(testkit + File.separator + area + File.separatorChar + testInstance.getId());
	}

	public boolean exists() {
		return getTestDir().isDirectory();
	}

	public SectionTestPlanFileMap getTestPlans() throws Exception {

		File index = new File(getTestDir() + File.separator + "index.idx");
		return getSectionTestPlanFileMap(index);

	}

	private SectionTestPlanFileMap getSectionTestPlanFileMap(File index) throws Exception {
		if (index.exists())
			return getTestPlansFromIndex(index);
		else
			return getTestPlanFromDir(getTestDir());
	}

	public File getIndexFile() {
		return new File(getTestDir() + File.separator + "index.idx");
	}

	SectionTestPlanFileMap getTestPlansFromIndex(File index) throws Exception {
		SectionTestPlanFileMap plans = new SectionTestPlanFileMap();
		File testdir = getTestDir();

		for (LinesOfFile lof = new LinesOfFile(index); lof.hasNext(); ) {
			String dir = lof.next().trim();
			if (dir.length() ==0)
				continue;
			File path = new File(testdir + File.separator + dir + File.separatorChar + testPlanFileName);
			if ( ! path.exists() )
				throw new Exception("TestSpec " + toString() + " references sub-directory " + dir + 
						" which does not exist or does not contain a " + testPlanFileName + " file");
			plans.put(dir, path);
		}
		return plans;
	}

	/**
	 * Read test logs based on index.idx file up to but not including section upToSection. If 
	 * upToSection is null, then read all.
	 * @param index - index.idx file
	 * @param upToSection - first section to not read
	 * @return list of Files representing log files
	 * @throws Exception
	 */
	List<File> getTestLogsForThisTest(File index, String upToSection) throws Exception {
		logger.info("GGetTestLogsForThisTest(" + index + ", " + upToSection + ")");
		List<File> logs = new ArrayList<File>();
		if (logRepository == null)
			return logs;
		File logdir = logRepository.logDir(testInstance);

		if (!index.exists())
			return logs;

		for (LinesOfFile lof = new LinesOfFile(index); lof.hasNext(); ) {
			String dir = lof.next().trim();
			if (dir.length() ==0)
				continue;
			logger.info("*******    upto " + upToSection + " this is " + dir  + "     ************");
			if (upToSection != null && dir.equals(upToSection))
				return logs;
			File path = new File(logdir + File.separator + dir + File.separatorChar + "log.xml");
			logger.info("  section " + dir  + " path = " + path);
			if ( ! path.exists() ) continue;
//				throw new Exception("TestSpec " + toString() + " references the section " + dir +
//						" - no log file exists ( file " + path.toString() + " does not exist");
			logs.add(path);
		}
		return logs;
	}
	
	public List<String> getSectionsFromTestDef(File testdir) throws IOException {
		List<String> sections = new ArrayList<String>();
		if (logRepository == null)
			return sections;
		File logdir = logRepository.logDir();

		File index = new File(testdir + File.separator + "index.idx");
		
		if (!index.exists())
			return sections;

		for (LinesOfFile lof = new LinesOfFile(index); lof.hasNext(); ) {
			String dir = lof.next().trim();
			if (dir.length() ==0)
				continue;
			File path = new File(logdir + File.separator + area + File.separator + testInstance + File.separatorChar + dir + File.separatorChar + "log.xml");
			sections.add(dir);
		}
		
		
		return sections;
	}
	
	public File getTestLog(TestInstance testInstance, String section) {
		File path;
		if (logRepository == null)
			return null;
		File logdir = logRepository.logDir();

		if (section != null && !section.equals("") && !section.equals("None"))
			path = new File(logdir + File.separator + ".." + File.separator + testInstance.getId() + File.separatorChar + section + File.separatorChar + "log.xml");
		else
			path = new File(logdir + File.separator + ".." + File.separator + testInstance.getId() + File.separatorChar  + "log.xml");
		return path;
	}

	public void selectSections(List<String> sectionNames) throws Exception {
		loadTestPlansFromSectionList(sectionNames);
		
		// will need to load all previous section logs for referencing
		List<File> previousLogFiles = getTestLogsForThisTest(getIndexFile(), sectionNames.get(0));
				
		for (File f : previousLogFiles) {
			LogFileContentDTO lf = new LogFileContentBuilder().build(f);
			String sectionName = lf.getSection();
			//System.out.println("\tLoading log for section " + sectionName);
			sectionLogMapDTO.put(sectionName, lf);
		}
		
	}
	
	public File getTestplanFile(String section) throws Exception {
		File testdir = getTestDir();
		File path;
		
		if (section == null) {
			path = new File(testdir + File.separator + testPlanFileName);
		} else {
			path = new File(testdir + File.separator + section + File.separator + testPlanFileName);
		}
		if ( ! path.exists() )
			throw new Exception("Test Section " + section + 
					" has been requested but does not exist or does not contain a " + testPlanFileName + " file (" + path + ")");
		return path;
	}

	void loadTestPlansFromSectionList(List<String> sections) throws Exception {
		testPlanFileMap = new SectionTestPlanFileMap();

		for (String sectionName : sections ) {
			File path = getTestplanFile(sectionName);
			testPlanFileMap.put(sectionName, path);
		}
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

	public boolean isTestDir() {
		if ( new File(getTestDir() + File.separator + "index.idx").exists())
			return true;
		if ( new File(getTestDir() + File.separator + testPlanFileName).exists())
			return true;
		return false;
	}

	public void validateTestPlans() throws XdsInternalException {
		for (File tp : testPlanFileMap.values()) {
			if ( !tp.exists())
				throw new XdsInternalException("TestPlan file " + tp + " does not exist");
		}
	}

	public TestInstance getTestInstance() {
		return testInstance;
	}

	public void setTestInstance(TestInstance testInstance) {
		this.testInstance = testInstance;
	}
}

