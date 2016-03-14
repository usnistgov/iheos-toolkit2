package gov.nist.toolkit.valregmsg.xdm;

import java.util.ArrayList;

public class Path {

	ArrayList<String> dirs;
	String file = null;

	public Path(String path) {
		dirs = asList(path.split("/"));
		if (!path.endsWith("/")) {
			file = pop();
		}
	}

	Path() {}

	ArrayList<String> asList(String[] parts) {
		ArrayList<String> l = new ArrayList<String>();

		for (String part : parts) 
			l.add(part);

		return l;
	}
	
	public String getDir(int i) {
		if (dirs.size() <= i)
			return null;
		return dirs.get(i);
	}
	
	public boolean hasFile() {
		return file != null;
	}
	
	public int dirSize() {
		return dirs.size();
	}
	
	public Path withFile(String filename) {
		Path x = clone();
		x.file = filename;
		return x;
	}

	public String pop() {
		if (file != null) {
			String val = file;
			file = null;
			return val;
		}
		if (dirs.size() == 0)
			return null;
		int last = dirs.size()-1;
		String val = dirs.get(last);
		dirs.remove(last);
		return val;
	}

	public boolean hasParent() {
		if (file == null)
			return dirs.size() > 1;
			return dirs.size() > 0;
	}

	public Path getParent() {
		if (!hasParent())
			return null;
		Path path = clone();
		if (path.file != null) {
			path.file = null;
			return path;
		}
		path.dirs.remove(path.dirs.size()-1);
		return path;
	}

	@SuppressWarnings("unchecked")
	public Path clone() {
		Path path = new Path();
		path.dirs = (ArrayList<String>) dirs.clone();
		path.file = file;
		return path;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();

		for (String x : dirs) 
			buf.append(x).append("/");

		if (file != null)
			buf.append(file);

		return buf.toString();
	}

	int findElement(String ele) {
		for (int i=0; i<dirs.size(); i++) {
			if (dirs.get(i).equals(ele))
				return i;
		}
		return -1;
	}

	public Path getCanonical() {
		Path p = clone();

		while (true) {
			int index = p.findElement("..");
			if (index == -1)
				break;
			if (index == 0)
				break;
			p.dirs.remove(index-1);  // prev dir
			p.dirs.remove(index-1);  // ..
		}

		if ("..".equals(p.file) && p.dirs.size() > 0) {
			p.file = null;
			p.dirs.remove(p.dirs.size()-1);
		}


		return p;
	}

	public boolean equals(Path x) {
		if (file == null && x.file != null) return false;
		if (file != null && !file.equals(x.file)) return false;
		if (dirs.size() != x.dirs.size()) return false;
		for (int i=0; i<dirs.size(); i++) {
			if (!dirs.get(i).equals(x.dirs.get(i))) return false;
		}
		return true;
	}

	static public void main(String[] args) {
		Path ref;
		ref = new Path("foo");
		assert ref.equals(new Path("foo"));

		ref = new Path("foo/bar");
		assert ref.equals(new Path("foo/bar"));

		assert ref.getCanonical().equals(new Path("foo/bar"));

		ref = new Path("foo/xx/../bar");
		assert ref.findElement("..") == 2;
				
		assert ref.getCanonical().equals(new Path("foo/bar"));
		
		ref = new Path("foo/bar/..").getCanonical();
		assert ref.equals(new Path("foo/"));
	}
}
