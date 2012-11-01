package gov.nist.toolkit.registrymetadata.client;


import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;


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
}
