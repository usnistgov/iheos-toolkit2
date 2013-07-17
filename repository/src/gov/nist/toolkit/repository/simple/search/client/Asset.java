package gov.nist.toolkit.repository.simple.search.client;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Asset implements IsSerializable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -46123676112710466L;
	
	private String repId;
	private String assetId;
	private String type;
	private String displayName;
	private String description;
	public String getRepId() {
		return repId;
	}
	public void setRepId(String repId) {
		this.repId = repId;
	}
	public String getAssetId() {
		return assetId;
	}
	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
