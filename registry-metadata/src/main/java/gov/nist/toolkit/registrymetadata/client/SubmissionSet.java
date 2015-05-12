package gov.nist.toolkit.registrymetadata.client;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SubmissionSet extends RegistryObject implements IsSerializable, Serializable {
	
	private static final long serialVersionUID = 1L;
	public String submissionTime;
	public String submissionTimeX;
	public String submissionTimeDoc;
	
	public String sourceId;
	public String sourceIdX;
	public String sourceIdDoc;
	
	public List<String> contentTypeCode;
	public List<String> contentTypeCodeX;
	public List<String> contentTypeCodeDoc;

	public List<Author> authors;	
	public List<String> authorsX;
	public List<Author> authorsDoc;	
	
	public List<String> intendedRecipients;
	public String intendedRecipientsX;
	public List<String> intendedRecipientsDoc;
	
	public String displayName() {
		String name = title;
		if (name == null || name.equals(""))
			name = id;

		return "SubmissionSet(" + name + ")";
	}
	
}
