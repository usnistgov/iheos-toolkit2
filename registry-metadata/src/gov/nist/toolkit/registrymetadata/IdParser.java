package gov.nist.toolkit.registrymetadata;

import gov.nist.toolkit.registrysupport.MetadataSupport;
import gov.nist.toolkit.xdsexception.XdsInternalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;

public class IdParser {
	Metadata m;
	// symbolic compiler
	List<OMAttribute> referencingAttributes = new ArrayList<OMAttribute>();
	List<OMAttribute> identifyingAttributes = new ArrayList<OMAttribute>();
	List<OMAttribute> symbolicIdReplacements = null;
	Map<String, String> assignedUuids = null;

	public IdParser(Metadata m) {
		this.m = m;
		parse(true);
	}

	// compile only one id
	public IdParser(Metadata m, OMAttribute focusIdentifyingAttribute) {
		this.m = m;
		identifyingAttributes.add(focusIdentifyingAttribute);
		parse(false);
	}

	public List<String> getDefinedIds() {
		List<String> defined = new ArrayList<String>();

		for (Iterator<OMAttribute> it=this.identifyingAttributes.iterator(); it.hasNext(); ) {
			OMAttribute attr = it.next();
			String id = attr.getAttributeValue();

			if ( !defined.contains(id))
				defined.add(id);
		}

		return defined;
	}

	public List<String> getReferencedIds() {
		List<String> refer = new ArrayList<String>();
		for (Iterator<OMAttribute> it=referencingAttributes.iterator(); it.hasNext(); ) {
			OMAttribute attr =  it.next();
			String id = attr.getAttributeValue();

			if ( !refer.contains(id))
				refer.add(id);
		}

		return refer;
	}

	public List<String> getUndefinedIds() {
		List<String> referenced = getReferencedIds();
		List<String> defined = getDefinedIds();
		List<String> undefined = new ArrayList<String>();

		for (Iterator<String> it=referenced.iterator(); it.hasNext(); ) {
			String id = it.next();

			if ( ! defined.contains(id))
				undefined.add(id);

		}

		return undefined;
	}

	void parse(boolean scanIds) {
		List<OMElement> allObjects = m.getAllObjects();

		for (int i=0; i<allObjects.size(); i++) {
			OMElement obj = allObjects.get(i);
			parse(obj, scanIds);
		}
	}
	
	QName[] referencingAttributeNames =
	{
			MetadataSupport.classified_object_qname,
			MetadataSupport.source_object_qname,
			MetadataSupport.target_object_qname,
			MetadataSupport.registry_object_qname
			
	};

	/**
	 * Build identifyingAttributes (id attributes) and symbolic referencing attributes (atts making reference with symbolic name)
	 * @param obj
	 */
	void parse(OMElement obj, boolean scanIds) {
		if (scanIds) {
			OMAttribute idAtt = obj.getAttribute(MetadataSupport.id_qname);
			if (idAtt != null)
				identifyingAttributes.add(idAtt);
		}
		
		for (QName attname : referencingAttributeNames) {
			OMAttribute a = obj.getAttribute(attname);
			if (a == null)
				continue;
			// saving off only those attributes with symbolic values
			if (a.getAttributeValue().startsWith("urn:uuid:"))
				continue;
			referencingAttributes.add(a);
			
		}

		for (Iterator it1=obj.getChildElements(); it1.hasNext(); ) {
			OMElement objI = (OMElement) it1.next();
			parse(objI, scanIds);
		}
	}
		
	
	List<String> submittedUUIDs;
	
	public List<String> getSubmittedUUIDs() {
		return submittedUUIDs;
	}

	/*
	 * Symbol Compiler
	 */
	public Map<String, String> compileSymbolicNamesIntoUuids () throws XdsInternalException {

		// make list of all symbolic names used in metadata
		// allocate UUID for these names
		// update attributes that define these symbolic names with UUIDs
		List<String> symbolicNames = new ArrayList<String>();
		List<String> uuids = new ArrayList<String>();
		assignedUuids = new HashMap<String, String>();
		submittedUUIDs = new ArrayList<String>();
		for (int i=0; i<identifyingAttributes.size(); i++) {
			OMAttribute att = (OMAttribute) identifyingAttributes.get(i);
			String name = att.getAttributeValue();
			if (name.startsWith("urn:uuid:")) {
				submittedUUIDs.add(name);
				continue;
			}
			symbolicNames.add(name);
			String uuid = UuidAllocator.allocate();
			uuids.add(uuid);    // can index uuids like symbolic_names
			att.setAttributeValue(uuid);
			assignedUuids.put(name, uuid);
		}

		// update all references to objects that we just allocated uuids for
		for (int i=0; i<referencingAttributes.size(); i++) {
			OMAttribute att = (OMAttribute) referencingAttributes.get(i);
			String symbolicName = att.getAttributeValue();
			if (symbolicName.startsWith("urn:uuid:"))
				continue;
			int idIndex = symbolicNames.indexOf(symbolicName);
			if (idIndex == -1)
				throw new XdsInternalException("Metadata:compileSymbolicNamesIntoUuids(): cannot find symbolic name " + symbolicName + " in tables");
			String uuid = (String) uuids.get(idIndex);
			att.setAttributeValue(uuid);
		}

		return assignedUuids;
	}

	public Map<String, String> getSymbolicNameUuidMap() {
		return assignedUuids;
	}

	public OMElement getApproveObjectsRequest(List uuids) {
		OMElement req = MetadataSupport.om_factory.createOMElement("ApproveObjectsRequest", null);
		req.addChild(mk_object_ref_list(uuids));
		return req;
	}

	public OMElement getDeprecateObjectsRequest(List uuids) {
		OMElement req = MetadataSupport.om_factory.createOMElement("DeprecateObjectsRequest", null);
		req.addChild(mk_object_ref_list(uuids));
		return req;

	}

	private OMElement mk_object_ref_list(List uuids) {
		OMElement object_ref_list = MetadataSupport.om_factory.createOMElement("ObjectRefList", null);
		for (Iterator it=uuids.iterator(); it.hasNext(); ) {
			String uuid = (String) it.next();
			OMAttribute att = MetadataSupport.om_factory.createOMAttribute("id", null, uuid);
			OMElement object_ref = MetadataSupport.om_factory.createOMElement("ObjectRef", null);
			object_ref.addAttribute(att);
			object_ref_list.addChild(object_ref);
		}
		return object_ref_list;
	}

	/*
	 * Registry adaptor - approve objects
	 */

	List<OMElement> approveable_objects(Metadata m) {
		List<OMElement> o = new ArrayList<OMElement>();
		o.addAll(m.getExtrinsicObjects());
		o.addAll(m.getRegistryPackages());
		return o;
	}

	public List<String> approvable_object_ids(Metadata m) {
		return m.getObjectIds(approveable_objects(m));
	}

	boolean is_approveable_object(Metadata m, OMElement o) {
		if (m.getExtrinsicObjects().contains(o))
			return true;
		if (m.getRegistryPackages().contains(o))
			return true;
		return false;
	}



}
