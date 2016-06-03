/**
 * 
 */
package gov.nist.toolkit.simulators.sim.idc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.registrymsg.repository.RetrieveImageRequestGenerator;
import gov.nist.toolkit.registrymsg.repository.RetrieveImageRequestModel;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentModel;
import gov.nist.toolkit.registrymsg.repository.RetrievedDocumentsModel;
import gov.nist.toolkit.simulators.support.BaseDsActorSimulator;
import gov.nist.toolkit.sitemanagement.client.Site;
import gov.nist.toolkit.soap.axis2.Soap;
import gov.nist.toolkit.testengine.engine.RetrieveB;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;

/**
 * Image Document Consumer Actor Simulator. PRELIMINARY @author Ralph Moulton /
 * MIR WUSTL IHE Development Project <a
 * href="mailto:moultonr@mir.wustl.edu">moultonr@mir.wustl.edu</a>
 */
public class ImgDocConsActorSimulator extends BaseDsActorSimulator {

   private TransactionType type = TransactionType.RET_IMG_DOC_SET;
   public void setTransactionType(TransactionType type) {
      this.type = type;
   }

   static private final Logger logger =
      Logger.getLogger(ImgDocConsActorSimulator.class);
   private List <OMElement> extraSoapHeaderElements = new ArrayList <>();

   /** boolean, is transaction to use TLS connection */
   private boolean tls = false;
   private boolean direct = false;
   private Site site = null;
   private String endpoint = null;
   /** If present, SOAP headers and Request body are stored here */
   private String messageDir = null;

   /**
    * @return the {@link #tls} value.
    */
   public boolean isTls() {
      return tls;
   }

   /**
    * @param tls the {@link #tls} to set
    */
   public void setTls(boolean tls) {
      this.tls = tls;
   }
   
   public void setDirect(boolean direct) {
      this.direct = direct;
   }

   /**
    * @param site the {@link #site} to set
    */
   public void setSite(Site site) {
      this.site = site;
   }

   /**
    * @param endpoint the {@link #endpoint} to set
    */
   public void setEndpoint(String endpoint) {
      this.endpoint = endpoint;
   }

   /**
    * @return the {@link #messageDir} value.
    */
   public String getMessageDir() {
      return messageDir;
   }

   /**
    * @param messageDir the {@link #messageDir} to set
    */
   public void setMessageDir(String messageDir) {
      this.messageDir = messageDir;
   }

   /**
    * This would be used if this were a server sim. It is useless as a client
    * sim @param transactionType transaction code @param mvc
    * MessageValidatorEngine - execution engine for validators and
    * simulators @param validation name of special validation to be run. Allows
    * simulators to be extended to perform test motivated
    * validations @return @throws IOException
    */
   @Override
   public boolean run(TransactionType transactionType,
      MessageValidatorEngine mvc, String validation) throws IOException {
      return false;
   }

   @Override
   public void init() {}

   public ImgDocConsActorSimulator() {}

   public RetrievedDocumentsModel retrieve(
      RetrieveImageRequestModel rModel) throws Exception {

      if (!direct) endpoint = site.getEndpoint(type, isTls(), false);

      OMElement retrieveRequest = buildRetrieve(rModel);

      Soap soap = new Soap();
      for (OMElement ele : extraSoapHeaderElements) {
         soap.addHeader(ele);
      }

      OMElement result = soap.soapCall(retrieveRequest, endpoint, 
         true, // mtom
         true, // addressing
         true, // SOAP1.2
         type.getRequestAction(), type.getResponseAction());

      logger.info(result.toStringWithConsume());
      
      if (messageDir != null) {
         writeToFile(soap.getOutHeader().toString(), messageDir, "RequestHeader.xml");
         writeToFile(soap.getInHeader().toString(), messageDir, "ResponseHeader.xml");
         writeToFile(retrieveRequest.toString(), messageDir, "RequestBody.xml");
      }

      return parseResponse(result);

   } // EO retrieve method

   private OMElement buildRetrieve(RetrieveImageRequestModel iModel)
      throws FactoryConfigurationError {
      RetrieveImageRequestGenerator g =
         new RetrieveImageRequestGenerator(iModel);
      return g.get();
   }

   public static RetrievedDocumentsModel parseResponse(OMElement result)
      throws Exception {
      RetrieveB retb = new RetrieveB(null);
      Map <String, RetrievedDocumentModel> map =
         retb.parse_rep_response(result).getMap();
      RetrievedDocumentsModel rModel = new RetrievedDocumentsModel();
      rModel.setMap(map);
      rModel.setAbbreviatedMessage(abbreviateResponse(result));
      return rModel;
   }
   
   /**
    * Returns the passed response message in string form, replacing text in
    * {@code <Document>} elements with "...". <b>Destructive</b>
    * @param resp passed message
    * @return String version of abbreviated response
    */
   @SuppressWarnings("unchecked")
   public static  String abbreviateResponse(OMElement resp) throws XMLStreamException {
      Iterator<OMElement> dri = resp.getChildrenWithLocalName("DocumentResponse");
      while (dri.hasNext()) {
         OMElement dr = dri.next();
         Iterator<OMElement> di = dr.getChildrenWithLocalName("Document");
         while (di.hasNext()) {
            OMElement d = di.next();
            d.setText("...");
         } // <Document> loop         
      } // <DocumentResponse> loop
      return resp.toStringWithConsume();
   }
   
   private void writeToFile(String str, String dir, String fName) throws Exception{
      Path pfn = Paths.get(dir, fName);
      Files.write(pfn, str.getBytes("UTF-8"));
   }

} // EO ImgDocConsActorSimulator class
