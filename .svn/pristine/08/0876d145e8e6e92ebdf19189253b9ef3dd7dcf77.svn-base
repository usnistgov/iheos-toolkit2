package gov.nist.toolkit.valregmsg.xdm;

import java.util.ArrayList;
import java.util.List;

public class OMap {

	class OMapEntry {
		Path path;
		ByteArray data;
		
		OMapEntry(Path path, ByteArray data) {
			this.path = path;
			this.data = data;
		}
	}
	
	List<OMapEntry> map = new ArrayList<OMapEntry>();
	
	public OMap() {}
	
	public void put(Path path, ByteArray data) {
		OMapEntry ome = new OMapEntry(path, data);
		map.add(ome);
	}
	
	public ByteArray get(Path path) {
		for (OMapEntry ome : map) {
			if (ome.path.equals(path))
				return ome.data;
		}
		return null;
	}
	
	public boolean containsKey(Path path) {
		for (OMapEntry ome : map) {
			Path p = ome.path;
			if (p.equals(path))
				return true;
		}
		return false;
	}
	
	public boolean containsDirectory(Path dir) {
		String dirStr = dir.toString();
		if (!dirStr.endsWith("/"))
			dirStr = dirStr + "/";
		
		for (OMapEntry ome : map) {
			Path p = ome.path;
			if (p.toString().startsWith(dirStr))
				return true;
		}
		return false;
		
	}
	
	public List<Path> keySet() {
		List<Path> keys = new ArrayList<Path>();
		for (OMapEntry ome : map)
			keys.add(ome.path);
		return keys;
	}
	
}
