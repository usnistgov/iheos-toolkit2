package gov.nist.toolkit.registrymetadata.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

/**
 * This class represents either an Object uid (DocumentEntry, Folder, SubmissionSet)
 * or a repositoryUniqueId. Attribute home is only useful if Cross-Community is
 * involved.  Attribute repositoryUniqueId is only useful if used as a
 * DocumentEntry.uniqueId.
 * @author bill
 *
 */
public class Uid  implements Serializable, IsSerializable {
	public String uid;
	public String home;
	public String repositoryUniqueId;
	
	public Uid() {
		
	}
	
	public Uid(AnyId aid) {
		uid = aid.id;
		home = aid.home;
		repositoryUniqueId = aid.repositoryUniqueId;
	}
	
	public String toString() {
		return "[uid=" + uid + 
		" repositoryUniqueId=" + repositoryUniqueId +
		" home=" + home;
	}
	
	public Uid(String uid, String home) {
		this.uid = uid;
		this.home = home;
	}
	
	public Uid(String uid) {
		this.uid = uid;
	}
	
	public Uid(DocumentEntry de) {
		this.uid = de.uniqueId;
		this.home = de.home;
		this.repositoryUniqueId = de.repositoryUniqueId;
	}
	
	public String displayName() {
		return uid;
	}

}
