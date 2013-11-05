package gov.nist.toolkit.dst.cmd.language;

import java.util.ArrayList;
import java.util.List;

public class Node {
	List<Node> nexts = new ArrayList<Node>();
	Object type;
	Object value;
	String name;
	
	public Node() {  }
	
	public Node(String name) {
		this.name = name;
	}
	
	public Node addNext(Node node) {
		nexts.add(node);
		return node;
	}
	
	public Node addNext(String nodeName) {
		return addNext(new Node(nodeName));
	}
	
	public Node next(String token) {
		for (Node next : nexts) {
			if (token.equals(next.name))
				return next;
		}
		return null;
	}
	
	public void run() { }
	
}
