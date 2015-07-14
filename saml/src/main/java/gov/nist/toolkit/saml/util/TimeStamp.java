package gov.nist.toolkit.saml.util;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Timestamp according to SOAP Message Security 1.0,
 * 
 * @author Srinivasarao Eadara
 */
public class TimeStamp {
	protected Element element = null;
    protected List<Element> customElements = null;
    protected Date createdDate;
    protected Date expiresDate;
    
    protected String validateResult = "" ;
    protected int errorVal = 0 ;
    
    public static final String WSU_NS = 
        "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    public static final String WSU_PREFIX = "wsu";
    public static final String TIMESTAMP_TOKEN_LN = "Timestamp";

    public static final String CREATED_LN = "Created";
    public static final String EXPIRES_LN = "Expires";

    public String getValidateResult() {
		return validateResult;
	}

	public void setValidateResult(String validateResult) {
		this.validateResult = validateResult;
	}

	public int getErrorVal() {
		return errorVal;
	}

	public void setErrorVal(int errorVal) {
		this.errorVal = errorVal;
	}

	/**
     * Constructs a <code>Timestamp</code> object and parses the
     * <code>wsu:Timestamp</code> element to initialize it.
     *
     * @param timestampElement the <code>wsu:Timestamp</code> element that
     *        contains the timestamp data
     */
    public TimeStamp(Element timestampElement) throws Exception {
        this(timestampElement, true);
    }

    /**
     * Constructs a <code>Timestamp</code> object and parses the
     * <code>wsu:Timestamp</code> element to initialize it.
     *
     * @param timestampElement the <code>wsu:Timestamp</code> element that
     *        contains the timestamp data
     * @param bspCompliant whether the Timestamp processing complies with the BSP spec
     */
    public TimeStamp(Element timestampElement, boolean bspCompliant) throws Exception {

        element = timestampElement;
        customElements = new ArrayList<Element>();

        String strCreated = null;
        String strExpires = null;

        for (Node currentChild = element.getFirstChild();
             currentChild != null;
             currentChild = currentChild.getNextSibling()
         ) {
            if (Node.ELEMENT_NODE == currentChild.getNodeType()) {
                Element currentChildElement = (Element) currentChild;
                if (CREATED_LN.equals(currentChild.getLocalName()) &&
                        WSU_NS.equals(currentChild.getNamespaceURI())) {
                    if (strCreated == null) {
                        String valueType = currentChildElement.getAttribute("ValueType");
                        if (bspCompliant && valueType != null && !"".equals(valueType)) {
                            // We can't have a ValueType attribute as per the BSP spec
                            throw new Exception(
                                "Invalid TimeStamp.."
                            );
                        }
                        strCreated = ((Text)currentChildElement.getFirstChild()).getData();
                    } else {
                        // Test for multiple Created elements
                        throw new Exception(
                            "Invalid TimeStamp"
                        );
                    }
                } else if ("Expires".equals(currentChild.getLocalName()) &&
                        WSU_NS.equals(currentChild.getNamespaceURI())) {
                    if (strExpires == null) {
                        String valueType = currentChildElement.getAttribute("ValueType");
                        if (bspCompliant && valueType != null && !"".equals(valueType)) {
                            // We can't have a ValueType attribute as per the BSP spec
                            throw new Exception(
                            		"Invalid TimeStamp.."
                            );
                        }
                        strExpires = ((Text)currentChildElement.getFirstChild()).getData();
                    } else if (strExpires != null || (bspCompliant && strCreated == null)) {
                        //
                        // Created must appear before Expires, and we can't have multiple
                        // Expires elements
                        //
                        throw new Exception(
                        		"Invalid TimeStamp.."
                        );                        
                    }
                } else {
                    if (bspCompliant) {
                        throw new Exception(
                        		"Invalid TimeStamp.."
                        );
                    }
                    customElements.add(currentChildElement);
                }
            }
        }
        
        // We must have a Created element
        if (bspCompliant && strCreated == null) {
            throw new Exception(
            		"Invalid TimeStamp.."
            );  
        }

        // Parse the dates
        DateFormat zulu = new XmlSchemaDateFormat();
        if (bspCompliant) {
            zulu.setLenient(false);
        }
        try {
            System.out.println("Current time: " + zulu.format(new Date()));
            if (strCreated != null) {
                createdDate = zulu.parse(strCreated);
                System.out.println("Timestamp created: " + zulu.format(createdDate));
            }
            if (strExpires != null) {
                expiresDate = zulu.parse(strExpires);
                    System.out.println("Timestamp expires: " + zulu.format(expiresDate));
            }
            
        } catch (Exception e) {
            throw new Exception(
                "Invalid TimeStamp"
            );
        }
    }


