/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "FileManager.java". Description: 
"Handles saving and loading of Node   
  
  TODO: a better job (this is a quick one)
  TODO: is there any metadata to store? 
  TODO: test
  
  @author Bryan Tripp"

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
 * Created on 7-Jun-2006
 */
package ca.nengo.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import ca.nengo.model.Node;
import ca.nengo.util.TimeSeries;

/**
 * Handles saving and loading of Node   
 * 
 * TODO: a better job (this is a quick one)
 * TODO: is there any metadata to store? 
 * TODO: test
 * 
 * @author Bryan Tripp
 */
public class FileManager {

	public static final String ENSEMBLE_EXTENSION = "nef";
	
	private static File ourDefaultLocation = new File("./work");
	static {
		ourDefaultLocation.mkdirs();
	}
	
	public static File getDefaultLocation() {
		return ourDefaultLocation;
	}
	
	public static void setDefaultLocation(File location) {
		ourDefaultLocation = location;
	}

	public void save(Node node, File destination) throws IOException {
		saveObject(node, destination);
	}
	
	public void save(TimeSeries timeSeries, File destination) throws IOException {
		saveObject(timeSeries, destination);
	}
	
	private static void saveObject(Object object, File destination) throws IOException {		
		FileOutputStream fos = new FileOutputStream(destination);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
		oos.flush();
		fos.close();
	}
	
	public Object load(File source) throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(source);
		
		ObjectInputStream ois = new ObjectInputStream(fis);
		
		return ois.readObject();
	}
	
}
