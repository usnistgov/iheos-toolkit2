package gov.nist.toolkit.registrymetadata.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AnyIds implements IsSerializable {
	public List<AnyId> ids= new ArrayList<AnyId>();

	public AnyIds() {
	}
	
	public AnyIds(AnyId id) {
		ids.add(id);
	}

	public AnyIds(ObjectRefs orefs) {
		for (ObjectRef or : orefs.objectRefs) {
			ids.add(new AnyId(or));
		}
	}
	
	public void labelAsLids() {
		for (AnyId aid : ids) {
			aid.isLid = true;
		}
	}

	public AnyIds(ObjectRef or) {
		ids.add(new AnyId(or));
	}

	public int size() {
		return ids.size();
	}

	public boolean isUUID() {
		if (ids.size() == 0)
			return false;
		return ids.get(0).isUUID();
	}

	public boolean isLid() {
		if (ids.size() == 0)
			return false;
		return ids.get(0).isLid();
	}

	public void add(AnyId id) {
		ids.add(id);
	}
	
	public String typeName() {
		if (ids.size() == 0)
			return "";
		return ids.get(0).typeName();
		
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("[");

		for (AnyId id : ids) {
			buf.append(id.toString());
		}

		buf.append("] ");
		
		buf.append(typeName());
		
		return buf.toString();
	}
	
	public AnyIds(Uids uids) {
		for (Uid uid : uids.uids) {
			ids.add(new AnyId(uid));
		}
	}

}
