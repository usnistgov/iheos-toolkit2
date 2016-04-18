package gov.nist.toolkit.toolkitServicesCommon.resource;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

/**
 * Not for Public Use.
 */
@XmlRootElement
public class OperationResultResource {
    Response.Status status;
    int extendedCode = 0;
    String reason;
    String reasonPhrase;
    String stackTrace = null;

    static final public String EXTENDED_CODE_HEADER = "X-EXTENDED-CODE";
    static final public String REASON_HEADER = "X-REASON";
    static final public String STACK_TRACE_HEADER = "X-STACK-TRACE";

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
        reasonPhrase = response.getStatusInfo().getReasonPhrase();
        String codeString = response.getHeaderString(EXTENDED_CODE_HEADER);
        if (codeString != null && !codeString.equals("")) extendedCode = Integer.parseInt(codeString);
        reason = response.getHeaderString(REASON_HEADER);

        StringBuilder buf = new StringBuilder();
        MultivaluedMap<String, String> map = response.getStringHeaders();
        Set<Map.Entry<String, List<String>>> sets = map.entrySet();
        Map<String, String> traceHeaders = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : sets) {
            String key = entry.getKey();
            if (key.startsWith(STACK_TRACE_HEADER)) {
                for (String value : entry.getValue()) {
                    traceHeaders.put(key, value);
                }
            }
        }

        for (String key : sortAsNumbers(asList(traceHeaders.keySet()), STACK_TRACE_HEADER)) {
            String value = traceHeaders.get(key);
//            System.out.println(String.format("%s => %s", key, value));
            buf.append(traceHeaders.get(key)).append("\n");
        }

        stackTrace = buf.toString();
    }

    List<String> asList(Set<String> in) {
        List<String> out = new ArrayList<>();
        for (String x : in) out.add(x);
        return out;
    }

    List<String> sortAsNumbers(List<String> in, String prefix) {
        Collections.sort(in,
                new Comparator<String>() {
                    @Override
                    public int compare(String o1, String o2) {
                        if (o1.startsWith(STACK_TRACE_HEADER))
                            o1 = o1.substring(STACK_TRACE_HEADER.length());
                        if (o2.startsWith(STACK_TRACE_HEADER))
                            o2 = o2.substring(STACK_TRACE_HEADER.length());
                        int i1 = Integer.parseInt(o1);
                        int i2 = Integer.parseInt(o2);
                        if (i1 == i2) return 0;
                        return (i1 < i2) ? -1 : 1;
//                        return o1.compareTo(o2);
                    }
                });
        return in;
    }

    public Response.ResponseBuilder asHeaders(Response.ResponseBuilder builder) {
        return builder
                .header(EXTENDED_CODE_HEADER, Integer.toString(extendedCode))
                .header(REASON_HEADER, reason);
    }

    public Response.ResponseBuilder asResponse() {
       Response.ResponseBuilder builder = Response.status(status)
                .header(EXTENDED_CODE_HEADER, Integer.toString(extendedCode))
                .header(REASON_HEADER, reason);

        int i = 1;

        List<String> lines = asLineList(stackTrace);
        // first line is exception message.  Rest is stack trace
        if (lines.size() > 0) {
            builder.header(String.format("%s%d", STACK_TRACE_HEADER, i), lines.get(0));
            i++;
        }
        for (String traceLine : onlyToolkitLines(lines)) {
            builder.header(String.format("%s%d", STACK_TRACE_HEADER, i), traceLine);
            i++;
        }

        return builder.entity(this, null);
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

    List<String> asLineList(String x) {
        String[] lines = x.split("\n");
        List<String> lineList = new ArrayList<>();
        for (int i=0; i<lines.length; i++) {
            lineList.add(lines[i]);
        }
        return lineList;
    }

    List<String> onlyToolkitLines(List<String> in) {
        List<String> out = new ArrayList<String>();

        for (String line : in) {
            if (line.contains("gov.nist.toolkit"))
                out.add(line);
        }

        return out;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
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

                append(" reason:").
                append("\"").
                append((reason == null) ? null : reason.trim()).
                append("\"").

                append(" reasonPhrase:").
                append("\"").
                append((reasonPhrase == null) ? null : reasonPhrase.trim()).
                append("\"");

        if (stackTrace != null) {
            buf.append("\nStack Trace:\n  ").
                    append(stackTrace.replaceAll("\n", "\n  ")).
                    append("\n");
        }

        buf.append(" }");
        return buf.toString();
    }
}
