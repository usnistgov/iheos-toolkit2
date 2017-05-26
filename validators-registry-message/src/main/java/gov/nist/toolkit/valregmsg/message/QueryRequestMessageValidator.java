package gov.nist.toolkit.valregmsg.message;

import gov.nist.toolkit.docref.SqDocRef;
import gov.nist.toolkit.errorrecording.IErrorRecorder;
import gov.nist.toolkit.errorrecording.common.XdsErrorCode;
import gov.nist.toolkit.errorrecording.xml.assertions.Assertion;
import gov.nist.toolkit.errorrecording.xml.assertions.AssertionLibrary;
import gov.nist.toolkit.registrymetadata.Metadata;
import gov.nist.toolkit.commondatatypes.MetadataSupport;
import gov.nist.toolkit.registrysupport.logging.LoggerException;
import gov.nist.toolkit.utilities.xml.XmlUtil;
import gov.nist.toolkit.valregmetadata.object.RegistryObject;
import gov.nist.toolkit.valregmsg.registry.storedquery.generic.StoredQuery;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.ParamParser;
import gov.nist.toolkit.valregmsg.registry.storedquery.support.ParamParser.SlotParse;
import gov.nist.toolkit.valregmsg.registry.storedquery.validation.ValidationStoredQueryFactory;
import gov.nist.toolkit.valsupport.client.ValidationContext;
import gov.nist.toolkit.valsupport.engine.MessageValidatorEngine;
import gov.nist.toolkit.valsupport.message.AbstractMessageValidator;
import gov.nist.toolkit.xdsexception.MetadataValidationException;
import gov.nist.toolkit.xdsexception.XdsException;
import gov.nist.toolkit.xdsexception.XdsInternalException;
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
	private AssertionLibrary ASSERTIONLIBRARY = AssertionLibrary.getInstance();


	public void run(IErrorRecorder er, MessageValidatorEngine mvc) {
		this.er = er;
		//er.registerValidator(this);

		if (ahqr == null) {
			er.err(XdsErrorCode.Code.XDSRegistryError, "AdhocQueryRequest: top element null", this, "");
			//er.unRegisterValidator(this);
			return;
		}

		OMElement respOpt = XmlUtil.firstChildWithLocalName(ahqr, "ResponseOption");
		OMElement ahq = XmlUtil.firstChildWithLocalName(ahqr, "AdhocQuery");

		if (!"AdhocQueryRequest".equals(ahqr.getLocalName())) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA133");
			String detail = "Found instead: '" + ahqr.getLocalName() + "'";
			er.err(XdsErrorCode.Code.XDSRepositoryError, assertion, this, "", detail);
		}
		if (respOpt == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA134");
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
		}
		if (ahq == null) {
			Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA135");
			er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
		}
		String returnType = "";
		if (respOpt != null) {
			returnType = respOpt.getAttributeValue(MetadataSupport.return_type_qname);
			if (returnType == null || returnType.equals("")) {
				Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA136");
				er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
			} else {
				if (! (returnType.equals("LeafClass") || returnType.equals("ObjectRef"))) {
					if (!(vc.leafClassWithDocumentOk && returnType.equals("LeafClassWithRepositoryItem"))) {
						Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA137");
						er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", "");
					}
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
					//TODO Upgrade to Assertions
					er.err(XdsErrorCode.Code.XDSRegistryError, error, this, SqDocRef.Request_parms);
				}
				sps.add(sp);
			}


			ValidationStoredQueryFactory vsqf;
			try {
				vsqf = new ValidationStoredQueryFactory(ahqr, er);
				StoredQuery sq = vsqf.getImpl();

				if (sq == null) {
					Assertion assertion = ASSERTIONLIBRARY.getAssertion("TA138");
					String detail = "Query ID found: '" + queryId + "'";
					er.err(XdsErrorCode.Code.XDSRegistryError, assertion, this, "", detail);
					//er.unRegisterValidator(this);
					return;
				}

				sq.validateParameters();
				// TODO Upgrade to Assertions
			} catch (MetadataValidationException e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e.getMessage(), this, SqDocRef.Request_parms);
			} catch (LoggerException e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e.getMessage(), this, null);
			} catch (XdsException e) {
				er.err(XdsErrorCode.Code.XDSRegistryError, e.getMessage(), this, SqDocRef.Request_parms);
			}
			finally {
				//er.unRegisterValidator(this);
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
