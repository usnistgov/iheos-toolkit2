package gov.nist.toolkit.testengine.engine;

import gov.nist.toolkit.commondatatypes.MetadataSupport;

public class TransactionStatus {

	static enum StatusValue { Success, Failure, Warning, PartialSuccess, FAULT, UNKNOWN };

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
        if (value.equals(StatusValue.PartialSuccess) && fullValue.startsWith(MetadataSupport.ihe_response_status_type_namespace))
            return true;
		return fullValue.startsWith(MetadataSupport.response_status_type_namespace);
	}

	public boolean equals(TransactionStatus ts) {
		return ts.value == value;
	}

	public String toString() {
		return value.toString();
	}

	public boolean isFault() {
		return value == StatusValue.FAULT;
	}

	public boolean isFailure() {
		return value == StatusValue.Failure;
	}

	public boolean isSuccess() {
		return value == StatusValue.Success;
	}

	public boolean isPartialSuccess() {
		return value == StatusValue.PartialSuccess;
	}

	public String getNamespace() {
		if (!isPartialSuccess()) {
			return MetadataSupport.response_status_type_namespace + value.toString();
		} else if (isFault()) {
			return "Fault";
		} else {
			return MetadataSupport.ihe_response_status_type_namespace + value.toString();
		}
	}

}
