/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "NetworkViewerConfig.java". Description: 
"Settings for a Network UI object
  
  @author Shu Wu"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

package ca.nengo.ui.models.viewers;

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
