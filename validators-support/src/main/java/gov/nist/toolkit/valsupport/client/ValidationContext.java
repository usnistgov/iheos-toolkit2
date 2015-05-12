package gov.nist.toolkit.valsupport.client;


import gov.nist.toolkit.commondatatypes.client.MetadataTypes;
import gov.nist.toolkit.commondatatypes.client.SchematronMetadataTypes;
import gov.nist.toolkit.errorrecording.client.XdsErrorCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * Collection of flags and logic to record needed validation steps. If validation
 * steps are discovered along the way then this keeps track of what was run.
 * @author bill
 *
 */
public class ValidationContext  implements Serializable, IsSerializable {

	String codesFilename = null;

	// These flags control the validation


	public boolean xds_b     = false;

	// primary transaction selection
	public boolean isR       = false;
	public boolean isPnR     = false;
	public boolean isRet	 = false;
	public boolean isXDR	 = false;
	public boolean isXDRLimited = false;   // IHE version
	public boolean isXDRMinimal = false;   // Direct version
	public boolean isXDM     = false;
	public boolean isSQ      = false;
	public boolean isMU      = false;
	public boolean isDIRECT  = false;
	public boolean isCCDA	 = false;

	// is Cross Community - a modifier on other settings
	public boolean isXC      = false;

	// if neither set then context is not known
	public boolean isRequest = false;
	public boolean isResponse= false;

	public boolean isAsync = false;
	public boolean isMultiPatient = false;
	//		public boolean minMeta = false;

	public boolean skipInternalStructure = false;
	public boolean updateable = true;

	public boolean isEpsos = false;
	//NHIN xcpd
	public boolean isXcpd = false;
	public boolean isNwHINxcpd = false;
	public boolean isC32 = false;
	//E-Priscription ncpdp/
	public boolean isNcpdp = false;

	//
	// State maintained by various validators
	//

	// this is set by MtomMessageValidator and SimpleSoapMessageValidator
	// to record what was actually found in message
	public boolean isSimpleSoap = false;

	// this is not a 'type' but instead and indicator that
	// a SOAP wrapper either is present or must be present
	// in validation
	public boolean hasSoap = false;
	public boolean hasSaml = false ;
	public boolean hasHttp = false;

	// a bad place to keep this status
	public boolean updateEnabled = false;

	public boolean leafClassWithDocumentOk = false;
	
	public String ccdaType = null;
	public byte[] privKey = null;
	public String privKeyPassword = "";

	public enum MetadataPattern { UpdateDocumentEntry, UpdateDocumentEntryStatus };

	public List<MetadataPattern> metadataPatterns = new ArrayList<MetadataPattern>();
	
	// Since content can be nested (CCDA inside XDM inside ...) the context(s) for validation
	// must also be nested.
	// In the current code, a CCDA nested inside a Direct message is coded as isCCDA = true and ccdaType = ???
	// In time this should be converted to:
	//  VC(Direct)
	//    VC(CCDA, ccdaType = ???)
	// This will allow:
	//   VC(Direct)
	//     VC(XDM)
	//       VC(CCDA, ccdaType = ???)
	// which parsed as a CCDA of type ??? nested in an XDM included as a part of a Direct message.
	// This is a list instead of a single element since we may have the need to validate a Direct
	// message (or XDM) a plain text attachment AND a CCDA attachment.
	// For now the only containers (formats that contain other formats) are Direct messages
	// and XDM.  Provide & Register (XDS or XDR) could be considered a container as well. So
	// far there is no requirement to deal with content validation in those areas.
	List<ValidationContext> innerContexts = new ArrayList<ValidationContext>();

	public void addInnerContext(ValidationContext ivc) {
		innerContexts.add(ivc);
	}
	
	public int getInnerContextCount() { 
		return innerContexts.size();
	}
	
	public ValidationContext getInnerContext(int i) {
		if (i < innerContexts.size())
			return innerContexts.get(i);
		return null;
	}
	
	//
	// End state maintained by various validators
	//

	public void addMetadataPattern(MetadataPattern pattern) {
		metadataPatterns.add(pattern);
	}

	public boolean hasMetadataPattern(String patternString) {
		for (MetadataPattern pat : metadataPatterns) {
			if (patternString.equalsIgnoreCase(pat.toString())) 
				return true;
		}
		return false;
	}

	public boolean requiresSimpleSoap() {
		return isR || isMU || (isSQ && !isEpsos);
	}

	public XdsErrorCode.Code getBasicErrorCode() {
		if (requiresMtom())
			return XdsErrorCode.Code.XDSRepositoryError;
		return XdsErrorCode.Code.XDSRegistryError;
	}

	public boolean requiresMtom() {
		return isPnR || isRet || isXDR || (isSQ && isEpsos);
	}

	public boolean containsDocuments() {
		if (isPnR && isRequest) return true;
		if (isXDR && isRequest) return true;
		if (isRet && isResponse) return true;
		return false;
	}

