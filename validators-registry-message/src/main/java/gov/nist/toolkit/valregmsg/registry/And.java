package gov.nist.toolkit.valregmsg.registry;

import java.util.ArrayList;

public class And extends ArrayList {
		
	public String toString() {
		StringBuffer buf = new StringBuffer();
		
		if (size() == 0)
			buf.append("Empty");
		else {
			buf.append("{");
			buf.append(get(0).toString());
			for (int i=1; i<size(); i++) {
				buf.append(" AND ");
				buf.append(get(i).toString());
			}
			buf.append("}");
		}
		
		return buf.toString();
	}
}
