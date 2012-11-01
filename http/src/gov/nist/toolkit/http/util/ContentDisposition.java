/*
 * ContentDisposition.java
 *
 * Created on November 5, 2003, 2:02 PM
 */

package gov.nist.toolkit.http.util;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
/**
 *
 * @author  bill
 */
public class ContentDisposition {
    HashMap map;
    
    /** Creates a new instance of ContentDisposition */
    public ContentDisposition(HttpServletRequest request) {
        String cd = request.getHeader("Content-Disposition");
        parse(cd);
    }
    
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
    
    public String get(String name) {
        return (String) map.get(name);
    }
    
}
