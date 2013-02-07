package gov.nist.toolkit.dns;

import org.xbill.DNS.Lookup;
import org.xbill.DNS.Record;
import org.xbill.DNS.TextParseException;
import org.xbill.DNS.Type;

public class DnsLookup {

	public String getMxRecord(String domainname) throws TextParseException {
		Lookup dnsLookup = new Lookup(domainname, Type.MX);
		Record[] records = dnsLookup.run();
		if (records.length == 0)
			return null;
		String[] d = records[0].rdataToString().split(" ");
		if (d.length < 2)
			return null;
		String value = d[1];
		if (value.endsWith("."))
			value = value.substring(0, value.length()-1);
		return value;
	}
	
	public String getCertRecord(String domainname) throws TextParseException {
		Lookup dnsLookup = new Lookup(domainname, Type.CERT);
		Record[] records = dnsLookup.run();
		if (records.length == 0)
			return null;
		String[] d = records[0].rdataToString().split(" ");
		if (d.length < 4)
			return null;
		String value = d[3];
		return value;
	}

}
