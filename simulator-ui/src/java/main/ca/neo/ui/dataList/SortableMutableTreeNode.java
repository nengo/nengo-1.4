package ca.neo.ui.dataList;

import java.util.Collections;
import java.util.Comparator;

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

class NodeComparator implements Comparator<Object> {

	public int compare(Object o1, Object o2) {
		return (o1.toString().compareTo(o2.toString()));
	}

}
