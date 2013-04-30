package gov.nist.direct.utils;

import gov.nist.toolkit.errorrecording.ErrorRecorder;

import java.util.ArrayList;

public class ValidationSummary {
	
	public enum Status {VALID, ERROR, PART};
	
	class SummaryEntry {
		String key;
		Status value;
	}
	
	
	private ArrayList<SummaryEntry> encryptedMessageSummaryList;
	private ArrayList<SummaryEntry> decryptedMessageSummaryList;
	private SummaryEntry signatureStatus;
	
	public ValidationSummary() {
		encryptedMessageSummaryList = new ArrayList<ValidationSummary.SummaryEntry>();
		decryptedMessageSummaryList = new ArrayList<ValidationSummary.SummaryEntry>();
		signatureStatus = new SummaryEntry();
		signatureStatus.key = "-----Signature";
		signatureStatus.value = Status.VALID;
	}
	
	public void recordKey(String key, Status value, boolean encrypted) {
		SummaryEntry sumEntry = new SummaryEntry();
		sumEntry.key = key;
		sumEntry.value = value;
		if(encrypted) {
			encryptedMessageSummaryList.add(sumEntry);
		} else {
			decryptedMessageSummaryList.add(sumEntry);
		}
	}
	
	public void recordKey(String key, boolean hasError, boolean encrypted) {
		SummaryEntry sumEntry = new SummaryEntry();
		sumEntry.key = key;
		if(hasError) {
			sumEntry.value = Status.ERROR;
		} else {
			sumEntry.value = Status.VALID;
		}
		if(encrypted) {
			encryptedMessageSummaryList.add(sumEntry);
		} else {
			decryptedMessageSummaryList.add(sumEntry);
		}
	}
	
	public void updateInfos(String key, Status value, boolean encrypted) {
		if(encrypted) {
			int index = findIndex(key, encryptedMessageSummaryList);
			if(index != -1) {
				encryptedMessageSummaryList.get(index).value = value;
			}
		} else {
			int index = findIndex(key, decryptedMessageSummaryList);
			if(index != -1) {
				decryptedMessageSummaryList.get(index).value = value;
			}
		}
	}
	
	public void updateInfos(String key, boolean hasErrors, boolean encrypted) {
		if(encrypted) {
			int index = findIndex(key, encryptedMessageSummaryList);
			if(index != -1) {
				if(hasErrors) {
					encryptedMessageSummaryList.get(index).value = Status.ERROR;
				}
			}
		} else {
			int index = findIndex(key, decryptedMessageSummaryList);
			if(index != -1) {
				if(hasErrors) {
					decryptedMessageSummaryList.get(index).value = Status.ERROR;
				} else {
					decryptedMessageSummaryList.get(index).value = Status.VALID;
				}
			}
		}
	}
	
	public int findIndex(String key, ArrayList<SummaryEntry> summary) {
		for(int i=0;i<summary.size();i++) {
			if(summary.get(i).key.equals(key)) {
				return i;
			}
		}
		return -1;
	}
	
	public void updateSignatureStatus(boolean status) {
		if(status) {
			signatureStatus.value = Status.ERROR;
		}
	}
	
	public String toString() {
		String res = "";
		// Encrypted Message Headers
		for(int i=0;i<encryptedMessageSummaryList.size();i++) {
			res += encryptedMessageSummaryList.get(i).key + ": " + encryptedMessageSummaryList.get(i).value + "\n";
		}
		// Decrypted Message Headers
		for(int i=0;i<decryptedMessageSummaryList.size();i++) {
			res += decryptedMessageSummaryList.get(i).key + ": " + decryptedMessageSummaryList.get(i).value + "\n";
		}
		// Signature
		res += signatureStatus.key + ": " + signatureStatus.value + "\n";
		return res;
	}
	
	public void writeErrorRecorder(ErrorRecorder er) {
		// Encrypted Message Headers
		for(int i=0;i<encryptedMessageSummaryList.size();i++) {
			switch(encryptedMessageSummaryList.get(i).value) {
			case VALID:
				er.detail(encryptedMessageSummaryList.get(i).key);
				break;
			case ERROR:
				er.err("", encryptedMessageSummaryList.get(i).key, "", "", "");
				break;
			case PART:
				er.sectionHeading(encryptedMessageSummaryList.get(i).key);
				break;
			default:
				break;
			}
		}
		
		// Decrypted Message Headers
		for(int i=0;i<decryptedMessageSummaryList.size();i++) {
			switch(decryptedMessageSummaryList.get(i).value) {
			case VALID:
				er.detail(decryptedMessageSummaryList.get(i).key);
				break;
			case ERROR:
				er.err("", decryptedMessageSummaryList.get(i).key, "", "", "");
				break;
			case PART:
				er.sectionHeading(decryptedMessageSummaryList.get(i).key);
				break;
			default:
				break;
			}
		}
		
		// Signature
		switch(signatureStatus.value) {
		case VALID:
			er.detail(signatureStatus.key);
			break;
		case ERROR:
			er.err("", signatureStatus.key, "", "", "");
			break;
		default:
			break;
		}
		
	}
	
	
	
}
