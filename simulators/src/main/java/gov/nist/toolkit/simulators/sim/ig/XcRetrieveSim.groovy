package gov.nist.toolkit.simulators.sim.ig
import gov.nist.toolkit.actorfactory.SimManager
import gov.nist.toolkit.configDatatypes.SimulatorProperties
import gov.nist.toolkit.actorfactory.client.SimulatorConfig
import gov.nist.toolkit.configDatatypes.client.TransactionType
import gov.nist.toolkit.errorrecording.ErrorRecorder
import gov.nist.toolkit.errorrecording.client.XdsErrorCode
import gov.nist.toolkit.registrymsg.repository.*
import gov.nist.toolkit.simulators.support.DsSimCommon
import gov.nist.toolkit.simulators.support.SimCommon
import gov.nist.toolkit.sitemanagement.Sites
import gov.nist.toolkit.sitemanagement.client.Site
import gov.nist.toolkit.soap.axis2.Soap
import gov.nist.toolkit.validatorsSoapMessage.message.SoapMessageValidator
import gov.nist.toolkit.valregmsg.registry.RetrieveMultipleResponse
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator
import gov.nist.toolkit.xdsexception.ExceptionUtil
import groovy.transform.TypeChecked
import org.apache.axiom.om.OMElement
import org.apache.log4j.Logger
/**
 *
 */
@TypeChecked
public class XcRetrieveSim extends AbstractMessageValidator {
    SimCommon common;
    DsSimCommon dsSimCommon;
    Exception startUpException = null;
    Logger logger = Logger.getLogger(XcRetrieveSim);
    boolean isSecure;
    boolean isAsync;
    SimulatorConfig asc;
    RetrieveMultipleResponse response;
    RetrievedDocumentsModel retrievedDocs = new RetrievedDocumentsModel();
    OMElement result = null;

    public XcRetrieveSim(SimCommon common, DsSimCommon dsSimCommon, SimulatorConfig asc) {
        super(common.vc);
        this.common = common;
        this.dsSimCommon = dsSimCommon;
        this.asc = asc;
        isSecure = common.isTls();
        isAsync = false;


        // build response????
        try {
            response = new RetrieveMultipleResponse();
        } catch (Exception e) {
            System.out.println(ExceptionUtil.exception_details(e));
            startUpException = e;
        }
    }

    class NonException extends Exception { }

    @Override
    public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
        this.er = er;
        er.registerValidator(this);

        if (startUpException != null) {
            er.err(XdsErrorCode.Code.XDSRegistryError, startUpException);
            throw new NonException()
        }

        try {
            // if request didn't validate, return so errors can be reported
            if (common.hasErrors()) {
                response.add(dsSimCommon.getRegistryErrorList(), null);
                throw new NonException();  // need to run finally code
            }

            SimManager simMgr = new SimManager("ignored");
            List<Site> sites = simMgr.getSites(asc.getConfigEle(SimulatorProperties.respondingGateways).asList());
            if (sites == null || sites.size() == 0) {
                er.err(XdsErrorCode.Code.XDSRepositoryError, "No RespondingGateways configured", this, null);
                throw new NonException();  // need to run finally code
            }
            Sites remoteSites = new Sites(sites);

            // Get body of request
            SoapMessageValidator smv = (SoapMessageValidator) common.getMessageValidatorIfAvailable(SoapMessageValidator.class);
            OMElement ahqr = smv.getMessageBody();
            RetrieveRequestParser requestParser = new RetrieveRequestParser(ahqr);
            RetrieveRequestModel requestModel = requestParser.getRequest();

            for (String homeId : requestModel.getHomeCommunityIds()) {
                Site site = remoteSites.getSiteForHome(homeId);

                if (site == null) {
                    er.err(XdsErrorCode.Code.XDSRepositoryError, "Don't have configuration for RG with homeCommunityId " + homeId, this, null);
                    throw new NonException();  // need to run finally code
                }

                RetrievedDocumentsModel retDocs = forwardRetrieve(site, requestModel.getItemsForCommunity(homeId));
                for (RetrievedDocumentModel item : retDocs.values()) {
                    logger.info("XC retrieve returned " + item)
                    retrievedDocs.add(item);
                }
            }

        } catch (NonException e) {
        } catch (Exception e) {
            logException(er, e);
        } finally {
            result = new RetrieveDocumentResponseGenerator(retrievedDocs, 
               dsSimCommon.registryErrorList).get();
            er.unRegisterValidator(this);
        }
    }

    public OMElement getResult() { return result }

    RetrievedDocumentsModel forwardRetrieve(Site site, List<RetrieveItemRequestModel> items) throws Exception {
        String endpoint = site.getEndpoint(TransactionType.XC_RETRIEVE, isSecure, isAsync);
        er.detail("Forwarding retrieve request to " + endpoint);

        RetrievedDocumentsModel models = retrieveCall(items, endpoint);
        validateXcRetrieveResponse(models)
        return models
    }

    RetrievedDocumentsModel retrieveCall(List<RetrieveItemRequestModel> items, String endpoint) throws Exception {
        OMElement request = new RetrieveRequestGenerator(items).get();
        Soap soap = new Soap();
        soap.setAsync(false);
        soap.setUseSaml(false);

        try {
            soap.soapCall(request,
                    endpoint,
                    true, //mtom
                    true,  // WS-Addressing
                    true,  // SOAP 1.2
                    "urn:ihe:iti:2007:CrossGatewayRetrieve",
                    "urn:ihe:iti:2007:CrossGatewayRetrieveResponse"
            );
        } catch (Exception e) {
            Exception e2 = new Exception("Soap Call to endpoint " + endpoint + " failed - " + e.getMessage(), e);
            logException(er, e2)
            throw e2
        }
        OMElement result = soap.getResult();

        return new RetrieveResponseParser(result).get();
    }

    void validateXcRetrieveResponse(RetrievedDocumentsModel models) {
        models.values().each { RetrievedDocumentModel model ->
            model.with {
                if (!docUid) er.err(XdsErrorCode.Code.XDSRegistryMetadataError, 'Responding Gateway returned no Document.uniqueId', this, null)
                if (!repUid) er.err(XdsErrorCode.Code.XDSRegistryMetadataError, 'Responding Gateway returned no repositoryUniqueId', this, null)
                if (!content_type) er.err(XdsErrorCode.Code.XDSRegistryMetadataError, 'Responding Gateway returned no mimeType', this, null)
                if (!home) er.err(XdsErrorCode.Code.XDSRegistryMetadataError, 'Responding Gateway returned no homeCommunityId', this, null)
            }
        }
    }


    private void logException(ErrorRecorder er, Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.equals(""))
            msg = ExceptionUtil.exception_details(e);
        logger.error(msg);
        er.err(XdsErrorCode.Code.XDSRepositoryError, msg, this, null);
    }

}