	public boolean equals(ValidationContext v) {
		return
				updateEnabled == v.updateEnabled &&
				isMU == v.isMU &&
				isR == v.isR &&
				isPnR == v.isPnR &&
				isRet == v.isRet &&
				//			isXDR == v.isXDR &&     // not sure how this needs to work
				isDIRECT == v.isDIRECT &&
				isCCDA == v.isCCDA &&
				isSQ == v.isSQ &&
				isXC == v.isXC &&
				isRequest == v.isRequest &&
				isResponse == v.isResponse &&
				isAsync == v.isAsync &&
				isMultiPatient == v.isMultiPatient &&
				isEpsos == v.isEpsos &&
				//NHIN xcpd and C32
				isXcpd == v.isXcpd &&
				isNwHINxcpd == v.isNwHINxcpd &&
				isC32 == v.isC32 &&
				//			minMeta == v.minMeta &&
				leafClassWithDocumentOk == v.leafClassWithDocumentOk &&
				isNcpdp == v.isNcpdp &&
				((ccdaType == null) ?  v.ccdaType == null   :  ccdaType.equals(v.ccdaType))   
				;
	}

	public void clone(ValidationContext v) {
		hasSoap = v.hasSoap;
		hasSaml = v.hasSaml;
		hasHttp = v.hasHttp;

		xds_b = v.xds_b;

		isMU = v.isMU;
		updateEnabled = v.updateEnabled;
		//			minMeta = v.minMeta;

		isR = v.isR;
		isPnR = v.isPnR;
		isRet = v.isRet;
		isXDR = v.isXDR;
		isXDRLimited = v.isXDRLimited;
		isXDRMinimal =  v.isXDRMinimal;
		isXDM = v.isXDM;
		isSQ  = v.isSQ;
		isDIRECT = v.isDIRECT;
		isCCDA = v.isCCDA;

		isXC  = v.isXC;

		isRequest = v.isRequest;
		isResponse = v.isResponse;

		isAsync = v.isAsync;
		isMultiPatient = v.isMultiPatient;

		skipInternalStructure = v.skipInternalStructure;
		updateable = v.updateable;
		isEpsos = v.isEpsos;
		//NHIN xcpd and C32
		isXcpd = v.isXcpd;
		isNwHINxcpd = v.isNwHINxcpd;
		isC32 = v.isC32;
		leafClassWithDocumentOk = v.leafClassWithDocumentOk;
		isNcpdp = v.isNcpdp;
		ccdaType = v.ccdaType;
	}

	public boolean hasMetadata() {
		if ((isR || isMU || isPnR || isXDR || isXDM) && isRequest) return true;
		if (isSQ && isResponse) return true;
		return false;
	}

	public String getTransactionName() {
		if (isPnR) {
			if (isRequest)
				return "ProvideAndRegister.b";
			if (isResponse)
				return "RegistryResponse";
		}
		if (isR) {
			if (isRequest)
				return "Register.b";
			if (isResponse)
				return "RegistryResponse";
		}
		if (isMU) {
			if (isRequest)
				return "Metadata Update";
			if (isResponse)
				return "RegistryResponse";
		}
		if (isRet) {
			if (isRequest) {
				if (isXC) {
					return "Cross Gateway Retrieve Request";
				} else {
					return "Retrieve Request";
				}
			}
			if (isResponse) {
				if (isXC)
					return "Cross Gateway Retrieve Response";
				else	
					return "Retrieve Response";
			}
		}
		if (isXDR) {
			if (isRequest)
				return "ProvideAndRegister.b";
			if (isResponse)
				;
		}
		if (isXDM) return "XDM";
		if (isSQ) {
			if (isXC) {
				if (isEpsos) {
					if (isRequest) 
						return "Epsos Cross Gateway Query Request";
					if (isResponse)
						return "Epsos Cross Gateway Query Response";
				} else {
					if (isRequest) 
						return "Cross Gateway Query Request";
					if (isResponse)
						return "Cross Gateway Query Response";
				}
			} else {
				if (isRequest) 
					return "Stored Query Request";
				if (isResponse)
					return "Stored Query Response";
			}
		}
		return "";
	}

	// NwNIN transactions
	public int getSchematronValidationType() {
		if (isXcpd) {
			if (isRequest) 
				return SchematronMetadataTypes.IHE_XCPD_305;
			if (isResponse) 
				return SchematronMetadataTypes.IHE_XCPD_306;
		}
		if(isNcpdp) {
			return SchematronMetadataTypes.NCPDP;
		}
		if (isNwHINxcpd) {
			if (isRequest) 
				return SchematronMetadataTypes.NwHINPD_305;
			if (isResponse) 
				return SchematronMetadataTypes.NwHINPD_306;
		}
		if (isC32) {
			return SchematronMetadataTypes.C32;
		}
		return SchematronMetadataTypes.METADATA_TYPE_UNKNOWN;
	}

