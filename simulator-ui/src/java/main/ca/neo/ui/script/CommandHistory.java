/*
 * Created on 5-Nov-07
 */
package ca.neo.ui.script;

import java.util.ArrayList;
import java.util.List;

/**
 * A list of commands that have been entered previously. 
 * 
 * @author Bryan Tripp
 */
public class CommandHistory {

	private List<String> myCommands;
	private int myIndex;
	
	public CommandHistory() {
		//TODO: load saved history of available
		myCommands = new ArrayList<String>(100);
		myIndex = myCommands.size();
	}
	
	/**
	 * @param command New command to add to the history
	 */
	public void add(String command) {
		myCommands.add(command);
		resetIndex();
	}
	
	public void resetIndex() {
		myIndex = myCommands.size();
	}
	
	/**
	 * @param partial Partial command string (from beginning)
	 * @return Next most recent command (from current position) that begins with 
	 * 		given partial command. Returns partial if end of list is reached.  
	 */
	public String previous(String partial) {
		String result = null;
		for (int i = myIndex-1; i >= 0 && result == null; i--) {
//			System.out.println("history (up) " + myIndex + ": " + myCommands.get(i));
			if (myCommands.get(i).startsWith(partial)) {
				result = myCommands.get(i);
				myIndex = i;
			}
		}
		if (result == null) {
			result = partial;
			myIndex = -1;
		}
		return result;
	}
	
	/**
	 * @param partial Partial command string (from beginning)
	 * @return Next command (from current position) that begins with 
	 * 		given partial command. Returns partial if end of list is reached.  
	 */
	public String next(String partial) {
		String result = null;
		for (int i = myIndex+1; i < myCommands.size() && result == null; i++) {
//			System.out.println("history (down) " + myIndex + ": " + myCommands.get(i));
			if (myCommands.get(i).startsWith(partial)) {
				result = myCommands.get(i);
				myIndex = i;
			}
		}
		if (result == null) {
			result = partial;
			myIndex = myCommands.size();
		}
		return result;		
	}
	
	public void save() {
		//TODO
	}

}
