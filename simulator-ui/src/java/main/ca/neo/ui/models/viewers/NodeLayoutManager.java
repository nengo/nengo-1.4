package ca.neo.ui.models.viewers;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public class NodeLayoutManager implements Serializable {

	private static final long serialVersionUID = 1L;

	Hashtable<String, NodeLayout> layouts;

	public NodeLayoutManager() {
		super();
		layouts = new Hashtable<String, NodeLayout>();
	}

	public void addLayout(NodeLayout e) {
		layouts.put(e.getName(), e);
	}

	public String[] getLayoutNames() {
		String[] names = new String[layouts.size()];
		int i = 0;
		Enumeration<NodeLayout> en = layouts.elements();
		while (en.hasMoreElements()) {
			names[i++] = en.nextElement().getName();
		}
		return names;
	}

	public NodeLayout getLayout(String name) {
		return layouts.get(name);
	}

	public NodeLayout removeLayout(String name) {
		return layouts.remove(name);
	}

}
