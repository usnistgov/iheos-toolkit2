package gov.nist.toolkit.valregmsg.registry;

import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.xdsexception.MetadataException;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class BasicQuery {
	protected final static Logger logger = Logger.getLogger(BasicQuery.class);
	static Properties properties = null;
	
	static {
		properties = Properties.loader();
	}
	

	public void secure_URI(Metadata metadata) throws MetadataException {
		for (OMElement doc : metadata.getExtrinsicObjects()) {
			int updated = 0;
			for (int sl=0; sl<10; sl++) {
				String uri_value = metadata.getSlotValue(doc, "URI", sl);
				if (uri_value == null) break;
				boolean save = false;
				if (uri_value.indexOf("http:") != -1) {
					updated++;
					save = true;
					uri_value = uri_value.replaceAll("http", "https");
				}
				if (uri_value.indexOf("9080") != -1) {
					updated++;
					save = true;
					uri_value = uri_value.replaceAll("9080", "9443");
				}
				if (save) {
					metadata.setSlotValue(doc, "URI", sl, uri_value);
				}
				if (updated >= 2) break;
			}
		}
	}

}
