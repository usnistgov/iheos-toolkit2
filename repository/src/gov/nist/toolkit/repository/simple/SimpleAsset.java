package gov.nist.toolkit.repository.simple;

import java.io.Serializable;

import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.Type;

public class SimpleAsset implements Asset {
	Type type;
	String displayName;
	long effectiveDate;
	long expirationDate;
	String description;
	Id id;
	String classNameForSerializable;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5388152847325921264L;

	@Override
	public void updateDisplayName(String displayName)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void updateEffectiveDate(long effectiveDate)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void updateExpirationDate(long expirationDate)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public String getDisplayName() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public String getDescription() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Id getId() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Type getAssetType() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public long getEffectiveDate() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public long getExpirationDate() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void updateDescription(String description)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Id getRepository() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public Serializable getContent() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void updateContent(Serializable content) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void addAsset(Id assetId) throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public void removeAsset(Id assetId, boolean includeChildren)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public AssetIterator getAssets() throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

	@Override
	public AssetIterator getAssetsByType(Type assetType)
			throws RepositoryException {
		throw new RepositoryException(RepositoryException.UNIMPLEMENTED);
	}

}
