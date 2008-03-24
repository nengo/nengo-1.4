package ca.nengo.ui.dataList;

import java.lang.ref.WeakReference;

import ca.nengo.model.Node;

public class NeoTreeNode extends SortableMutableTreeNode {
	private static final long serialVersionUID = 1L;

	private WeakReference<Node> nengoNodeRef;

	public NeoTreeNode(String name, Node neoNode) {
		super(name);
		this.nengoNodeRef = new WeakReference<Node>(neoNode);
	}

	/**
	 * @return Reference to NeoNode, otherwise null 
	 */
	public Node getNeoNode() {
		return nengoNodeRef.get();
	}

}
