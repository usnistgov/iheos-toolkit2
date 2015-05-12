/*
 * MultipartMap.java
 *
 * Created on October 26, 2003, 12:03 PM
 */

package gov.nist.toolkit.http.axis2soap;

import gov.nist.toolkit.xdsexception.ExceptionUtil;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.util.Enumeration;
import java.util.HashMap;

import javax.mail.internet.MimeBodyPart;
import javax.servlet.http.HttpServletRequest;

import org.apache.soap.util.mime.ByteArrayDataSource;

/**
 * Class used for easier manipulation of Multipart MIME data.
 * @author Bill Majurski
 */
public class MultipartMap {
    javax.mail.internet.MimeMultipart mp;
    HashMap map, typemap;
    HashMap<Integer, Object> positionmap = null;
    HashMap<Integer, String> positiontypemap = null;
    
    String stripQuotes(String s) {
        if (s == null) return s;
        if (s.startsWith("\""))
            s = s.substring(1);
        if (s.endsWith("\""))
            s = s.substring(0,s.length()-1);
        return s;
    }
    
    /**
     * Used for testing and demonstration purposes.
     */
    static public void main(String args[]) throws Exception, java.io.IOException {
        try {
            //String xx = "------=_Part_2_9110923.1073664290010\r\nContent-Type: text/plain\r\nContent-ID: urn:uuid:d4bfb124-7922-45bc-a03d-823351eed716\r\n\r\nhttp://ratbert.ncsl.nist.gov:8081/hl7services/transform.html\r\n------=_Part_2_9110923.1073664290010\r\nContent-Type: text/plain\r\nContent-ID: urn:uuid:45b90888-49c1-4b64-a8eb-e94f541368f0\r\n\r\nhttp://ratbert.ncsl.nist.gov:8081/hl7services/rawSQL.html\r\n------=_Part_2_9110923.1073664290010--\r\n";
            String xx = "------_Part_\r\nContent-Type: text/plain\r\nContent-ID: urn:uuid:d4bfb124-7922-45bc-a03d-823351eed716\r\n\r\nhttp://ratbert.ncsl.nist.gov:8081/hl7services/transform.html\r\n------_Part_\r\nContent-Type: text/plain\r\nContent-ID: urn:uuid:45b90888-49c1-4b64-a8eb-e94f541368f0\r\n\r\nhttp://ratbert.ncsl.nist.gov:8081/hl7services/rawSQL.html\r\n------_Part_--\r\n";
            StringBufferInputStream is = new StringBufferInputStream(xx);
            String contentType = "multipart/related; boundary=----_Part_";
            ByteArrayDataSource ds = new ByteArrayDataSource(is,  contentType);
            //ByteArrayDataSource ds = new ByteArrayDataSource();
            javax.mail.internet.MimeMultipart mp = new javax.mail.internet.MimeMultipart(ds);
            int i = mp.getCount();
        } catch (javax.mail.MessagingException me) {
            throw new Exception("messaging exception in parsing for MultipartMap");
        }
        System.out.println("Done");
    }
    
    /**
     * Constructor.
     * @param is The content as an InputStream.
     * @param contentType The type of content this multipart piece is.
     * @throws java.lang.Exception Thrown if there is a problem parsing this input.
     * @throws java.io.IOException Thrown if there is an IO problem accessing this input.  (Check that 
     * InputStream is valid.)
     */
    public MultipartMap(InputStream is, String contentType)
    throws Exception, java.io.IOException {
        try {
            //contentType = contentType.replaceFirst("boundary=--", "boundary=");
            map = new HashMap();
            typemap = new HashMap();
            positionmap = new HashMap<Integer, Object>();
            positiontypemap = new HashMap<Integer, String>();
            boolean isMultipart = contentType.startsWith("multipart");
            if (isMultipart) {
                ByteArrayDataSource ds = new ByteArrayDataSource(is, contentType);
                //ByteArrayDataSource ds;
                // this should be rewritten, ByteArrayDataSource will take directly from an input stream
                //String input = getStringFromInputStream(request.getInputStream());
                //ds = new ByteArrayDataSource(input,request.getHeader("Content-Type"));
                //String ext =ds.getText();
                //String xx = "------=_Part_2_9110923.1073664290010\r\nContent-Type: text/plain\r\nContent-ID: urn:uuid:d4bfb124-7922-45bc-a03d-823351eed716\r\n\r\nhttp://ratbert.ncsl.nist.gov:8081/hl7services/transform.html\r\n------=_Part_2_9110923.1073664290010\r\nContent-Type: text/plain\r\nContent-ID: urn:uuid:45b90888-49c1-4b64-a8eb-e94f541368f0\r\n\r\nhttp://ratbert.ncsl.nist.gov:8081/hl7services/rawSQL.html\r\n------=_Part_2_9110923.1073664290010--\r\n";
                mp = new javax.mail.internet.MimeMultipart(ds);
                for (int i=0; i<mp.getCount(); i++) {
                    MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(i);
                    String name = null;
                    String contentID[] = bp.getHeader("Content-ID");
                    if (contentID.length > 0)
                        name = contentID[0];
                    if (name == null) name = "Part " + Integer.toString(i);
                    typemap.put(name, bp.getContentType());
                    positiontypemap.put(new Integer(i), bp.getContentType());
                    
                    InputStream part_is = bp.getInputStream();
                    
                    Object part_object = null;
                    String type[] = bp.getContentType().split("/");
                    if (type[0].equals("text") && type[1].equals("plain")) {
                        part_object = bp.getContent();
                        map.put(name, part_object);
                    } else {
                        map.put(name, part_is);
                        part_object = part_is;
                    }
                    positionmap.put(new Integer(i), part_object);
                }
            } else {
                map.put("Content", is);
                typemap.put("Content", contentType);
            }
        } catch (javax.mail.MessagingException me) {
            throw new Exception("messaging exception in parsing for MultipartMap: " + ExceptionUtil.exception_details(me));
        }
    }
    
    
    public HashMap<Integer, Object> getPositionMap() {
    	return positionmap;
    }
    public HashMap<Integer, String> getPositionTypeMap() {
    	return positiontypemap;
    }
    
