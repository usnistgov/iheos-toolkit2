package gov.nist.toolkit.registrymetadata.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Uids implements IsSerializable {
	public List<Uid> uids;
	
	public Uids() {
		uids = new ArrayList<Uid>();
	}
	
	public Uids(AnyIds aids) {
		uids = new ArrayList<Uid>();
		for (AnyId aid : aids.ids) {
			uids.add(new Uid(aid));
		}
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		for (Uid uid : uids) {
			buf.append(uid.toString()).append("\n");
		}
		
		return buf.toString();
	}
	
	public Map<String, Uids> organizeByRepository() {
		Map<String, Uids> org = new HashMap<String, Uids>();

		for (Uid uid : uids) {
			String repuid = uid.repositoryUniqueId;
			if (repuid == null || repuid.equals(""))
				repuid = "none";
			Uids repUids = org.get(repuid);
			if (repUids == null) {
				repUids = new Uids();
				org.put(repuid, repUids);
			}
			repUids.uids.add(uid);
		}

		return org;
	}
	
	public void add(Uid uid) {
		uids.add(uid);
	}


}
