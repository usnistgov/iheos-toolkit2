package gov.nist.toolkit.directsim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

public class DirectRegistrationManager {
	File externalCache; 

	public DirectRegistrationManager(File externalCache) {
		this.externalCache = externalCache;
	}

//	public DirectRegistrationManager(Session session) {
//		this.externalCache = session.externalCache();
//	}
	
	public void save(DirectRegistrationDataServer direct) throws Exception {
		File file = directPath(direct.directAddr);
		new File(file.getParent()).mkdirs();
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		System.out.println("Writing " + direct.getClass().getName() + " to " + file);
		
		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(direct);
		} catch (FileNotFoundException e) {
			throw new Exception("Cannot create file " + file.toString(), e);
		} catch (IOException e) {
			throw new Exception("Cannot create file " + file.toString(), e);
		}
		finally {
			try {
				out.close();
			} catch (Exception e) {}
		}

	}
	
	public DirectRegistrationDataServer load(File file) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream in = null;

		System.out.println("Reading DirectRegistrationDataServer " + " from " + file);

		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			DirectRegistrationDataServer c = new DirectRegistrationDataServer();
			return (DirectRegistrationDataServer) in.readObject();
		} 
//		catch (FileNotFoundException e) {
//			throw new Exception("Cannot find file " + file, e);
//		} 
		catch (InvalidClassException e) {
			System.out.println("InvalidClassException: " + e.getMessage());
			throw new Exception(e);
		} catch (OptionalDataException e) {
			System.out.println("OptionalDataException: " + e.getMessage());
			throw new Exception(e);
		} catch (StreamCorruptedException e) {
			System.out.println("StreamCorruptedException: " + e.getMessage());
			throw new Exception(e);
		} catch (IOException e) {
			throw new Exception("Cannot load file " + file, e);
		} catch (ClassNotFoundException e) {
			throw new Exception("Cannot create class DirectRegistrationData", e);
		} catch (RuntimeException e) {
			System.out.println("RuntimeException: " + e.getMessage());
			throw new Exception(e);
		} catch (Throwable e) {
			System.out.println("DirectRegistrationManager#load: " + file + ": " +  e.getMessage());
			throw new Exception("Exception: ", e);
		}
		
	}
	
	protected File externalCache() {
			return externalCache;
	}
	
	public String pathToDirectFile() {
		return externalCache() + File.separator + "direct" +
				File.separator + "direct" + File.separator;
	}

	public File directPath(String directAddr) {
		String filename = pathToDirectFile() + DirectEmailAddr.FILENAME(directAddr);
		return new File(filename);
	}
	
	public DirectRegistrationDataServer load(String directAddr) throws Exception {
		return load(directPath(directAddr));
	}
	
	

}
