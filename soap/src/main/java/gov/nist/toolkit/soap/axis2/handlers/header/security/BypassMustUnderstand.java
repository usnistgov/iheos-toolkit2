package gov.nist.toolkit.soap.axis2.handlers.header.security;

import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axis2.AxisFault;
import org.apache.axis2.context.MessageContext;
import org.apache.axis2.handlers.AbstractHandler;
import org.apache.log4j.Logger;

import java.util.Iterator;

public class BypassMustUnderstand extends AbstractHandler {
    private static Logger logger = Logger.getLogger(BypassMustUnderstand.class);

    @Override
    public InvocationResponse invoke(MessageContext messageContext) throws AxisFault {
        try{

            logger.info("MustUnderstand: invoke " + messageContext.toString());

            SOAPEnvelope env = messageContext.getEnvelope();
            SOAPHeader header = env.getHeader();

            if(header != null){

                for(Iterator<?> itr = header.getChildElements(); itr.hasNext();){

                    SOAPHeaderBlock headerBlock = (SOAPHeaderBlock) itr.next();

                    if(headerBlock.getMustUnderstand()){

                        // Mark it as processed
                        headerBlock.setProcessed();
                        logger.info("MustUnderstand marked as processed: " + headerBlock.getQName());
                    }
                }
            }
        }
        catch(Exception e){
            logger.warn(e.toString());
        }

        return InvocationResponse.CONTINUE;
    }
}
