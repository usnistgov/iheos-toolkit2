/**
 * 
 */
package gov.nist.toolkit.simulators.servlet;

import edu.wustl.mir.erl.ihe.xdsi.util.Utility;
import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.configDatatypes.SimulatorProperties;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.GwtErrorRecorderBuilder;
import gov.nist.toolkit.http.HttpHeader;
import gov.nist.toolkit.http.HttpHeader.HttpHeaderParseException;
import gov.nist.toolkit.http.ParseException;
import gov.nist.toolkit.installation.Installation;
import gov.nist.toolkit.simcommon.client.BadSimIdException;
import gov.nist.toolkit.simcommon.client.SimId;
import gov.nist.toolkit.simcommon.client.SimulatorConfig;
import gov.nist.toolkit.simcommon.client.config.SimulatorConfigElement;
import gov.nist.toolkit.simcommon.server.GenericSimulatorFactory;
import gov.nist.toolkit.simcommon.server.RuntimeManager;
import gov.nist.toolkit.simcommon.server.SimDb;
import gov.nist.toolkit.simulators.support.BaseHttpActorSimulator;
import gov.nist.toolkit.simulators.support.SimCommon;
import gov.nist.toolkit.utilities.io.Io;
import gov.nist.toolkit.valsupport.client.MessageValidationResults;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.DefaultValidationContextFactory;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.xdsexception.ExceptionUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

/**
 * Servlet for http (only) based transaction simulations
 */
public class HttpSimServlet extends HttpServlet {
   private static final long serialVersionUID = 1L;
   
   private static String nl = Utility.nl;
   
   static Logger logger = Logger.getLogger(SimServlet.class);
   static ServletConfig config;
   Map<String, String> headers = new HashMap<String, String>();
   String contentType;
   HttpHeader contentTypeHeader;
   String bodyCharset;
   MessageValidationResults mvr;
   
   @Override
   public void doGet(HttpServletRequest request, HttpServletResponse response) {
      String uri = request.getRequestURI().toLowerCase();
      Date now = new Date();
      logger.info("+ + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + " + now.toString());
      logger.info("HttpSimServlet: GET " + uri);
      
      String[] uriParts = uri.split("\\/");
      
      // parse URI. format:
      // http://host:port/xdstools2/httpsim/simid/actor/transaction
      // where
      //   simid is a uniqueID for a simulator, for example, acme__ids
      //   actor is an actor code, for example, ids
      //   transaction is a transaction name, for example, wado.ret.ids
      
      SimId simid;
      SimulatorConfig simConfig;
      File simDbFile;
      String actor;
      String transaction;
      TransactionType transactionType;
      
      try {
         // find location of httpsim in uri; must be at least three more segments 
         int index;
         for (index = 0; index < uriParts.length; index++) 
            if (uriParts[index].equals("httpsim")) break;
         if ((index + 3) >= uri.length()) throw new Exception("Required segments missing");
         
         // simulator being accessed
         String simulatorName = uriParts[index + 1];
         try {
            simid = new SimId(simulatorName);
         } catch (BadSimIdException bsie) {
            throw new Exception("invalid simulator id [" + simulatorName + "]");
         }
         simDbFile = Installation.instance().simDbFile();
         try {
            simConfig = GenericSimulatorFactory.getSimConfig(simid);
         } catch (IOException ioe) {
            throw new Exception("IO error trying to load simulator id [" + simulatorName + "]");
         } catch (ClassNotFoundException cnfe) {
            throw new Exception("sim class not found for simulator id [" + simulatorName + "]");
         }
         
         actor = uriParts[index + 2];
         transaction = uriParts[index + 3];
         transactionType = ATFactory.findTransactionByShortName(transaction);
         if (transactionType == null) 
            throw new Exception("invalid transaction [" + transaction + "]");
         logger.debug("Incoming transaction [" + transaction + "], which is " + transactionType);
         
      } catch (Exception e) {
         try {
            String em = e.getMessage() + getFormat(request);
            logger.info(em);
            response.sendError(400, em);
            return;
         } catch (IOException e1) {
            logger.warn("I/O error attempting to send error response " + e1.getMessage());
            return;
         }
      }
      
      MessageValidatorEngine mvc = new MessageValidatorEngine();
      try {
         
         SimDb db = new SimDb(simid, actor, transaction);
         // These are passed to the filter for logging
         request.setAttribute("SimDb", db);
         logRequest(request, db, actor, transaction);
         request.setAttribute("mvc", mvc);
         
         ValidationContext vc = DefaultValidationContextFactory.validationContext();
         SimulatorConfigElement asce = simConfig.get(SimulatorProperties.codesEnvironment);
         if (asce != null) vc.setCodesFilename(asce.asString());
         SimCommon simCommon = new SimCommon(db, simConfig, request.getRequestURI().startsWith("https"), vc, mvc, request, response);
         

         ErrorRecorder er = new GwtErrorRecorderBuilder().buildNewErrorRecorder();
         er.sectionHeading("Endpoint");
         er.detail("Endpoint is " + uri);
         mvc.addErrorRecorder("**** Http Service: " + uri, er);
         
         BaseHttpActorSimulator sim = (BaseHttpActorSimulator) RuntimeManager.getHttpSimulatorRuntime(simid);
         
         sim.init(simCommon);
         SimulatorConfigElement sce = simConfig.getConfigEle(SimulatorProperties.FORCE_FAULT);
         if (sce != null && sce.asBoolean()) 
            throw new Exception("Forced fault");
         sim.onTransactionBegin(simConfig);
         sim.run(transactionType, mvc);
         sim.onTransactionEnd(simConfig);
         return;
         
      } catch  (Exception e){
         try {
            response.sendError(400, e.getMessage());
         } catch (IOException e1) {
            logger.warn("I/O error attempting to send error response " + e1.getMessage());
            return;
         }
      }
      
      // TODO current
      
   } // EO doGet method