	public String[] getSchematronValidationTypeName(int type) {
		return SchematronMetadataTypes.getSchematronMetadataTypeName(type);
	}
	public int getSchemaValidationType() {
		if (isPnR || isXDR) {
			if (isRequest)
				return MetadataTypes.METADATA_TYPE_PRb;
			if (isResponse)
				return MetadataTypes.METADATA_TYPE_REGISTRY_RESPONSE3;
		}
		if (isR || isMU) {
			if (isRequest)
				return MetadataTypes.METADATA_TYPE_Rb;
			if (isResponse)
				return MetadataTypes.METADATA_TYPE_REGISTRY_RESPONSE3;
		}
		if (isXDM)
			return MetadataTypes.METADATA_TYPE_Rb;
		if (isSQ && isRequest)
			return MetadataTypes.METADATA_TYPE_SQ;
		if (isSQ && isResponse)
			return MetadataTypes.METADATA_TYPE_SQ;
		if (isRet)
			return MetadataTypes.METADATA_TYPE_RET;
		return MetadataTypes.METADATA_TYPE_UNKNOWN;
	}

	public String getSchemaValidationTypeName(int type) {
		return MetadataTypes.getMetadataTypeName(type);
	}


	public String toString() {
		StringBuffer buf = new StringBuffer();

		if (isRequest) buf.append("Request");
		else if (isResponse) buf.append("Response");
		else buf.append("???");

		if (hasHttp) buf.append(";HTTP");
		if (hasSoap) buf.append(";SOAP");
		if (hasSaml) buf.append(";SAML");
		if (xds_b) buf.append(";xds.b");
		if (isR) buf.append(";R");
		if (isMU) buf.append(";MU");
		if (isPnR) buf.append(";PnR");
		if (isRet) buf.append(";Ret");
		if (isXDR) buf.append(";XDR");
		if (isXDM) buf.append(";XDM");
		if (isDIRECT) buf.append(";DIRECT");
		if (isCCDA) buf.append(";CCDA");
		if (isSQ) buf.append(";SQ");
		if (isXC) buf.append(";XC");
		if (isEpsos) buf.append(";Epsos");
		//NHIN xcpd and C32
		if (isXcpd) buf.append(";Xcpd");
		if (isNwHINxcpd) buf.append(";NwHINxcpd");
		if (isC32) buf.append(";C32");
		if (isNcpdp) buf.append(";Ncpdp");
		if (isAsync) buf.append(";Async");
		if (isMultiPatient) buf.append(";MultiPatient");
		if (skipInternalStructure) buf.append(";SkipInternalStructure");
		//			if (minMeta) buf.append(";MinMetadata");
		if (isXDRLimited) buf.append(";XDRLimited");
		if (isXDRMinimal) buf.append(";XDRMinimal");
		if (updateable)
			buf.append(";Updateable");
		else
			buf.append(";NotUpdateable");
		if (!metadataPatterns.isEmpty()) 
			buf.append(";MetadataPatterns:").append(metadataPatterns);
		buf.append(";CCDA type is " + ccdaType);
		
		if (innerContexts != null) {
			for (ValidationContext v : innerContexts) {
				buf.append("[").append(v.toString()).append("]");
			}
		}

		return buf.toString();
	}

	public boolean isTransactionKnown() {
		return isR || isMU || isPnR || isRet || isXDR || isXDM || isSQ;
	}

	public boolean isMessageTypeKnown() {
		return 
				(isTransactionKnown() && (isRequest || isResponse))
				|| isXDM || isXcpd || isNwHINxcpd || isC32 || isNcpdp || isDIRECT 
				|| isCCDA
				;
	}

	public boolean isValid() {
		return isMessageTypeKnown() || hasSoap;
	}

	public boolean isSubmit() {
		return isR || isMU || isPnR || isXDR || isXDM;
	}

	public boolean availabilityStatusRequired() {
		return isSQ && isResponse;
	}
	public boolean hashRequired() {
		if (isXDRMinimal) return false;
		if (isXDRLimited) return false;
		if (isXDR) return false;
		if (isXDM) return true;
		if (isPnR) return false;
		if (isR && isRequest) return true;
		if (isMU && isRequest) return true;
		if (isSQ && isResponse) return false;
		return true;
	}
	public boolean sizeRequired() {
		return hashRequired();
	}
	public boolean homeRequired() {
		return isXC && (isSQ || isRet) && isResponse 
				//			&& !minMeta
				;
	}
	public boolean repositoryUniqueIdRequired() {
		if (isXDM) return false;
		if (isR && isRequest) return true;
		if (isMU && isRequest) return true;
		if (isSQ && isResponse) return true;
		return false;
	}
	public boolean uriRequired() {
		return isXDM;
	}

	public void setCodesFilename(String filename) {
		this.codesFilename = filename;
	}

	public String getCodesFilename() {
		return codesFilename;
	}

}




