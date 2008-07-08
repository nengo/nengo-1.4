/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "SaveNodeAction.java". Description: 
"Saves a PNodeContainer
  
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

package ca.nengo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ca.nengo.ui.NengoGraphics;
import ca.nengo.ui.models.UINeoNode;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.actions.UserCancelledException;
import ca.shu.ui.lib.objects.activities.TrackedAction;
import ca.shu.ui.lib.util.UserMessages;

/**
 * Saves a PNodeContainer
 * 
 * @author Shu Wu
 */
public class SaveNodeAction extends StandardAction {
	private static final long serialVersionUID = 1L;

	/**
	 * File to be saved to
	 */
	private File file;
	
	private boolean blocking;  // if blocking, then saving occurs in the same thread before
	                           // returning

	private UINeoNode nodeUI;

	/**
	 * @param nodeUI
	 *            Node to be saved
	 */
	public SaveNodeAction(UINeoNode nodeUI) {
		super("Save "+nodeUI.getName());

		this.nodeUI = nodeUI;
		
		this.blocking = false;
	}
	
	/**
	 * A blocking save will perform the save before returning, rather than saving in a
	 * separate thread.  This is useful on exit, when we want to be sure the save has
	 * finished before leaving Nengo.
	 * 
	 * @param value
	 * 				Whether or not the save should be blocking
	 */
	public void setBlocking(boolean value) {
		this.blocking = value;
	}

	@Override
	protected void action() throws ActionException {
		int returnVal = JFileChooser.CANCEL_OPTION;

		NengoGraphics.FileChooser.setSelectedFile(new File(nodeUI.getFileName()));

		returnVal = NengoGraphics.FileChooser.showSaveDialog();

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = NengoGraphics.FileChooser.getSelectedFile();
			
			if (blocking) {
				try {
					nodeUI.saveModel(file);
				} catch (IOException e) {
					UserMessages.showError("Could not save file: " + e.toString());
				} catch (OutOfMemoryError e) {
					UserMessages
							.showError("Out of memory, please increase memory size: "
									+ e.toString());
				}
			} else {
	
				TrackedAction task = new TrackedAction("Saving model") {
	
					private static final long serialVersionUID = 1L;
	
					@Override
					protected void action() throws ActionException {
						try {
	
							nodeUI.saveModel(file);
	
						} catch (IOException e) {
							UserMessages.showError("Could not save file: "
									+ e.toString());
						} catch (OutOfMemoryError e) {
							UserMessages
									.showError("Out of memory, please increase memory size: "
											+ e.toString());
						}
	
					}
				};
				task.doAction();
			}

		} else {
			throw new UserCancelledException();
		}

	}
}
