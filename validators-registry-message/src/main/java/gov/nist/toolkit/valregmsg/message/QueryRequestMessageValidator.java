package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.errorrecording.ErrorRecorder;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmetadata.model.RegistryObject;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQuery;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.ParamParser;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.ParamParser.SlotParse;
import gov.nist.toolkit.valregmsg.registry.storedquery.validation.ValidationStoredQueryFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.xdsexception.client.MetadataValidationException;
import gov.nist.toolkit.xdsexception.client.XdsException;
import gov.nist.toolkit.xdsexception.client.XdsInternalException;
import org.apache.axiom.om.OMElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Validate a Query Request message.
 * @author bill
 *
 */
public class QueryRequestMessageValidator extends AbstractMessageValidator {
	OMElement ahqr;

	public void run(ErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		er.registerValidator(this);

		if (ahqr == null) {
			er.err(XdsErrorCode.Code.XDSRegistryError, "AdhocQueryRequest: top element null", this, "");
            er.unRegisterValidator(this);
			return;
		}

		OMElement respOpt = XmlUtil.firstChildWithLocalName(ahqr, "ResponseOption");
		OMElement ahq = XmlUtil.firstChildWithLocalName(ahqr, "AdhocQuery");

		if (!"AdhocQueryRequest".equals(ahqr.getLocalName()))
			er.err(XdsErrorCode.Code.XDSRegistryError, "Top level element must be AdhocQueryRequest - found instead " + ahqr.getLocalName(), this, "ebRS section 6.1");

		if (respOpt == null)
			er.err(XdsErrorCode.Code.XDSRegistryError, "AdhocQueryRequest: ResponseOption element missing", this, "ebRS section 6.1");

		if (ahq == null)
			er.err(XdsErrorCode.Code.XDSRegistryError, "AdhocQueryRequest: AdhocQuery element missing", this, "ebRS section 6.1");

		String returnType = "";
		if (respOpt != null) {
			returnType = respOpt.getAttributeValue(MetadataSupport.return_type_qname);
			if (returnType == null || returnType.equals("")) {
				er.err(XdsErrorCode.Code.XDSRegistryError, "AdhocQuery: returnType attribute missing or empty", this, "ebRS section 6.1");
			} else {
				if (! (returnType.equals("LeafClass") || returnType.equals("ObjectRef"))) {
					if (!(vc.leafClassWithDocumentOk && returnType.equals("LeafClassWithRepositoryItem")))
						er.err(XdsErrorCode.Code.XDSRegistryError, "AdhocQuery: returnType must be LeafClass or ObjectRef", this, SqDocRef.Return_type);
				}
			}
		}

		if (ahq != null) {
			String queryId = ahq.getAttributeValue(MetadataSupport.id_qname);
			String sqName = MetadataSupport.getSQName(queryId);

			er.detail("Query ID is " + queryId);
			er.detail("Query Name is " + sqName);

			List<SlotParse> sps = new ArrayList<SlotParse>();

			er.detail("Query Parameters are:");
			ParamParser parser = new ParamParser();
			for (OMElement slot : XmlUtil.childrenWithLocalName(ahq, "Slot")) {
				SlotParse sp = parser.parseSingleSlot(slot,true);
				er.detail(sp.name + " ==> " + sp.rawValues);
				er.detail(".    .    . yields values " + sp.values);
				for (String error : sp.errs) {
					er.err(XdsErrorCode.Code.XDSRegistryError, error, this, SqDocRef.Request_parms);
				}
				sps.add(sp);
			}


			ValidationStoredQueryFactory vsqf;
			try {
				vsqf = new ValidationStoredQueryFactory(ahqr, er);
				StoredQuery sq = vsqf.getImpl();

				if (sq == null) {
					er.err(XdsErrorCode.Code.XDSRegistryError, "Do not understand query [" + queryId + "]", this, SqDocRef.QueryID);
                    er.unRegisterValidator(this);
					return;
				}

				sq.validateParameters();
			} catch (MetadataValidationException e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e.getMessage(), this, SqDocRef.Request_parms);
			} catch (LoggerException e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e.getMessage(), this, null);
			} catch (XdsException e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e.getMessage(), this, SqDocRef.Request_parms);
			}
            finally {
                er.unRegisterValidator(this);
            }
		}

		try {
			new RegistryObject(new Metadata(), ahq);

		} catch (XdsInternalException e) {

		}


	}

	public QueryRequestMessageValidator(ValidationContext vc, OMElement xml) {
		super(vc);
		this.ahqr = xml;
	}

}
