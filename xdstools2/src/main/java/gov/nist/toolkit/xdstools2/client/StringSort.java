package gov.nist.toolkit.xdstools2.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StringSort {

	List<String> data1;
	
	public StringSort() {
		
	}

//	public List<String> sort(List<String> data) {
//		data1 = new ArrayList<String>();
//		data1.addAll(data);
//		
//		boolean changed = true;
//		while (changed)
//			changed = sort1();
//		
//		return data1;
//	}

	public List<String> sort(Collection<String> data) {
		data1 = new ArrayList<String>();
		data1.addAll(data);
		
		boolean changed = true;
		while (changed)
			changed = sort1();
		
		return data1;
	}

	boolean sort1() {
		boolean changed = false;
		for (int i=0; i<data1.size(); i++) {
			int j = i + 1;
			if (j >= data1.size())
				continue;
			String ref = data1.get(i);
			String comp = data1.get(j);
			if (comp.compareTo(ref) < 0) {
				// swap
				data1.set(i, comp);
				data1.set(j, ref);
				changed = true;
			}
		}
		return changed;
	}

}
