package gov.nist.toolkit.sitemanagement.test;

import static org.junit.Assert.*
import gov.nist.toolkit.actortransaction.client.ATFactory
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.sitemanagement.client.TransactionCollection
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType

import org.junit.Test

class TransactionCollectionTest {

	///////////////////////////////////////////////////////////
	@Test
	public void testEqualsTransactionCollection() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionCollection tc = new TransactionCollection(false)
		tc.addTransaction(b)
		TransactionBean b1 = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionCollection tc1 = new TransactionCollection(false)
		tc1.addTransaction(b1)
		
		assertTrue tc.equals(tc1)
	}

	@Test
	public void testNotEqualsTransactionCollection() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			true,
			false)
		TransactionCollection tc = new TransactionCollection(false)
		tc.addTransaction(b)
		TransactionBean b1 = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionCollection tc1 = new TransactionCollection(false)
		tc1.addTransaction(b1)
		
		assertFalse tc.equals(tc1)
	}

	@Test
	public void testFixTlsEndpoints() {
		String origEndpoint = 'http://fooo:40/bar' 
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			origEndpoint,
			true,
			false)
		TransactionCollection tc = new TransactionCollection(false)
		tc.addTransaction(b)
		
		TransactionBean b2 = tc.find(ATFactory.TransactionType.REGISTER, true, false)
		assertTrue b2 != null
		String endpoint = b2.getEndpoint();
		assertTrue endpoint != null
		assertEquals origEndpoint, endpoint
		tc.fixTlsEndpoints()
		String fixedEndpoint = b2.getEndpoint()
		assertTrue fixedEndpoint != null
		assertFalse endpoint == fixedEndpoint
		assertTrue fixedEndpoint.startsWith('https')
		assertFalse endpoint.startsWith('https')
	}

	@Test
	public void testRemoveEmptyEndpoints() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveEmptyNames() {
		fail("Not yet implemented");
	}

	///////////////////////////////////////////////////////////
	@Test
	public void testContains() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionCollection tc = new TransactionCollection(false)
		tc.addTransaction(b)
		assertTrue tc.contains(b)
	}

	@Test
	public void testAddTransaction() {
		// testContains() handles this
	}

	@Test
	public void testSize() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionCollection tc = new TransactionCollection(false)
		tc.addTransaction(b)
		assertEquals tc.size(), 1
	}

	@Test
	public void testHasActor() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasTransaction() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindTransactionTypeBooleanBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindStringBooleanBoolean() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			true,
			false)
		TransactionCollection tc = new TransactionCollection(false)
		tc.addTransaction(b)
		
		TransactionBean b2 = tc.find(ATFactory.TransactionType.REGISTER, true, false)
		assertTrue b2 != null
	}

	@Test
	public void testFindAll() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTransactionTypeBooleanBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetStringBooleanBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		fail("Not yet implemented");
	}

}
