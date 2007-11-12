/*
 * Created on 5-Nov-07
 */
package ca.neo.ui.script;

/**
 * A list of commands that have been entered previously. 
 * 
 * @author Bryan Tripp
 */
public class HistoryCompletor extends CommandCompletor {

	public HistoryCompletor() {
		//TODO: load saved history of available
	}
	
	/**
	 * @param command New command to add to the history
	 */
	public void add(String command) {
		getOptions().add(command);
		resetIndex();
	}
	
	public void save() {
		//TODO
	}

}
