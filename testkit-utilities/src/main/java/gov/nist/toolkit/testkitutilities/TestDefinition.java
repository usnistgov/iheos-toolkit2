package gov.nist.toolkit.testkitutilities;

import gov.nist.toolkit.utilities.io.Io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestDefinition {
	File testDir;

	public TestDefinition(File testDir) {
		this.testDir = testDir;
	}
	
	public String getReadme() throws IOException {
		return Io.stringFromFile(getFile("readme.txt"));
	}
	
	/**
	 * Get test description from readme.txt file.  Description
	 * is the first line of the file.
	 * @return
	 * @throws IOException
	 */
	public String getTestDescription() throws IOException {
		String readme = getReadme();
		String[] parts = readme.split("\n");
		if (parts.length == 0)
			return "";
		return parts[0].trim();
	}
	/**
	 * Get list of sections defined by the test.
	 * @return list of section names
	 * @throws IOException
	 */
	public List<String> getSectionIndex() throws IOException {
		List<String> names = new ArrayList<String>();
		
		String[] parts = Io.stringFromFile(getFile("index.idx")).split("\n");
		
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
	
	public File getSectionDir(String sectionName) {
		return new File(testDir.toString() + File.separator + sectionName);
	}
		
	File getFile(String filename) {
		return new File(testDir.toString()+ File.separator + filename);
	}
	
	
}
