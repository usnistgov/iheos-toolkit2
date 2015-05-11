package gov.nist.toolkit.dns.test

import org.junit.Test
import org.xbill.DNS.*
import static org.junit.Assert.assertFalse

class DnsTest {

	@Test
	public void mxHitTestingTest() {
		String domainPart = "hit-testing.nist.gov"
		Lookup dnsLookup = new Lookup(domainPart, Type.MX)
		Record[] records = dnsLookup.run()
		println "size is ${records.size()}"
		records.eachWithIndex { rec, i -> println rec }
		records.eachWithIndex { rec, i -> 
			String[] d = rec.rdataToString().split(" ")
			d.eachWithIndex { part, j -> println part }
		}
	}

	@Test
	public void mxTHTest() {
		String domainPart = "ttt.transparenthealth.org"
		Lookup dnsLookup = new Lookup(domainPart, Type.MX)
		Record[] records = dnsLookup.run()
		assertFalse records == null
		println "size is ${records.size()}"
		records.eachWithIndex { rec, i -> println rec }
		records.eachWithIndex { rec, i ->
			String[] d = rec.rdataToString().split(" ")
			d.eachWithIndex { part, j -> println part }
		}
	}

	@Test
	public void certTHTest() {
		String domainPart = "ttt.transparenthealth.org"
		Lookup dnsLookup = new Lookup(domainPart, Type.CERT)
		Record[] records = dnsLookup.run()
		assertFalse records == null
		println "size is ${records.size()}"
		records.eachWithIndex { rec, i -> println rec }
		records.eachWithIndex { rec, i ->
			String[] d = rec.rdataToString().split(" ")
			d.eachWithIndex { part, j -> println "size is ${part.size()} : ${part}" }
		}
	}

	@Test
	public void certTH2Test() {
		String domainPart = "direct.transparenthealth.org"
		Lookup dnsLookup = new Lookup(domainPart, Type.CERT)
		Record[] records = dnsLookup.run()
		assertFalse records == null
		println "size is ${records.size()}"
		records.eachWithIndex { rec, i -> println rec }
		records.eachWithIndex { rec, i ->
			String[] d = rec.rdataToString().split(" ")
			d.eachWithIndex { part, j -> println "size is ${part.size()} : ${part}" }
		}
	}

	@Test
	public void certMPTest() {
		String domainPart = "ttt.microphr.com"
		Lookup dnsLookup = new Lookup(domainPart, Type.CERT)
		Record[] records = dnsLookup.run()
		assertFalse records == null
		println "size is ${records.size()}"
		records.eachWithIndex { rec, i -> println rec }
		records.eachWithIndex { rec, i ->
			String[] d = rec.rdataToString().split(" ")
			d.eachWithIndex { part, j -> println "size is ${part.size()} : ${part}" }
		}
	}

	@Test
	public void certMP2Test() {
		String domainPart = "direct.microphr.com"
		Lookup dnsLookup = new Lookup(domainPart, Type.CERT)
		Record[] records = dnsLookup.run()
		assertFalse records == null
		println "size is ${records.size()}"
		records.eachWithIndex { rec, i -> println rec }
		records.eachWithIndex { rec, i ->
			String[] d = rec.rdataToString().split(" ")
			d.eachWithIndex { part, j -> println "size is ${part.size()} : ${part}" }
		}
	}

}
