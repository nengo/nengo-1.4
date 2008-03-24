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
