package gov.nist.direct.utils;

import java.util.ArrayList;

public class ValidationSummary {
	
	class SummaryEntry {
		String key;
		boolean value;
	}
	
	
	private ArrayList<SummaryEntry> encryptedMessageSummaryList;
	private ArrayList<SummaryEntry> decryptedMessageSummaryList;
	
	public ValidationSummary() {
		encryptedMessageSummaryList = new ArrayList<ValidationSummary.SummaryEntry>();
		decryptedMessageSummaryList = new ArrayList<ValidationSummary.SummaryEntry>();
	}
	
	public void recordKey(String key, boolean value, boolean encrypted) {
		SummaryEntry sumEntry = new SummaryEntry();
		sumEntry.key = key;
		sumEntry.value = value;
		if(encrypted) {
			encryptedMessageSummaryList.add(sumEntry);
		} else {
			decryptedMessageSummaryList.add(sumEntry);
		}
	}
	
	public void updateInfos(String key, boolean value, boolean encrypted) {
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
	
	public int findIndex(String key, ArrayList<SummaryEntry> summary) {
		for(int i=0;i<summary.size();i++) {
			if(summary.get(i).equals(key)) {
				return i;
			}
		}
		return -1;
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
		return res;
	}
	
	
	
}
