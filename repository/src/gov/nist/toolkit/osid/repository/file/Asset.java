package gov.nist.toolkit.osid.repository.file;

import gov.nist.toolkit.osid.repository.IType;
import gov.nist.toolkit.osid.shared.Id;
import gov.nist.toolkit.osid.shared.NotImplemented;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class Asset {
	Id id;
	IType type;
	String description = "";
	String displayName;
	long effectiveDate;    // milliseconds since the epoch - see java.util.Date
	long expirationDate;
	Id repository = RepositoryManager.NOREPID;
	Serializable content = null;

	public Asset() {
		id = new Id("my_id");
		type = new Type(new Id("my_type"));
		displayName = type + "|" + id + "|of:" + getRepository().getId();
		effectiveDate = new Date().getTime();
		expirationDate = 0;
	}
	
	public void addAsset(Id assetId) {
		throw new NotImplemented();
	}

	@JsonIgnore
	public AssetIterator getAssets() {
		return null;
	}

	public AssetIterator getAssetsByType(Type assetType) {
		throw new NotImplemented();
	}

	public IType getType() {
		return type;
	}

	public Serializable getContent() {
		return content;
	}

	public String getDescription() {
		return description;
	}

	public String getDisplayName() {
		return displayName;
	}

	public long getEffectiveDate() {
		return effectiveDate;
	}

	public long getExpirationDate() {
		return expirationDate;
	}

	public Id getId() {
		return id;
	}

	public Id getRepository()  {
		return repository;
	}

	public void removeAsset(Id assetId, boolean includeChildren) {
		throw new NotImplemented();
	}

	public void updateContent(Serializable content) {
		throw new NotImplemented();
	}

	public void updateDescription(String description) {
		throw new NotImplemented();
	}

	public void updateDisplayName(String displayName) {
		throw new NotImplemented();
	}

	public void updateEffectiveDate(long effectiveDate) {
		throw new NotImplemented();
	}

	public void updateExpirationDate(long expirationDate) {
		throw new NotImplemented();
	}
	
	public byte[] asBytes() throws JsonProcessingException {
		return RepositoryMapper.getInstance().get().writeValueAsBytes(this);
	}

	public static Asset LOAD(byte[] bytes) throws JsonParseException, JsonMappingException, IOException {
		return RepositoryMapper.getInstance().get().readValue(bytes, Asset.class);
	}
}
