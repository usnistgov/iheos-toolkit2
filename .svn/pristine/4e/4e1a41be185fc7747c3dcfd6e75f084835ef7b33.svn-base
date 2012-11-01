package gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab;

import gov.nist.toolkit.xdstools2.client.tabs.directRegistrationTab.Field.Color;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.FlexTable;

public class Builder {
	List<Field> fields = new ArrayList<Field>();
	FlexTable grid;
	
	public Builder(FlexTable grid) {
		this.grid = grid;
		grid.setCellSpacing(20);
	}

	public void add(Field f) {
		fields.add(f);
	}
	
	public Field getField(String id) {
		for (Field f : fields) {
			if (f.id.equals(id))
				return f;
		}
		return null;  // can't happen
	}

	public boolean allfieldsPresent() {
		boolean ok = true;
		for (Field f : fields) {
			if (f.value() == null || f.value().equals("")) {
				ok = false;
				error(f);
			} else
				clear(f);
		}
		return ok;
	}

	public void setGrid(int row, int startCol, String id, Color color) {
		Field field = getField(id);
		field.row = row;
		field.col = startCol;
	}
	
	public void displayGrid() {
		for (Field f : fields) {
			displayGrid(f);
		}
	}
	
	public void displayGrid(Field f) {
		grid.setWidget(f.row, f.col, f.nameHtml);
		grid.setWidget(f.row, f.col+1, f.textBox);
	}
	
	public void error(Field f) {
		f.error();
		displayGrid(f);
	}
	
	public void clear(Field f) {
		f.clear();
		displayGrid(f);
	}
}
