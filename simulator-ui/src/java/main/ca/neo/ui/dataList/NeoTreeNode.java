package ca.neo.ui.dataList;

import java.lang.ref.WeakReference;

import ca.neo.model.Node;

public class NeoTreeNode extends SortableMutableTreeNode {
	private static final long serialVersionUID = 1L;

	private WeakReference<Node> neoNodeRef;

	public NeoTreeNode(String name, Node neoNode) {
		super(name);
		this.neoNodeRef = new WeakReference<Node>(neoNode);
	}

	/**
	 * @return Reference to NeoNode, otherwise null 
	 */
	public Node getNeoNode() {
		return neoNodeRef.get();
	}

}
