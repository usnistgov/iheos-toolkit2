package gov.nist.toolkit.simcommon.client.config;

import com.google.gwt.user.client.rpc.IsSerializable;
import gov.nist.toolkit.actortransaction.client.ParamType;
import gov.nist.toolkit.configDatatypes.client.TransactionType;
import gov.nist.toolkit.configDatatypes.client.PatientErrorMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
	public TransactionType transType = null;

	// cannot use Object class - will not serialize so tricks are necessary
	public enum ValueType implements IsSerializable { BOOLEAN, STRING , SINGLE_SELECT_LIST, MULTI_SELECT_LIST, PATIENT_ERROR_MAP};
	ValueType valueType = ValueType.STRING;
	boolean booleanValue = false;
	String  stringValue = "";
    List<String> listValue = new ArrayList<>();
    PatientErrorMap patientErrorMap = new PatientErrorMap();
    String extraValue;

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

    public SimulatorConfigElement(String name, ParamType type, List<String> values, boolean isMultiSelect) {
        this.name = name;
        this.type = type;
        setValue(values, ((isMultiSelect) ? ValueType.MULTI_SELECT_LIST : ValueType.SINGLE_SELECT_LIST));
    }

    public SimulatorConfigElement(String name, ParamType type, PatientErrorMap value) {
        this.name = name;
        this.type = type;
        setValue(value);
    }

    public String getExtraValue() {
        return extraValue;
    }

    public void setExtraValue(String extraValue) {
        this.extraValue = extraValue;
    }

    public boolean isEditable() { return editable; }
	public void setEditable(boolean v) { editable = v; }

    public PatientErrorMap asPatientErrorMap() {
        return patientErrorMap;
    }

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

    public List<String> asList() { return listValue; }

	public boolean isBoolean() { return valueType == ValueType.BOOLEAN;  }
	public boolean isString() { return valueType == ValueType.STRING;  }
    public boolean isSingleList() { return valueType == ValueType.SINGLE_SELECT_LIST; }
    public boolean isMultiList() { return valueType == ValueType.MULTI_SELECT_LIST; }
    public boolean isList() { return isSingleList() || isMultiList(); }
    public boolean isPatientErrorMap() { return valueType == ValueType.PATIENT_ERROR_MAP; }

	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("name=").append(name);
		buf.append(" type=").append(type);
		buf.append(" transType=").append(transType);
		if (valueType == ValueType.BOOLEAN)
			buf.append(" boolean value=").append(booleanValue);
        else if (valueType == ValueType.MULTI_SELECT_LIST)
            buf.append(" multiSelectList=").append(listValue);
        else if (valueType == ValueType.SINGLE_SELECT_LIST)
            buf.append(" singleSelectList=").append(listValue);
        else if (valueType == ValueType.PATIENT_ERROR_MAP)
            buf.append(" patientErrorList=").append(patientErrorMap);
		else
			buf.append(" string value=").append(stringValue);

//		buf.append(" values=").append(values);

		buf.append(" editable=").append(isEditable());

		return buf.toString();
	}

	public void setValue(Boolean o) { booleanValue = o; valueType = ValueType.BOOLEAN; }
	public void setValue(String o) { stringValue = o; valueType = ValueType.STRING; }
    public void setValue(List<String> o, ValueType valueType) { listValue = o; this.valueType = valueType; }
    public void setValue(List<String> o) { listValue = o; }
    public void setValue(PatientErrorMap o) { patientErrorMap = o; valueType = ValueType.PATIENT_ERROR_MAP; }

}