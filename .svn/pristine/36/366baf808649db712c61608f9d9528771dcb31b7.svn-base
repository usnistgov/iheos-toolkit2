package gov.nist.toolkit.valregmetadata.field;


public class Validator {
//	RegistryErrorList rel;
//	Metadata m;
//	boolean is_submit;
//	boolean is_xdsb;
//	boolean is_xdm;
//	Structure s;
//	Attribute a;
//	CodeValidation cv;
//	PatientId pid;
//	UniqueId uid;
//	List<String> assigning_authorities;
//	LogMessage log_message;
//	boolean isPnR = false;
//
//	public Validator(Metadata m, RegistryErrorList rel, boolean is_submit, boolean is_xdsb, LogMessage log_message, boolean isPnR) throws LoggerException, XdsException {
//		this.rel = rel;
//		this.m = m;
//		this.is_submit = is_submit;
//		this.is_xdsb = is_xdsb;
//		this.log_message = log_message;
//		this.isPnR = isPnR;
//		
//		s = new Structure(m, is_submit, rel, log_message);
//		a = new Attribute(m, is_submit, is_xdsb, rel, isPnR);
//		a.setIsXDM(is_xdm);
//		try {
//			cv = new CodeValidation(m, is_submit, is_xdsb, rel);
//		}
//		catch (XdsInternalException e) {
//			rel.add_error(MetadataSupport.XDSRegistryError, e.getMessage(), RegistryUtility.exception_details(e), null);
//			throw new XdsInternalException(e.getLocalizedMessage(), e);
//		}
//		assigning_authorities = cv.getAssigningAuthorities();
//
//		pid = new PatientId(m, rel, is_submit, is_xdsb);
//		uid = new UniqueId(m, rel, is_xdsb);
//	}
//	
//	public void setIsXDM(boolean isxdm) {
//		is_xdm = isxdm;
//	}
//	
//	public List<String> getAssigningAuthority() {
//		return this.assigning_authorities;
//	}
//
//
//	public void run() throws XdsInternalException, MetadataValidationException, LoggerException, XdsException {
//		a.setIsXDM(is_xdm);
//
//		try {
//			s.run();
//
//			a.run();
//
//			cv.run();
//		}
//		catch (XdsInternalException e) {
//			rel.add_error(MetadataSupport.XDSRegistryError, e.getMessage(), RegistryUtility.exception_details(e), null);
//		}
//		catch (MetadataException e) {
//			rel.add_error(MetadataSupport.XDSRegistryError, e.getMessage(), RegistryUtility.exception_details(e), null);
//		}
//
//		pid.run();
//
//		for (OMElement ele : m.getRegistryPackages()) 
//			validate_internal_classifications(ele);
//		for (OMElement ele : m.getExtrinsicObjects()) 
//			validate_internal_classifications(ele);
//
//		uid.run();
//
//		rel.getRegistryErrorList(); // forces output of validation report
//		//System.out.println("Metadata Validator Done");
//	}
//
//
//	// internal classifications must point to object that contains them
//
//	void validate_internal_classifications(OMElement e) throws MetadataValidationException, MetadataException {
//		String e_id = e.getAttributeValue(MetadataSupport.id_qname);
//		if (e_id == null || e_id.equals(""))
//			return;
//		for (Iterator it=e.getChildElements(); it.hasNext(); ) {
//			OMElement child = (OMElement) it.next();
//			OMAttribute classified_object_att = child.getAttribute(MetadataSupport.classified_object_qname);
//			if (classified_object_att != null) {
//				String value = classified_object_att.getAttributeValue();
//				if ( !e_id.equals(value)) {
//					throw new MetadataValidationException("Classification " + m.getIdentifyingString(child) + 
//							"\n   is nested inside " + m.getIdentifyingString(e) +
//							"\n   but classifies object " + m.getIdentifyingString(value));
//				}
//			}
//		}
//	}
//
//	void val(String topic, String msg ) {
//		if (msg == null) msg = "Ok";
//		rel.add_validation(topic, msg, "Validator.java");
//	}
//
//
//
//	void err(String msg) {
//		rel.add_error(MetadataSupport.XDSRegistryMetadataError, msg, "Validator.java", null);
//	}
}
