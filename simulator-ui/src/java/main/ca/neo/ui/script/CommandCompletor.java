/*
 * Created on 11-Nov-07
 */
package ca.neo.ui.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for command completors, which provide suggestions for filling in the 
 * remainder of partially-specified scripting commands. 
 * 
 * @author Bryan Tripp
 */
public abstract class CommandCompletor {

	private List<String> myOptions;
	private int myIndex;

	public CommandCompletor() {
		myOptions = new ArrayList<String>(100);
		myIndex = myOptions.size();
	}
	
	/**
	 * @return The list of completion options currently under consideration 
	 */
	protected List<String> getOptions() {
		return myOptions;
	}

	/**
	 * Resets the index to the list of completion options to its default location.
	 */
	public void resetIndex() {
		myIndex = myOptions.size();
	}
	
	/**
	 * @param partial Partial command string 
	 * @return Next most recent command (from current index in options list) that begins with 
	 * 		given partial command. Returns the arg if end of list is reached.  
	 */
	public String previous(String partial) {
		String result = null;
		for (int i = myIndex-1; i >= 0 && result == null; i--) {
			if (myOptions.get(i).startsWith(partial)) {
				result = myOptions.get(i);
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
	 * @param partial Partial command string 
	 * @return Next command (from current index in options list) that begins with 
	 * 		given partial command. Returns the arg if end of list is reached.  
	 */
	public String next(String partial) {
		String result = null;
		for (int i = myIndex+1; i < myOptions.size() && result == null; i++) {
			if (myOptions.get(i).startsWith(partial)) {
				result = myOptions.get(i);
				myIndex = i;
			}
		}
		if (result == null) {
			result = partial;
			myIndex = myOptions.size();
		}
		return result;		
	}
	
}
