package gov.nist.toolkit.repository.simple.search.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.servlet.ServletContext;

import gov.nist.toolkit.common.datatypes.Hl7Date;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.repository.api.Asset;
import gov.nist.toolkit.repository.api.AssetIterator;
import gov.nist.toolkit.repository.api.Id;
import gov.nist.toolkit.repository.api.Repository;
import gov.nist.toolkit.repository.api.RepositoryException;
import gov.nist.toolkit.repository.api.RepositoryFactory;
import gov.nist.toolkit.repository.api.Type;
import gov.nist.toolkit.repository.api.TypeIterator;
import gov.nist.toolkit.repository.simple.Configuration;
import gov.nist.toolkit.repository.simple.IdFactory;
import gov.nist.toolkit.repository.simple.SimpleId;
import gov.nist.toolkit.repository.simple.SimpleType;
import gov.nist.toolkit.repository.simple.SimpleTypeIterator;
import gov.nist.toolkit.repository.simple.index.test.MockServletContext;
import gov.nist.toolkit.repository.simple.search.SearchResultIterator;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm;
import gov.nist.toolkit.repository.simple.search.client.SearchCriteria.Criteria;
import gov.nist.toolkit.repository.simple.search.client.SearchTerm.Operator;

import org.apache.commons.io.FileUtils;
import org.junit.BeforeClass;
import org.junit.Test;

public class SearchTest {

	/*
	 * Important: The following developer's system path variables need to verified manually before running the test.
	 * 
	 */
	
	static String RootPath = "/e/artrep_test_resources/"; 		// Root Path or the Test resources folder
	public static String RepositoriesPath;  					// Repositories folder
	static String InstallationPath = RootPath+"installation";	// Path containing the WEB-INF folder (for External_Cache)
	
	public static File RootOfAllRepositories; 
	static Installation inst = null;
	
	@BeforeClass
	static public void initialize() throws RepositoryException {
		
		
		// The MockServletContext is used for testing purposes only
		
		ServletContext sc = MockServletContext.getServletContext(InstallationPath); 
		
		Installation.installation(sc);
		
		String externalCache = Installation.installation().propertyServiceManager()
									.getToolkitProperties().get("External_Cache");
		System.out.println(externalCache);
		Installation.installation().setExternalCache(new File(sc.getRealPath(externalCache)));
		inst = Installation.installation();
		
		RepositoriesPath = externalCache + "/repositories";
		RootOfAllRepositories = new File(RepositoriesPath);
		
		new Configuration(RootOfAllRepositories);
	}


	
	@Test
	public void genTest() {
		Properties p = new Properties();
		p.setProperty("testkey", "value");
		p.setProperty("testkey1", "value");
		System.out.println(p.toString());
	}

