package gov.nist.toolkit.testengine;

import gov.nist.toolkit.registrysupport.MetadataSupport;

public class TransactionStatus {
	
	static enum StatusValue { Success, Failure, Warning, PartialSuccess, UNKNOWN };
	
	StatusValue value = StatusValue.UNKNOWN;
	String fullValue = "";
	
	public TransactionStatus(String val) {
		
		fullValue = val;
		
		for (StatusValue sv : StatusValue.values()) {
			if (sv.toString().compareToIgnoreCase(val) == 0) { 
				value = sv;
				return;
			}
		}
		
		if (val.indexOf(':') > -1) {
			String[] parts = val.split(":");
			if (parts.length > 0) {
				String val1 = parts[parts.length-1];
				for (StatusValue sv : StatusValue.values()) {
					if (sv.toString().compareToIgnoreCase(val1) == 0) { 
						value = sv;
						return;
					}
				}
			}
		}
	}
	
	public TransactionStatus() {
		value = StatusValue.UNKNOWN;
	}
	
	public boolean isNamespaceOk() {
		return fullValue.startsWith(MetadataSupport.response_status_type_namespace);
	}
	
	public boolean equals(TransactionStatus ts) {
		return ts.value == value;
	}
	
	public String toString() {
		return value.toString();
	}
	
	public boolean isFailure() {
		return value == StatusValue.Failure;
	}
	
	public boolean isSuccess() {
		return value == StatusValue.Success;
	}
	
}
