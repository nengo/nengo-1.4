package ca.neo.ui.dataList;

import java.util.Collections;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Has a sort() function to sort children
 * 
 * @author Shu Wu
 */
public class SortableMutableTreeNode extends DefaultMutableTreeNode {

	public SortableMutableTreeNode(Object userObject) {
		super(userObject);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	public void sort() {
		if (children != null) {
			Collections.sort(children, new NaturalOrderComparator());
		}
	}
}
