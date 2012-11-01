/*
 * RequestBean.java
 *
 * Created on September 15, 2004, 7:08 PM
 */


package gov.nist.toolkit.http.util;

import gov.nist.toolkit.utilities.io.Io;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;





/**
 * @author bill
 */
public class RequestBean extends Object implements Serializable {
    
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";
    
    private PropertyChangeSupport propertySupport;
    
    /**
     * Holds value of property request.
     */
    private HttpServletRequest request;
    
    /**
     * Holds value of property multiMap.
     */
    private MultipartMap multiMap;
    
    /**
     * Holds value of property action.
     */
    private String action;
    
    /**
     * Holds value of property partName.
     */
    private String partName;
    
    public RequestBean() {
        propertySupport = new PropertyChangeSupport(this);
    }
    
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    /**
     * Getter for property request.
     * @return Value of property request.
     */
    public HttpServletRequest getRequest() {
        return this.request;
    }
    
    /**
     * Setter for property request.
     * @param request New value of property request.
     */
    public void setRequest(HttpServletRequest request)
    throws javax.mail.MessagingException, java.io.IOException {
        this.request = request;
        if (MultipartMap.isMultipartForm(request)) {
            multiMap = new MultipartMap(request);
            action = (String) multiMap.get("action");
        } else
            action = request.getParameter("action");
    }
    
    /**
     * Getter for property multiMap.
     * @return Value of property multiMap.
     */
    public MultipartMap getMultiMap() {
        return this.multiMap;
    }
    
    /**
     * Setter for property multiMap.
     * @param multiMap New value of property multiMap.
     */
    public void setMultiMap(MultipartMap multiMap) {
        this.multiMap = multiMap;
    }
    
    public Collection getMapKeys() {
        if (multiMap != null)
            return multiMap.getNames();
        return new ArrayList();
    }
    
    /**
     * Getter for property action.
     * @return Value of property action.
     */
    public String getAction() {
        return this.action;
    }
    
    /**
     * Setter for property action.
     * @param action New value of property action.
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Getter for property part.
     * @return Value of property part.
     */
    public String getPart() throws java.io.IOException {
        if (multiMap==null)
            return null;
        
        Object o = multiMap.get(partName);
        if (o instanceof InputStream) {
            return Io.getStringFromInputStream((InputStream)o);
        } else {
            return (String) o;
        }
    }
    
    /**
     * Getter for property partName.
     * @return Value of property partName.
     */
    public String getPartName() {
        return this.partName;
    }
    
    /**
     * Setter for property partName.
     * @param partName New value of property partName.
     */
    public void setPartName(String partName) {
        this.partName = partName;
    }
    
}