    /**
     * Constructor builds a MultipartMap object based upon the contents of an entire
     * HttpServletRequest object.
     * @param request An HttpServletRequest object that contains Multipart content.
     * @throws javax.mail.MessagingException Thrown if there is a problem parsing this input.
     * @throws java.io.IOException Thrown if there is an IO problem accessing this input.  (Check that 
     * HttpServletRequest is valid.)
     */
    public MultipartMap(HttpServletRequest request) throws javax.mail.MessagingException, java.io.IOException {
        if (isMultipartForm(request)) {
            map = new HashMap();
            typemap = new HashMap();
            ByteArrayDataSource ds = new ByteArrayDataSource(request.getInputStream(), request.getContentType());
            //ByteArrayDataSource ds;
            // this should be rewritten, ByteArrayDataSource will take directly from an input stream
            //String input = getStringFromInputStream(request.getInputStream());
            //ds = new ByteArrayDataSource(input,request.getHeader("Content-Type"));
            mp = new javax.mail.internet.MimeMultipart(ds);
            for (int i=0; i<mp.getCount(); i++) {
                MimeBodyPart bp = (MimeBodyPart) mp.getBodyPart(i);
                ContentDisposition cd = new ContentDisposition(bp.getHeader("Content-Disposition")[0]);
                String name = stripQuotes(cd.get("name"));
                String filename = stripQuotes(cd.get("filename"));
                if (filename != null)
                    map.put("filename",  filename);
                //String name = getName(bp);
                typemap.put(name, bp.getContentType());
                String type[] = bp.getContentType().split("/");
                if (type[0].equals("text") && type[1].equals("plain")) {
                    map.put(name, bp.getContent());
                } else {
                    map.put(name, bp.getInputStream());         //getDataHandler());
                }
            }
        } else {
            throw new javax.mail.MessagingException("MultipartMap requires Multipart/Form format as input");
        }
    }
    
    /**
     * Returns the HashMap representing the content in this object.
     * @return The HashMap representing the content in this object.
     */
    public HashMap getMap() {
        return map;
    }
    
    /**
     * Returns the HashMap representing the content types in this object.
     * @return The HashMap representing the content types in this object.
     */
    public HashMap getTypeMap() {
        return typemap;
    }
    
    /**
     * Returns the attachment that has this name.  Returns null if nothing with that
     * name is found.
     * @param name The name to search for.
     * @return The attachment or null if none found.
     */
    public Object get(String name) {
        return getMap().get(name);
    }
    
    /**
     * Returns the content type of the attachment that has this name.  Returns null 
     * if nothing with that name is found.
     * @param name The name to search for.
     * @return The content type or null if none found.
     */
    public Object getType(String name) {
        return getTypeMap().get(name);
    }
    
    /**
     * A check to see whether the HttpServletRequest really contains Multipart 
     * information.  Returns true, if it does.  Returns false, if not.
     * @param request The HttpServletRequest to check.
     * @return True, if the HttpServletRequest contains Multipart information.
     * False, if it does not.
     */
    public static boolean isMultipartForm(HttpServletRequest request) {
        String contentType = request.getHeader("Content-Type");
        if (contentType == null) return false;
        String ct = contentType.substring(0,19);
        return ct.compareToIgnoreCase("multipart/form-data") == 0;
    }
    
    String getName(MimeBodyPart bp) throws java.io.IOException {
        Enumeration e;
        try {
            e = bp.getAllHeaderLines();
        } catch (javax.mail.MessagingException ex) {
            throw new java.io.IOException("cannot retrieve header lines inside getName()");
        }
        while(e.hasMoreElements()) {
            String s = (String) e.nextElement();
            int strt = s.indexOf("name=");
            if (strt != -1) {
                strt = strt+6;
                int end = s.indexOf('"',strt);
                if (end != -1) {
                    return s.substring(strt,end);
                }
            }
        }
        return null;
    }
    
    
}