   @Override
   public void init(ServletConfig sConfig) throws ServletException {
      super.init(sConfig);
      config = sConfig;
      logger.info("Initializing HttpSimServlet");
      File warHome = new File(config.getServletContext().getRealPath("/"));
      logger.info("...warHome is " + warHome);
      Installation.instance().warHome(warHome);
        logger.info("...warHome initialized to " + Installation.instance().warHome());

      Installation.instance().setServletContextName(getServletContext().getContextPath());

      onServiceStart();

      logger.info("HttpSimServlet initialized");
   }

   @Override
   public void destroy() {
      onServiceStop();
   }
   

   public MessageValidationResults getMessageValidationResults() {
      return mvr;
   }
   
   public static void onServiceStart()  {
      try {
         List<SimId> simIds = new SimDb().getAllSimIds();
         for (SimId simId : simIds) {
            BaseHttpActorSimulator sim = (BaseHttpActorSimulator) RuntimeManager.getHttpSimulatorRuntime(simId);
            if (sim == null) continue;

            SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(simId);
            sim.init(asc);
            sim.onServiceStart(asc);
         }
      } catch (Exception e) {
         logger.fatal(ExceptionUtil.exception_details(e));
      }
   }

   public static void onServiceStop() {
      try {
         List<SimId> simIds = new SimDb().getAllSimIds();
         for (SimId simId : simIds) {
            BaseHttpActorSimulator sim = (BaseHttpActorSimulator) RuntimeManager.getHttpSimulatorRuntime(simId);
            if (sim == null) continue;

            SimulatorConfig asc = GenericSimulatorFactory.getSimConfig(simId);
            sim.init(asc);
            sim.onServiceStop(asc);
         }
      } catch (Exception e) {
         logger.fatal(ExceptionUtil.exception_details(e));
      }
   }
   
   /*
    * Stores HTTP Request header and body in simulator transaction directory.
    * This version includes parameters.
    */
   void logRequest(HttpServletRequest request, SimDb db, String actor, String transaction)
            throws FileNotFoundException, IOException, HttpHeaderParseException, ParseException {
         StringBuffer buf = new StringBuffer();

         buf.append(request.getMethod() + " " + request.getRequestURI());
         // Append query String if present.
         String queryString = request.getQueryString();
         if (StringUtils.isNotBlank(queryString))
            buf.append("?").append(queryString);
         buf.append(" ").append(request.getProtocol()).append(nl);
         for (Enumeration<String> en=request.getHeaderNames(); en.hasMoreElements(); ) {
            String name = en.nextElement();
            String value = request.getHeader(name);
            headers.put(name.toLowerCase(), value);
            buf.append(name).append(": ").append(value).append(nl);
         }
//         String ctype = headers.get("content-type");
//         if (ctype == null || ctype.equals(""))
//            throw new IOException("Content-Type header not found");
//         contentTypeHeader = new HttpHeader("content-type: " + ctype);
//         bodyCharset = contentTypeHeader.getParam("charset");
//         contentType = contentTypeHeader.getValue();
//
//         if (bodyCharset == null || bodyCharset.equals(""))
//            bodyCharset = "UTF-8";
//
//         buf.append(nl);

         db.putRequestHeaderFile(buf.toString().getBytes());

         db.putRequestBodyFile(Io.getBytesFromInputStream(request.getInputStream()));

      }
   
   /**
    * Generate simple format message to return on invalid URIs
    * @param request received
    * @return message
    */
   private String getFormat(HttpServletRequest request) {
      String [] parts = request.getRequestURI().toLowerCase().split("\\/");
      String name = (parts.length < 2) ? "" : parts[1];
      return " - Endpoint format is http://" + request.getLocalName() + ":" + 
      request.getLocalPort() + "/" + name + "/httpsim/simid/actor/transaction " +
      "where simid, actor and transaction are variables for simulators. ";
   }

}
