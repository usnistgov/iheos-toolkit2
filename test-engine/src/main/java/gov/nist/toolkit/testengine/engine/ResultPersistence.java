package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.results.client.Result;
import gov.nist.toolkit.results.client.TestInstance;
import gov.nist.toolkit.xdsexception.client.XdsException;

import java.io.*;

public class ResultPersistence {

	public void write(Result result, String testSession) throws IOException, XdsException {

		if (result.testInstance == null || result.testInstance.isEmpty())
			throw new XdsException("No test name specified in Result - cannot persist", null);

		String outFile = getFilePath(result.testInstance, testSession, true);
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		fos = new FileOutputStream(outFile.toString());
		out = new ObjectOutputStream(fos);
		out.writeObject(result);
		out.close();

	}

    public void delete(TestInstance testInstance, String testSession) throws IOException, XdsException {
        File file = new File(getFilePath(testInstance, testSession, true));
        if (file.exists()) file.delete();
    }

	public Result read(TestInstance testInstance, String testSession) throws XdsException  {
		try {
			FileInputStream fis = new FileInputStream(getFilePath(testInstance, testSession, false));
			ObjectInputStream in = new ObjectInputStream(fis);
			Result result = (Result) in.readObject();
			in.close();
			return result;
		} 
		catch (IOException e) {
			throw new XdsException(e.getMessage(), null, e);
		} catch (ClassNotFoundException e) {
			throw new XdsException(e.getMessage(), null, e);
		}
	}

	String getFilePath(TestInstance testInstance,String testSession, boolean write) throws IOException {
		File dir = new File(
				Installation.instance().propertyServiceManager().getTestLogCache().toString() + File.separator +
				testSession + File.separator + 
				"Results");
		if (write)
			dir.mkdirs();

		return dir.toString() + File.separator + testInstance.toString().replace(":","") + ".ser";

	}
}