	@Test
	public void multipleBaseCriteriaTest() {		 		

		/* All Search criteria constructions are based on bottom-up approach
		 * Idea is to build parts and assemble them together
		 * Example: 
		 build sub criteria A
		 build sub criteria B
		 build final criteria based on A and B
		 */		
				
		
		SearchCriteria subCriteriaA = new SearchCriteria(Criteria.AND);
		
		subCriteriaA.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"101","102"}));
		subCriteriaA.append(new SearchTerm("documentType",Operator.EQUALTO,"C32"));

		subCriteriaA.append(new SearchTerm("status",Operator.EQUALTOANY,new String[]{"Pending","Unknown"}));		
					
		subCriteriaA.append(new SearchTerm("startDate",Operator.GREATERTHAN,"20070101"));
		subCriteriaA.append(new SearchTerm("startDate",Operator.LESSTHANOREQUALTO,"20080101"));
		
		
		SearchCriteria subCriteriaB = new SearchCriteria(Criteria.AND);
		subCriteriaB.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"103","104","105"}));
		subCriteriaB.append(new SearchTerm("documentType",Operator.EQUALTO,"C64"));
		
		SearchCriteria criteria = new SearchCriteria(Criteria.OR);
		criteria.append(subCriteriaA);
		criteria.append(subCriteriaB);
		
		
		
		
		System.out.println(criteria.toString());
		
	}
	
	@Test
	public void criteriaTestL1() {
		
		SearchCriteria subCriteriaA = new SearchCriteria(Criteria.AND);
		
		subCriteriaA.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"101","102"}));
		subCriteriaA.append(new SearchTerm("documentType",Operator.EQUALTO,"C32"));

		SearchCriteria subCriteriaAba = new SearchCriteria(Criteria.OR);		
			subCriteriaAba.append(new SearchTerm("status",Operator.EQUALTOANY,new String[]{"Pending","Unknown"}));		
			subCriteriaAba.append(new SearchTerm("status",Operator.EQUALTO,"Active"));
			
			subCriteriaA.append(subCriteriaAba);
		
		subCriteriaA.append(new SearchTerm("startDate",Operator.GREATERTHAN,"20070101"));
		subCriteriaA.append(new SearchTerm("startDate",Operator.LESSTHANOREQUALTO,"20080101"));
		

		
		
		SearchCriteria subCriteriaB = new SearchCriteria(Criteria.AND);
		subCriteriaB.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"103","104","105"}));
		subCriteriaB.append(new SearchTerm("documentType",Operator.EQUALTO,"C64"));
		
		SearchCriteria criteria = new SearchCriteria(Criteria.OR);
		criteria.append(subCriteriaA);
		criteria.append(subCriteriaB);
		
		
		
		
		System.out.println(criteria.toString());
		
	}
	
	@Test
	public void billTestL1() {
		
		SearchCriteria subCriteriaA = new SearchCriteria(Criteria.AND);
		
		subCriteriaA.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"pid^hcid"}));
		subCriteriaA.append(new SearchTerm("classCode",Operator.EQUALTOANY,new String[]{"33401-1","y","z"}));
		subCriteriaA.append(new SearchTerm("type",Operator.EQUALTO,"DocumentEntry"));
		subCriteriaA.append(new SearchTerm("author.name",Operator.LIKE,"Julien%"));
		
		SearchCriteria subCriteriaAb = new SearchCriteria(Criteria.OR);					
			SearchCriteria subCriteriaAba = new SearchCriteria(Criteria.AND);
				subCriteriaAba.append(new SearchTerm("codeX.codeSystem",Operator.EQUALTO,"3M"));
				subCriteriaAba.append(new SearchTerm("code",Operator.EQUALTO,"blue"));
			SearchCriteria subCriteriaACa = new SearchCriteria(Criteria.AND);
				subCriteriaACa.append(new SearchTerm("codeX.codeSystem",Operator.EQUALTO,"WHO"));
				subCriteriaACa.append(new SearchTerm("code",Operator.EQUALTO,"red"));
		
		subCriteriaAb.append(subCriteriaAba);
		subCriteriaAb.append(subCriteriaACa);
		
				
		SearchCriteria criteria = new SearchCriteria(Criteria.AND);
		criteria.append(subCriteriaA);
		criteria.append(subCriteriaAb);
		
		/*
		 *  
		 *  o
		 *  |
		 *  o--+----o
		 *     |       codeSystem =  3M 
		 *     |       ...
		 *     -or-
		 *     |
		 *     +---o  codeSystem = "WHO"
		 *     
		 *     
		 *     o = criteria
		 *     ...  = search term (leaf nodes) 
		 * 
		 */
		
		
		System.out.println(criteria.toString());
		
	}
	
	
	@Test
	public void criteriaTestL2() {
		
		SearchCriteria subCriteriaA = new SearchCriteria(Criteria.AND);
		
		subCriteriaA.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"101","102"}));
		subCriteriaA.append(new SearchTerm("documentType",Operator.EQUALTO,"C32"));

		SearchCriteria subCriteriaAba = new SearchCriteria(Criteria.OR);		
			subCriteriaAba.append(new SearchTerm("status",Operator.EQUALTOANY,new String[]{"Pending","Unknown"}));		
			subCriteriaAba.append(new SearchTerm("status",Operator.EQUALTO,"Active"));
			
			SearchCriteria subCriteriaAbaa = new SearchCriteria(Criteria.AND);			
				subCriteriaAbaa.append(new SearchTerm("indFlag",Operator.EQUALTO,"Y"));
			subCriteriaAba.append(subCriteriaAbaa);
			
			subCriteriaA.append(subCriteriaAba);
		
		subCriteriaA.append(new SearchTerm("startDate",Operator.GREATERTHAN,"20070101"));
		subCriteriaA.append(new SearchTerm("startDate",Operator.LESSTHANOREQUALTO,"20080101"));
		

		
		
		SearchCriteria subCriteriaB = new SearchCriteria(Criteria.AND);
		subCriteriaB.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"103","104","105"}));
		subCriteriaB.append(new SearchTerm("documentType",Operator.EQUALTO,"C64"));
		
		SearchCriteria criteria = new SearchCriteria(Criteria.OR);
		criteria.append(subCriteriaA);
		criteria.append(subCriteriaB);
		
		
		
		
		System.out.println(criteria.toString());
		
	}
	
	@Test
	public void criteriaTestL3() {
		
		SearchCriteria subCriteriaA = new SearchCriteria(Criteria.AND);
		
		subCriteriaA.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"101","102"}));
		subCriteriaA.append(new SearchTerm("documentType",Operator.EQUALTO,"C32"));

		SearchCriteria subCriteriaAba = new SearchCriteria(Criteria.OR);		
			subCriteriaAba.append(new SearchTerm("status",Operator.EQUALTOANY,new String[]{"Pending","Unknown"}));		
			subCriteriaAba.append(new SearchTerm("status",Operator.EQUALTO,"Active"));
			
			SearchCriteria subCriteriaAbaa = new SearchCriteria(Criteria.AND);			
				subCriteriaAbaa.append(new SearchTerm("indFlag",Operator.EQUALTO,"Y"));
				subCriteriaAba.append(subCriteriaAbaa);
				
				SearchCriteria subCriteriaAbaaa = new SearchCriteria(Criteria.OR);			
				subCriteriaAbaaa.append(new SearchTerm("afflCode",Operator.EQUALTO,"NPO"));
				subCriteriaAbaa.append(subCriteriaAbaaa);
				
				
			subCriteriaA.append(subCriteriaAba);
		
		subCriteriaA.append(new SearchTerm("startDate",Operator.GREATERTHAN,"20070101"));
		subCriteriaA.append(new SearchTerm("startDate",Operator.LESSTHANOREQUALTO,"20080101"));
		

		
		
		SearchCriteria subCriteriaB = new SearchCriteria(Criteria.AND);
		subCriteriaB.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"103","104","105"}));
		subCriteriaB.append(new SearchTerm("documentType",Operator.EQUALTO,"C64"));
		
		SearchCriteria criteria = new SearchCriteria(Criteria.OR);
		criteria.append(subCriteriaA);
		criteria.append(subCriteriaB);
		
		
		
		
		System.out.println(criteria.toString());
		
	}
	
	
	@Test
	public void searchResultIteratorTest() {				
		
		SearchCriteria criteria = new SearchCriteria(Criteria.OR);		
//		criteria.append(new SearchTerm("displayName",Operator.EQUALTOANY,new String[]{"My Site","Bogus site"}));		
//		criteria.append(new SearchTerm("patientId",Operator.EQUALTOANY,new String[]{"TestPatient1","TestPatient2"}));
//		criteria.append(new SearchTerm("startDate",Operator.GREATERTHAN,"20050101"));
		// criteria.append(new SearchTerm("createdDate",Operator.GREATERTHAN,"20050101"));
		criteria.append(new SearchTerm("expirationDate",Operator.LESSTHAN,"2013080101"));
		
		
		try {
			RepositoryFactory fact = new RepositoryFactory();		
			Repository repos1 = fact.getRepository(new SimpleId("46182094-bc5a-4fb6-a26d-a9c38a00a333")); // Local repository ids
			Repository repos2 = fact.getRepository(new SimpleId("8e4465aa-17ce-490f-9377-ff85b4d48daa"));
			Repository repos3 = fact.getRepository(new SimpleId("993dc7b8-867b-44e8-9859-dfd643734876"));
			Repository repos4 = fact.getRepository(new SimpleId("9678578a-593e-489b-a747-ab87a63f00cf"));
			
			AssetIterator iter = new SearchResultIterator(new Repository[]{repos1,repos2,repos3,repos4}, criteria );
			
			while (iter.hasNextAsset()) {
				Asset a = iter.nextAsset();
				System.out.println("found asset: " +  a.getId().toString() + ", of type: "+ a.getAssetType().getKeyword() +", in repos:" + a.getRepository().getIdString() );
				System.out.println("life: " + a.getAssetType().getLifetime());
			}
			
		} catch (Exception e) {
			fail("Test failed " + e.toString());
			e.printStackTrace();
		}
	}

	// @Test
	public void fileTest() {
		String s = "helo this is a simple string";
		try {
			FileUtils.writeByteArrayToFile(new File(RepositoriesPath + "/data/test/mytestfile.txt" ), s.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test 
	public void hl7DateTest() {
		try {
			Type t = new SimpleType("simple"); 
			if (t != null) {			
				TypeIterator it;
				try {
					it = new SimpleTypeIterator(t);
					if (it.hasNextType()) {				
						Type assetType = it.nextType();
						String lifetime = assetType.getLifetime();
						if (lifetime!=null) {
								Integer days = Integer.parseInt(lifetime.substring(0,lifetime.indexOf(" days")));
								if (days!=null)
									System.out.println("lf: " + days);
								SimpleDateFormat sdf = new SimpleDateFormat(Hl7Date.parseFmt);
								Date dt=sdf.parse("20020911091200");
								
								Calendar c = sdf.getCalendar();
								System.out.println("cal str:"+c.getTime().toString());
								
								c.add(Calendar.DATE, 65);
								Date expr = c.getTime();
								

								System.out.println("expr: " + sdf.format(expr));

								

						}
						
					}
				} catch (RepositoryException e) {
					
				}
			}
			
		} catch (Exception e) {
			
		}

	}
}

	
