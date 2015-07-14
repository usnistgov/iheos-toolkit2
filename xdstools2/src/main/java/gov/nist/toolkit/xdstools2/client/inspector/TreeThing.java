package gov.nist.toolkit.xdstools2.client.inspector;

import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

class TreeThing {
	Tree tree;
	TreeItem item;

	TreeThing(Tree tree) {
		this.tree = tree;
		this.item = null;
	}

	TreeThing(TreeItem item) {
		this.tree = null;
		this.item = item;
	}

	void addItem(TreeItem newItem) {
		if (tree == null)
			item.addItem(newItem);
		else
			tree.addItem(newItem);
	}

	void addItem(TreeThing newItem) {
		if (tree == null)
			item.addItem(newItem.item);
		else
			tree.addItem(newItem.item);
	}
}