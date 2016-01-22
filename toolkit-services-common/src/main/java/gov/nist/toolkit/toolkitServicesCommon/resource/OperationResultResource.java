package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Not for Public Use.
 */
@XmlRootElement
public class OperationResultResource {
    Response.Status status;
    int extendedCode = 0;
    String reason;

    static final public String EXTENDED_CODE_HEADER = "X-EXTENDED-CODE";
    static final public String REASON_HEADER = "X-REASON";

    static final public int SIM_DOES_NOT_EXIST = Response.Status.NOT_FOUND.getStatusCode()*100+1;
    static final public int CONTENT_DOES_NOT_EXIST = Response.Status.NOT_FOUND.getStatusCode()*100+2;

    static final public int ENVIRONMENT_DOES_NOT_EXIST = Response.Status.BAD_REQUEST.getStatusCode()*100+1;

    public OperationResultResource() {}

    public OperationResultResource(Response.Status status, String reason) {
        this.status = status;
        this.extendedCode = status.getStatusCode();
        this.reason = reason;
    }

    public OperationResultResource(Response response) {
        status = Response.Status.fromStatusCode(response.getStatus());
        String codeString = response.getHeaderString(EXTENDED_CODE_HEADER);
        if (codeString != null && !codeString.equals("")) extendedCode = Integer.parseInt(codeString);
        reason = response.getHeaderString(REASON_HEADER);
    }

    public Response.ResponseBuilder asHeaders(Response.ResponseBuilder builder) {
        return builder
                .header(EXTENDED_CODE_HEADER, Integer.toString(extendedCode))
                .header(REASON_HEADER, reason);
    }

    public Response.ResponseBuilder asResponse() {
       return Response.status(status)
                .header(EXTENDED_CODE_HEADER, Integer.toString(extendedCode))
                .header(REASON_HEADER, reason)
               .entity(this, null);
    }

    public Response.Status getStatus() {
        return status;
    }

    public void setStatus(Response.Status status) {
        this.status = status;
        if (extendedCode == 0)
            extendedCode = status.getStatusCode()*100;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public int getExtendedCode() {
        return extendedCode;
    }

    public void setExtendedCode(int extendedCode) {
        this.extendedCode = extendedCode;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{ ").
                append("code:").
                append("\"").
                append(status.getStatusCode()).
                append("\"").
                append(" extendedCode:").
                append("\"").
                append(extendedCode).
                append("\"").
                append(", reason:").
                append("\"").
                append((reason == null) ? null : reason.trim()).
                append("\"").
                append(" }");
        return buf.toString();
    }
}
