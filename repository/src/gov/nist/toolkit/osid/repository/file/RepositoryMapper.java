package gov.nist.toolkit.osid.repository.file;

import gov.nist.toolkit.osid.shared.Id;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RepositoryMapper {
	static ObjectMapper mapper;
	static RepositoryMapper instance;
	static File REPOSITORIES_HOME;
	static RepositoryManager REPOSITORY_MANAGER;
	
	static {
		instance = new RepositoryMapper();
		mapper = new ObjectMapper();
		REPOSITORIES_HOME = new File("/Users/bill/tmp/Repositories");
		REPOSITORIES_HOME.mkdirs();
		REPOSITORY_MANAGER = new RepositoryManager(REPOSITORIES_HOME);
	}
	
	
	private RepositoryMapper() { }
	
	public static RepositoryMapper getInstance() { return instance; }
	
	public ObjectMapper get() { 
		return RepositoryMapper.mapper;
	}

	public void save(File saveFile, Asset asset) throws JsonGenerationException, JsonMappingException, IOException {
		mapper.writeValue(saveFile, asset);
	}
	
	public Asset loadAsset(File loadFile) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(loadFile, Asset.class);
	}
	
	public RepositoryManager getDefaultRepositoryManager() {
		return REPOSITORY_MANAGER;
	}
	
	public Type getDefaultRepositoryType() {
		return new Type(new Id("rep"));
	}
	
}
