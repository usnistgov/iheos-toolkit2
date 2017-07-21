/**
 * 
 */
package gov.nist.toolkit.saml.builder;

import java.util.Iterator;

import javax.xml.namespace.QName;

import org.apache.axiom.soap.RolePlayer;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.engine.Handler.InvocationResponse;
import org.apache.axis2.handlers.AbstractHandler;

/**
 * @author vbeera
 *
 */
public class PatchForMustUnderstand {

	
	public static class SecurityHandler extends AbstractHandler 
	{ 
	   // @Override 
	    public InvocationResponse invoke(MessageContext msgContext) throws AxisFault 
	    { 
	        org.apache.axiom.soap.SOAPEnvelope envelope = msgContext.getEnvelope(); 

	        if (envelope.getHeader() == null) 
	        { 
	            return InvocationResponse.CONTINUE; 
	        } 

	        // Get all the headers targeted to us 
	        Iterator headerBlocks = envelope.getHeader().getHeadersToProcess((RolePlayer)msgContext.getConfigurationContext().getAxisConfiguration().getParameterValue("rolePlayer")); 

	        while (headerBlocks.hasNext()) 
	        { 
	            SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) headerBlocks.next(); 
	            QName headerName = headerBlock.getQName(); 

	            if(headerName.getLocalPart().equals("Security")) 
	            { 
	                headerBlock.setProcessed(); 
	            } 
	        } 
	        return InvocationResponse.CONTINUE; 
	    } 
	}
}
