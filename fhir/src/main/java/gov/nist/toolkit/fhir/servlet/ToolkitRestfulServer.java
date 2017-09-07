package gov.nist.toolkit.fhir.servlet;

/*
 * Toolkit extensions to RestfulServer
 */
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.method.RequestDetails;
import ca.uhn.fhir.rest.server.Constants;
import ca.uhn.fhir.rest.server.IRestfulServer;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.exceptions.InvalidRequestException;
import ca.uhn.fhir.rest.server.servlet.ServletRequestDetails;
import ca.uhn.fhir.util.UrlPathTokenizer;
import ca.uhn.fhir.util.UrlUtil;
import org.hl7.fhir.instance.model.api.IIdType;

import java.util.StringTokenizer;

public class ToolkitRestfulServer extends RestfulServer implements IRestfulServer<ServletRequestDetails> {

    public ToolkitRestfulServer(FhirContext theCtx) {
        super(theCtx);
    }

    public void populateRequestDetailsFromRequestPath(RequestDetails theRequestDetails, String theRequestPath) {

        /*
         * This did not take into consideration the serverBaseForRequest
         * as registered with the IServerAddressStrategy
         */
        String serverBase = theRequestDetails.getServerBaseForRequest();

        // This might include /xdstools2/fsim or it may not depending on the environment (IT Test vs production)
        // What is important is that resourceName start with the resourcename (and ID etc following if present)
        // so we need to look for /fsim/ in serverBase and take everything after it

        int fsimI = serverBase.indexOf("fsim/");
        String requestPath;
        if (fsimI == -1) {
            requestPath = theRequestPath.substring(serverBase.length());
        } else {
            requestPath = theRequestPath.substring(fsimI + "fsim/".length() - 1);
            // requestPath now points to simID
            requestPath = requestPath.substring(requestPath.indexOf('/') +1);
            // requestPath now points to resource name
        }


        StringTokenizer tok = new UrlPathTokenizer(requestPath);
        String resourceName = null;

        IIdType id = null;
        String operation = null;
        String compartment = null;
        if (tok.hasMoreTokens()) {
            resourceName = tok.nextToken();
            if (partIsOperation(resourceName)) {
                operation = resourceName;
                resourceName = null;
            }
        }
        theRequestDetails.setResourceName(resourceName);

        if (tok.hasMoreTokens()) {
            String nextString = tok.nextToken();
            if (partIsOperation(nextString)) {
                operation = nextString;
            } else {
                id = getFhirContext().getVersion().newIdType();
                id.setParts(null, resourceName, UrlUtil.unescape(nextString), null);
            }
        }

        if (tok.hasMoreTokens()) {
            String nextString = tok.nextToken();
            if (nextString.equals(Constants.PARAM_HISTORY)) {
                if (tok.hasMoreTokens()) {
                    String versionString = tok.nextToken();
                    if (id == null) {
                        throw new InvalidRequestException("Don't know how to handle request path: " + theRequestPath);
                    }
                    id.setParts(null, resourceName, id.getIdPart(), UrlUtil.unescape(versionString));
                } else {
                    operation = Constants.PARAM_HISTORY;
                }
            } else if (partIsOperation(nextString)) {
                if (operation != null) {
                    throw new InvalidRequestException("URL Path contains two operations: " + theRequestPath);
                }
                operation = nextString;
            } else {
                compartment = nextString;
            }
        }

        // Secondary is for things like ..../_tags/_delete
        String secondaryOperation = null;

        while (tok.hasMoreTokens()) {
            String nextString = tok.nextToken();
            if (operation == null) {
                operation = nextString;
            } else if (secondaryOperation == null) {
                secondaryOperation = nextString;
            } else {
                throw new InvalidRequestException("URL path has unexpected token '" + nextString + "' at the end: " + theRequestPath);
            }
        }

        theRequestDetails.setId(id);
        theRequestDetails.setOperation(operation);
        theRequestDetails.setSecondaryOperation(secondaryOperation);
        theRequestDetails.setCompartmentName(compartment);
    }

    private static boolean partIsOperation(String nextString) {
        return nextString.length() > 0 && (nextString.charAt(0) == '_' || nextString.charAt(0) == '$' || nextString.equals(Constants.URL_TOKEN_METADATA));
    }

}