    /**
     * Constructs a <code>Timestamp</code> object according
     * to the defined parameters.
     *
     * @param doc the SOAP envelope as <code>Document</code>
     * @param ttl the time to live (validity of the security semantics) in seconds
     */
    public TimeStamp(boolean milliseconds, Document doc, int ttl) {

        customElements = new ArrayList<Element>();
        element = 
            doc.createElementNS(
                WSU_NS, WSU_PREFIX + ":" + TIMESTAMP_TOKEN_LN
            );

        DateFormat zulu = null;
        if (milliseconds) {
            zulu = new XmlSchemaDateFormat();
        } else {
            zulu = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            zulu.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        Element elementCreated =
            doc.createElementNS(
                WSU_NS, WSU_PREFIX + ":" +CREATED_LN
            );
        createdDate = new Date();
        elementCreated.appendChild(doc.createTextNode(zulu.format(createdDate)));
        element.appendChild(elementCreated);
        if (ttl != 0) {
            expiresDate = new Date();
            expiresDate.setTime(createdDate.getTime() + (ttl * 1000));

            Element elementExpires =
                doc.createElementNS(
                    WSU_NS, WSU_PREFIX + ":" + EXPIRES_LN
                );
            elementExpires.appendChild(doc.createTextNode(zulu.format(expiresDate)));
            element.appendChild(elementExpires);
        }
    }
    
    /**
     * Add the WSU Namespace to this T. The namespace is not added by default for
     * efficiency purposes.
     *
    public void addWSUNamespace() {
        WSSecurityUtil.setNamespace(element, WSU_NS, WSU_PREFIX);
    }

    /**
     * Returns the dom element of this <code>Timestamp</code> object.
     *
     * @return the <code>wsse:UsernameToken</code> element
     */
    public Element getElement() {
        return element;
    }

    /**
     * Returns the string representation of the token.
     *
     * @return a XML string representation
     */
    public String toString() {
        return DOM2Writer.nodeToString((Node) element);
    }

    /**
     * Get the time of creation.
     *
     * @return the "created" time
     */
    public Date getCreated() {
        return createdDate;
    }

    /**
     * Get the time of expiration.
     *
     * @return the "expires" time
     */
    public Date getExpires() {
        return expiresDate;
    }

    /**
     * Creates and adds a custom element to this Timestamp
     */
    public void addCustomElement(Document doc, Element customElement) {
        customElements.add(customElement);
        element.appendChild(customElement);
    }

    /**
     * Get the the custom elements from this Timestamp
     *
     * @return the list containing the custom elements.
     */
    public List<Element> getCustomElements() {
        return customElements;
    }
    
    /**
     * Set wsu:Id attribute of this timestamp
     * @param id
     */
    public void setID(String id) {
        element.setAttributeNS(WSU_NS, WSU_PREFIX + ":Id", id);
    }
    
    /**
     * @return the value of the wsu:Id attribute
     */
    public String getID() {
        return element.getAttributeNS(WSU_NS, "Id");
    }
    
    /**
     * Return true if the current Timestamp is expired, meaning if the "Expires" value
     * is before the current time. It returns false if there is no Expires value.
     */
    public boolean isExpired() {
        if (expiresDate != null) {
            Date rightNow = new Date();
            return expiresDate.before(rightNow);
        }
        return false;
    }
    
    
    /**
     * Return true if the "Created" value is before the current time minus the timeToLive
     * argument, and if the Created value is not "in the future".
     * 
     * @param timeToLive the value in seconds for the validity of the Created time
     * @param futureTimeToLive the value in seconds for the future validity of the Created time
     * @return true if the timestamp is before (now-timeToLive), false otherwise
     */
    public boolean verifyCreated(
        long timeToLive,
        long futureTimeToLive
    ) {
        Date validCreation = new Date();
        long currentTime = validCreation.getTime();
        if (futureTimeToLive > 0) {
            validCreation.setTime(currentTime + futureTimeToLive * 1000);
        }
        // Check to see if the created time is in the future
        if (createdDate != null && createdDate.after(validCreation)) {
           
                validateResult="Validation of Timestamp: The message was created in the future!";
                errorVal = 1 ;
            return false;
        }
        
        // Calculate the time that is allowed for the message to travel
        currentTime -= timeToLive * 1000;
        validCreation.setTime(currentTime);

        // Validate the time it took the message to travel
        if (createdDate != null && createdDate.before(validCreation)) {
            
        	validateResult="Validation of Timestamp: The message was created too long ago";
        	errorVal = 1 ;
            return false;
        }

        
        validateResult="Validation of Timestamp: Everything is ok";
       
        return true;
    }

    
}
