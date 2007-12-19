package ca.neo.ui.models.viewers;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Settings for a Network UI object
 * 
 * @author Shu Wu
 */
public class NetworkViewerConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * File name of this Network
	 */
	private String fileName;

	/**
	 * Folder name of this Network
	 */
	private String folderName;

	/**
	 * Saved layouts
	 */
	private final Hashtable<String, NodeLayout> layouts;

	/**
	 * @param defaultFileName
	 *            Default file name to give to this network
	 */
	public NetworkViewerConfig(String defaultFileName) {
		super();
		this.fileName = defaultFileName;
		layouts = new Hashtable<String, NodeLayout>();
	}

	/**
	 * @param layout
	 *            Layout to be added
	 */
	public void addLayout(NodeLayout layout) {
		layouts.put(layout.getName(), layout);
	}

	/**
	 * @return File name of this network
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @return Folder name of this network
	 */
	public String getFolderName() {
		return folderName;
	}

	public NodeLayout getLayout(String name) {
		return layouts.get(name);
	}

	/**
	 * @return Names of the saved layouts
	 */
	public String[] getLayoutNames() {
		String[] names = new String[layouts.size()];
		int i = 0;
		Enumeration<NodeLayout> en = layouts.elements();
		while (en.hasMoreElements()) {
			names[i++] = en.nextElement().getName();
		}
		return names;
	}

	/**
	 * @param name
	 *            Name of layout to be removed
	 * @return Reference to the removed layout
	 */
	public NodeLayout removeLayout(String name) {
		return layouts.remove(name);
	}

	/**
	 * @param fileName
	 *            File name
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @param folderName
	 *            Folder name
	 */
	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

}
