package gov.nist.toolkit.session.server;

import gov.nist.toolkit.session.shared.Message;
import gov.nist.toolkit.utilities.xml.OMFormatter;

import java.util.StringTokenizer;

/**
 * Break input into parts.  These are not Mime parts but something slightly simpler.
 * The target is to be able to take a HTTP/SOAP message and format the XML while leaving the
 * non-XML stuff alone.
 */
public class MessageBuilder {

    public static Message parseMessage(String input) {
        Message m = new Message();
        StringBuilder b = new StringBuilder();
        String delims = "\n";

        StringTokenizer st = new StringTokenizer(input, delims);
        while (st.hasMoreElements()) {
            String ele = (String) st.nextElement();
            ele = ele.trim();

            if (ele.trim().length() == 0 && b.length() > 0) {  // blank line triggers new part
                String part = b.toString();
                if (part.startsWith("<")) {
                    try {
                        part = new OMFormatter(part).toHtml();
                    } catch (Exception e) {
                        m.add("*****  Could not XML format this content *****");
                    }
                }
                m.add(part);
                b = new StringBuilder();
                continue;
            }
            if (ele.startsWith("<")) {  // start of some XML
                if (b.length() > 0) { // if buffer not empty - create new part and empty buffer
                    m.add(b.toString());
                    b = new StringBuilder();
                }
            }
            b.append(ele).append("\n");
        }
        if (b.length() > 0) {
            m.add(b.toString());
        }

        return m;
    }
}
