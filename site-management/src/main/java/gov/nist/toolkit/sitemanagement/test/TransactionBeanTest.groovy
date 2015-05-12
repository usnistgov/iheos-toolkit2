package gov.nist.toolkit.sitemanagement.test;

import static org.junit.Assert.*
import gov.nist.toolkit.actortransaction.client.ATFactory
import gov.nist.toolkit.sitemanagement.client.TransactionBean
import gov.nist.toolkit.sitemanagement.client.TransactionBean.RepositoryType

import org.junit.Test

class TransactionBeanTest {

	///////////////////////////////////////////////////////////
	@Test
	public void testSameObjectHasSameIndex() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertTrue b.hasSameIndex(b)
	}

	@Test
	public void testHasSameIndex() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionBean c = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertTrue b.hasSameIndex(c)
	}

	@Test
	public void testNotHasSameIndex() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionBean c = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			true,
			false)
		assertFalse b.hasSameIndex(c)
	}

	///////////////////////////////////////////////////////////
	@Test
	public void testEqualsTransactionBean() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionBean c = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		TransactionBean d = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			true,
			false)
		assertEquals b, b
		assertTrue b.equals(c)
		assertFalse b.equals(d)
	}

	///////////////////////////////////////////////////////////
	@Test
	public void testHasName() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertTrue b.hasName('Register')
	}

	///////////////////////////////////////////////////////////
	@Test
	public void testGetName() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertEquals(b.getName(), ATFactory.TransactionType.REGISTER.name)
		assertEquals(b.getName(), "Register")
	}

	///////////////////////////////////////////////////////////
	
	// This should test isNameUid AND repositorytype not NONE AND is transaction type correct
	// have to fix users first
	// Have to use String as first parameter since that is whre repUid is passed in
	// Should have to use special type RepUid which wraps repUid to avoid confusion
	// RepositoryType and name should be consistent
	@Test
	public void testIsRetrieve() {
		TransactionBean b = new TransactionBean('1.2.3',
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertTrue b.isRetrieve()
	}

	@Test
	public void testIsRetrieveButBadrepUid() {
		TransactionBean b = new TransactionBean('foo',
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertFalse b.isRetrieve()
	}

	@Test
	public void testIsNotRetrieve() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertFalse b.isRetrieve()
	}

	///////////////////////////////////////////////////////////
	@Test
	public void testIsNameUid() {
		TransactionBean b = new TransactionBean('1.2.3',
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertTrue b.isRetrieve()		
	}

	///////////////////////////////////////////////////////////
	@Test
	public void testGetTransactionType() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertTrue b.getTransactionType() == ATFactory.TransactionType.REGISTER
	}

	@Test
	///////////////////////////////////////////////////////////
	public void testIsType() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertTrue b.isType(ATFactory.TransactionType.REGISTER)
	}

	///////////////////////////////////////////////////////////
	@Test
	public void testHasEndpoint() {
		TransactionBean b = new TransactionBean(ATFactory.TransactionType.REGISTER,
			RepositoryType.NONE,
			'http://fooo:40/bar',
			false,
			false)
		assertTrue b.hasEndpoint()
	}

}
