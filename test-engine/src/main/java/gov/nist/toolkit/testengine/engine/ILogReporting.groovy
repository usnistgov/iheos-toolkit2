package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.AxisFault;

import java.util.List;

public interface ILogReporting {
    void addDetail(String name, String value);
    void addDetailHeader(String headerText);
    void addDetailHeader(String headerText, String value);
    void addDetailLink(String externalLink, String internalPlaceToken, String linkText, String content);
    void set_error(String msg) throws XdsInternalException;
    void fail(OMElement ele) throws XdsInternalException;
    void set_error(List<String> msgs) throws XdsInternalException;
    void set_fault(String code, String msg) throws XdsInternalException;
    void set_fault(AxisFault e) throws XdsInternalException;
    void fail(String message) throws XdsInternalException;
}
