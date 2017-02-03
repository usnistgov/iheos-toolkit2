package gov.nist.toolkit.registrymetadata.client;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class Document implements IsSerializable, Serializable {

	private static final long serialVersionUID = 1L;
	public String uid;
	public String homeCommunityId;
	public String repositoryUniqueId;
	public String mimeType;
	public String cacheURL;
	public String newUid;
	public String newRepositoryUniqueId;
}
