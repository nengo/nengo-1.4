/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "HistoryCompletor.java". Description: 
"A list of commands that have been entered previously"

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

/*
 * Created on 5-Nov-07
 */
package ca.nengo.ui.script;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * A list of commands that have been entered previously. 
 * 
 * @author Bryan Tripp
 */
public class HistoryCompletor extends CommandCompletor {

	public static String HISTORY_LOCATION_PROPERTY = "HistoryCompletor.File";

	private BufferedWriter myWriter;
	
	public HistoryCompletor() {
		File f = new File(System.getProperty(HISTORY_LOCATION_PROPERTY, "commandhistory.txt"));
		if (f.exists() && f.canRead()) {
			try {
				BufferedReader r = new BufferedReader(new FileReader(f));
				String command = null;
				while ((command = r.readLine()) != null) {
					getOptions().add(command);
				}
				r.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		
		try {
			if (!f.exists()) f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (f.exists() && f.canWrite()) {
			try {
				myWriter = new BufferedWriter(new FileWriter(f));
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
	
	/**
	 * @param command New command to add to the history
	 */
	public void add(String command) {
		getOptions().add(command);
		resetIndex();
		
		if (myWriter != null) {
			try {
				myWriter.write(command + "\r\n");
				myWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
