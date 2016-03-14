package gov.nist.toolkit.registrymetadata.client;


import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.List;


public class ObjectRefs implements IsSerializable  {
	public List<ObjectRef> objectRefs = new ArrayList<ObjectRef>();
	
	public ObjectRefs() {
	}
	
	public ObjectRefs(AnyIds aids) {
		for (AnyId aid : aids.ids) {
			objectRefs.add(new ObjectRef(aid));
		}
	}

	public ObjectRefs(ObjectRef ref) {
		objectRefs.add(ref);
	}

	public String toString() {
		StringBuilder buf = new StringBuilder();

		buf.append("[");
		int cnt = 0;
		for (ObjectRef o : objectRefs) {
			if (cnt > 0) buf.append(", ");
			buf.append(o.displayName());
			cnt++;
		}
		buf.append("]");

		return buf.toString();
	}
}
