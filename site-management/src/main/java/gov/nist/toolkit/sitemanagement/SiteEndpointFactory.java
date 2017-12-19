package gov.nist.toolkit.sitemanagement;

import gov.nist.toolkit.actortransaction.server.EndpointParser;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.sitemanagement.client.TransactionBean;

/**
 * Created by bill on 9/22/17.
 */
public class SiteEndpointFactory {

    static public void updateNonTlsTransactionsToPort(Site s, String port) {
        for (TransactionBean tr : s.transactions.transactions) {
            if (!tr.isSecure) {
                String end = tr.getEndpoint();
                EndpointParser ep = new EndpointParser(end);
                ep.setPort(port);
                tr.setEndpoint(ep.getEndpoint());
            }
        }
    }

}
