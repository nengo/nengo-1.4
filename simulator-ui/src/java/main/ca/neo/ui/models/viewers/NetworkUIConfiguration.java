package ca.neo.ui.models.viewers;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

public class NetworkUIConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;

	private String folderName;
	private Hashtable<String, NodeLayout> layouts;

	public NetworkUIConfiguration(String defaultFileName) {
		super();
		this.fileName = defaultFileName;
		layouts = new Hashtable<String, NodeLayout>();
	}

	public void addLayout(NodeLayout e) {
		layouts.put(e.getName(), e);
	}

	public NodeLayout getLayout(String name) {
		return layouts.get(name);
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

	public NodeLayout removeLayout(String name) {
		return layouts.remove(name);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

}
