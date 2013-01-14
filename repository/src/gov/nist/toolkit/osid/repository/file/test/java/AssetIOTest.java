package gov.nist.toolkit.osid.repository.file.test.java;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import gov.nist.toolkit.osid.repository.file.Asset;
import gov.nist.toolkit.osid.repository.file.RepositoryMapper;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class AssetIOTest {

	@Test
	public void writeRead() {
		Asset a = new Asset();
		File file = new File("/Users/bill/tmp/RepositoryJunitTest/file1.json");
		file.getParentFile().mkdirs();
		try {
			RepositoryMapper.getInstance().save(file, a);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
			fail();
		} catch (JsonMappingException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
		
		try {
			Asset b = RepositoryMapper.getInstance().loadAsset(file);
		} catch (JsonParseException e) {
			e.printStackTrace();
			fail();
		} catch (JsonMappingException e) {
			e.printStackTrace();
			fail();
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		}
	}
}
