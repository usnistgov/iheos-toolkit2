
/*
 * ContentDisposition.java
 *
 * Created on November 5, 2003, 2:02 PM
 */

package gov.nist.toolkit.http.axis2soap;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
/**
 * A utility class for parsing the Content Disposition field for a MIME
 * communication.
 * @author Bill Majurski
 */
public class ContentDisposition {
    HashMap map;
    
    /**
     * Creates a new instance of ContentDisposition
     * @param request The HTTP Request of the MIME communication.
     */
    public ContentDisposition(HttpServletRequest request) {
        String cd = request.getHeader("Content-Disposition");
        parse(cd);
    }
    
    /**
     * Constructor
     * @param cd The Content Disposition header as a String.
     */
    public ContentDisposition(String cd) {
        parse(cd);
    }
    
    void parse(String contDisp) {
        map = new HashMap();
        String[] parts = contDisp.split(";");
        for (int i=0; i<parts.length; i++) {
            String part = parts[i].trim();
            String[] name_value = part.split("=");
            if (name_value.length == 1) {
                map.put("format", name_value[0]);
                continue;
            }
            if (name_value.length == 0)
                continue;
            map.put(name_value[0], name_value[1]);
        }
    }
    
    /**
     * Given a name, return a value as defined in the Content Disposition.  Returns
     * null if the name has no corresponding value.
     * @param name A name listed in the Content Disposition.
     * @return The value corresponding to that name.
     */
    public String get(String name) {
        return (String) map.get(name);
    }
    
}
