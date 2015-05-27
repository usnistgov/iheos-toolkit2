package gov.nist.toolkit.simcommon.client.config;

import gov.nist.toolkit.actortransaction.client.ATFactory;
import gov.nist.toolkit.actortransaction.client.ATFactory.ParamType;

import java.io.Serializable;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SimulatorConfigElement implements Serializable,IsSerializable {

	private static final long serialVersionUID = 1L;
	/**
	 * Parameter name
	 */
	public String name = null;
	/**
	 * Parameter type
	 */
	public ParamType type;
	public ATFactory.TransactionType transType = null;

	// cannot use Object class - will not serialize so tricks are necessary
	enum ValueType implements IsSerializable { BOOLEAN, STRING };
	ValueType valueType = ValueType.STRING;
	boolean booleanValue = false;
	String  stringValue = "";

	public List<String> values = null;
	boolean editable = false;
	
	public SimulatorConfigElement() {   }

	public SimulatorConfigElement(String name, ParamType type, Boolean value) {
		this.name = name;
		this.type = type;
		setValue(value);
	}

	public SimulatorConfigElement(String name, ParamType type, String value) {
		this.name = name;
		this.type = type;
		setValue(value);
	}

	public boolean isEditable() { return editable; }
	public void setEditable(boolean v) { editable = v; }

	public String asString() {
		if (valueType == ValueType.STRING)
			return stringValue;
		return Boolean.toString(false);
	}

	public Boolean asBoolean() { 
		if (valueType == ValueType.STRING) {
			String v = stringValue;
			v = v.toLowerCase();
			if (v.startsWith("t")) {
				booleanValue = Boolean.TRUE;
				valueType = ValueType.BOOLEAN;
			}
			else if (v.startsWith("f")) {
				booleanValue = Boolean.FALSE;
				valueType = ValueType.BOOLEAN;
			}
		}
		if (valueType == ValueType.BOOLEAN) return booleanValue;

		return false;
	}

	public boolean isBoolean() { return valueType == ValueType.BOOLEAN;  }
	public boolean isString() { return valueType == ValueType.STRING;  }

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("name=").append(name);
		buf.append(" type=").append(type);
		buf.append(" transType=").append(transType);
		if (valueType == ValueType.BOOLEAN)
			buf.append(" value=").append(booleanValue);
		else
			buf.append(" value=").append(stringValue);

		buf.append(" values=").append(values);

		buf.append(" editable=").append(isEditable());

		return buf.toString();
	}

	public void setValue(Boolean o) { booleanValue = o; valueType = ValueType.BOOLEAN; }
	public void setValue(String o) { stringValue = o; valueType = ValueType.STRING; }

}