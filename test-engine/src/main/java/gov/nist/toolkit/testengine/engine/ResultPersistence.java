package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.io.*;
import java.util.List;

public class ResultPersistence {

	public void write(Result result, String testSession) throws IOException, XdsException {

		if (result.testInstance == null || result.testInstance.isEmpty())
			throw new XdsException("No test name specified in Result - cannot persist", null);

		File outFile = getFilePath(result.testInstance, testSession, null, true);
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		fos = new FileOutputStream(outFile);
		out = new ObjectOutputStream(fos);
		out.writeObject(result);
		out.close();

	}

    public void delete(TestInstance testInstance, String testSession, List<String> sectionNames) throws IOException, XdsException {
        File file = getFilePath(testInstance, testSession, null, true); // if test run as single entity
        if (file.exists()) file.delete();
		// if test run as individual sections
		for (String sectionName : sectionNames) {
			file = getFilePath(testInstance, testSession, sectionName, true); // if test run as single entity
			if (file.exists()) file.delete();
		}
    }

	public Result read(TestInstance testInstance, List<String> sectionNames, String testSession) throws XdsException  {
		try {
			File resultFile = getFilePath(testInstance, testSession, null, false);
			if (resultFile.exists()) {
				// This form will only exist if test was run as a whole.  If sections were run individually
				// then there will be a file per section.
				FileInputStream fis = new FileInputStream(resultFile);
				ObjectInputStream in = new ObjectInputStream(fis);
				Result result = (Result) in.readObject();
				in.close();
				return result;
			} else {
				// Sections run individually.  Pick them up from their individual file names
				Result result = null;
				for (String sectionName : sectionNames) {
					resultFile = getFilePath(testInstance, testSession, sectionName, false);
					if (resultFile.exists()) {
						if (result == null) {
							FileInputStream fis = new FileInputStream(resultFile);
							ObjectInputStream in = new ObjectInputStream(fis);
							result = (Result) in.readObject();
							in.close();
						} else {
							FileInputStream fis = new FileInputStream(resultFile);
							ObjectInputStream in = new ObjectInputStream(fis);
							Result sectionResult = (Result) in.readObject();
							in.close();
							result.append(sectionResult);
						}
					}
				}
				return result;
			}
		}
		catch (IOException e) {
			throw new XdsException(e.getMessage(), null, e);
		} catch (ClassNotFoundException e) {
			throw new XdsException(e.getMessage(), null, e);
		}
	}

	private File getFilePath(TestInstance testInstance,String testSession, String sectionName, boolean write) throws IOException {
		File dir = new File(
				Installation.instance().propertyServiceManager().getTestLogCache().toString() + File.separator +
				testSession + File.separator + 
				"Results");
		if (write)
			dir.mkdirs();

		if (sectionName == null)
			return new File(dir.toString() + File.separator + testInstance.toString().replace(":","") + ".ser");
		return new File(dir.toString() + File.separator + testInstance.toString().replace(":","") + sectionName + ".ser");

	}
}
