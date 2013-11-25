package gov.nist.toolkit.dst.cmd;

import java.util.List;

public class Selectable {
	List<String> items;

	public Selectable(List<String> items) { this.items = items; }

	public String forDisplay() {
		StringBuffer buf = new StringBuffer();
		int i = 0;
		for (String item : items) {
			buf.append(toAlpha(i)).append("    ").append(item).append("\n");
			i++;
		}
		return buf.toString();
	}

	// TODO Finish
	public int choose() {
		return 0;
	}

	char toAlpha(int n) {
		return (char) ('a' + n);
	}

	int toNum(char c) {
		return c - 'a';
	}
}
