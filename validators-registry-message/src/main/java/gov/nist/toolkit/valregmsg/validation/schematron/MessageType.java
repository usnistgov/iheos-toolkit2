/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.nist.toolkit.valregmsg.validation.schematron;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlObject;

/**
 * @author mccaffrey
 */
public class MessageType {

    private String messageId = null;
    private String schemaLocation = null;
    private String schematronLocation = null;

    public MessageType() {}

    public MessageType(XmlObject xmlObject) {
        this.setMessageId(xmlObject.selectAttribute(new QName("id")).newCursor().getTextValue());
        XmlObject[] schemaLocationElement = xmlObject.selectChildren(new QName("schemaLocation"));
        if (schemaLocationElement != null && schemaLocationElement.length > 0)
            this.setSchemaLocation(schemaLocationElement[0].newCursor().getTextValue());
        XmlObject[] schematronLocationElement = xmlObject.selectChildren(new QName("schematronLocation"));
        if (schematronLocationElement != null && schematronLocationElement.length > 0)
            this.setSchematronLocation(schematronLocationElement[0].newCursor().getTextValue());

    }

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @param messageId the messageId to set
     */
    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    /**
     * @return the schemaLocation
     */
    public String getSchemaLocation() {
        return schemaLocation;
    }

    /**
     * @param schemaLocation the schemaLocation to set
     */
    public void setSchemaLocation(String schemaLocation) {
        this.schemaLocation = schemaLocation;
    }

    /**
     * @return the schematronLocation
     */
    public String getSchematronLocation() {
        return schematronLocation;
    }

    /**
     * @param schematronLocation the schematronLocation to set
     */
    public void setSchematronLocation(String schematronLocation) {
        this.schematronLocation = schematronLocation;
    }

}
