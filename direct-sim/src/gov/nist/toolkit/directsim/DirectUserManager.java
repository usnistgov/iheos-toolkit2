package gov.nist.toolkit.directsim;

import gov.nist.direct.config.DirectConfigManager;
import gov.nist.toolkit.directsim.client.ContactRegistrationData;
import gov.nist.toolkit.directsim.client.DirectRegistrationData;
import gov.nist.toolkit.installation.Installation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;


public class DirectUserManager extends DirectManagers {
	DirectConfigManager directConfig = new DirectConfigManager(Installation.installation().externalCache());
	
	public DirectUserManager() {
	}

	
	// Used for finding contactAddresses
	public File contactPath(File contact) {
		return new File( directConfig.pathToContactFile() + 
				DirectEmailAddr.FILENAME(contact.toString()));
	}
	
	public File contactPath(ContactRegistrationData contact) {
		String filename = directConfig.pathToContactFile() + DirectEmailAddr.FILENAME(contact.contactAddr);
		return new File(filename);
	}
	
	public File contactPath(String contactName) {
		return contactPath(new File(contactName));
	}
	
	// Used for finding directAddresses
	public File directPath(File directFileName) {
		File f = new File(new DirectRegistrationManager(Installation.installation().externalCache()).pathToDirectFile() + 
				DirectEmailAddr.FILENAME(directFileName.toString()));
		return f;
	}

	// contactName is same as contactEmail
	public ContactRegistrationData load(String contactName) throws Exception {
		File contact = contactPath(contactName);
		if (!contact.exists())
			return null;
		return load(contact);
	}
	
	public void saveCertFromUpload(ContactRegistrationData contact, String directAddr, byte[] cert) throws Exception {
		contact.add(directAddr, cert);
		save(contact);
	}
	
	public void contactRegistration(ContactRegistrationData contact) throws Exception {
		save(contact);
	}
	
	public ContactRegistrationData contactRegistrationData(String contactEmail) throws Exception {
		return load(contactEmail);
	}

	public void save(ContactRegistrationData contact) throws Exception {
		for (String direct : contact.directToCertMap.keySet()) {
			if (direct.equals(""))
				continue;
			DirectRegistrationData d = new DirectRegistrationData(direct, contact.contactAddr);
			DirectRegistrationDataServer ds = DirectRegistration.toServer(d);
			d.contactAddr = contact.contactAddr;
			save(ds, new DirectRegistrationManager(Installation.installation().externalCache()).directPath(direct));
		}
		save(contact, contactPath(contact));
	}
	
	public ContactRegistrationData deleteDirect(ContactRegistrationData contact, DirectRegistrationData direct) throws Exception {
		contact.directToCertMap.remove(direct.directAddr);
		save(contact);
		File directFile = new DirectRegistrationManager(Installation.installation().externalCache()).directPath(direct.directAddr);
		if (directFile.exists()) {
			directFile.delete();
			return contact;
		}
		return contact;
	}
			
	void save(Serializable data, File file) throws Exception {
		new File(file.getParent()).mkdirs();
		FileOutputStream fos = null;
		ObjectOutputStream out = null;

		System.out.println("Writing " + data.getClass().getName() + " to " + file);

		try {
			fos = new FileOutputStream(file);
			out = new ObjectOutputStream(fos);
			out.writeObject(data);
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
	
	public ContactRegistrationData load(File file) throws Exception {
		FileInputStream fis = null;
		ObjectInputStream in = null;

		System.out.println("Reading ContactRegistrationData "  + " from " + file);

		try {
			fis = new FileInputStream(file);
			in = new ObjectInputStream(fis);
			ContactRegistrationData c = new ContactRegistrationData();
			c = (ContactRegistrationData) in.readObject();
			return c;
		} catch (FileNotFoundException e) {
			throw new Exception("Cannot find file " + file, e);
		} catch (IOException e) {
			throw new Exception("Cannot load file " + file, e);
		} catch (ClassNotFoundException e) {
			throw new Exception("Cannot create class ContentRegistrationData", e);
		} finally {
			if (in != null)
				in.close();
			if (fis != null)
				fis.close();
		}
	}

	public boolean directUserExists(String directEmailAddr) {
		String filename = DirectEmailAddr.FILENAME(directEmailAddr);
		return directPath(new File(filename)).exists();
	}
	
}
