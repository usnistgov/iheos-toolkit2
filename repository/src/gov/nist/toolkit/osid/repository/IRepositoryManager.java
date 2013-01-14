package gov.nist.toolkit.osid.repository;

import gov.nist.toolkit.osid.repository.file.Asset;
import gov.nist.toolkit.osid.repository.file.AssetIterator;
import gov.nist.toolkit.osid.repository.file.RepositoryIterator;
import gov.nist.toolkit.osid.repository.file.Type;
import gov.nist.toolkit.osid.repository.file.TypeIterator;
import gov.nist.toolkit.osid.shared.Id;
import gov.nist.toolkit.osid.shared.LongValueIterator;
import gov.nist.toolkit.osid.shared.Properties;

import java.io.Serializable;

public interface IRepositoryManager {

	public  Id copyAsset(IRepository repository, Id assetId);

	public  IRepository createRepository(String displayName,
			String description, Type repositoryType);

	public  void deleteRepository(Id repositoryId);

	public  Asset getAsset(Id assetId);

	public  Asset getAssetByDate(Id assetId, long date);

	public  LongValueIterator getAssetDates(Id assetId);

	public  AssetIterator getAssetsBySearch(IRepository[] repositories,
			Serializable searchCriteria, IType searchType,
			Properties searchProperties);

	public  RepositoryIterator getRepositories();

	public  RepositoryIterator getRepositoresByType(IType repositoryType);

	public  IRepository getRepository(Id repositoryId);

	public  TypeIterator getRepositoryTypes();

}