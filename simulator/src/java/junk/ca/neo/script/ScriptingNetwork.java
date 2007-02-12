/*
 * Created on 8-Jun-2006
 */
package ca.neo.script;

/**
 * <p>Script-oriented facade for a network.</p> 
 * 
 * <p>This interface provides a means of performing common network-related tasks that is
 * simple and amenable to scripting (eg in matlab, javascript, or XML).</p>
 * 
 * TO DO: support non-NEF ensembles here? or just support probing but not creating or connecting? 
 * 
 * @author Bryan Tripp
 */
public interface ScriptingNetwork {
	
	/**
	 * Name of the basic output channel of every node. This is the only output channel for 
	 * external inputs. For ensembles it represents the state output, and other outputs can 
	 * be defined as functions of the state output.  
	 */
	public static final String MAIN_CHANNEL = "MAIN";
	
	/**
	 * Type code for leaky-integrate-and-fire neurons. 
	 */
	public static final String LIF_TYPE = "LIF";
	
	/**
	 * Saves the network to a file.  
	 * 
	 * @param fileName Name of file to save under. By convention a network file should have the 
	 * 		extension "neonetwork".  
	 */
	public void save(String fileName) throws ScriptingException;
	
	/**
	 * Loads the network from a file. 
	 *  
	 * @param fileName Name of file to save under. Only the path and base name should be given.  
	 * 		The extension is chosen automatically. 
	 */
	public void load(String fileName) throws ScriptingException; 

	/**
	 * Adds an "external input" to the network. An external input models a source of information that 
	 * is not included in the detailed neural model, for example a sensory signal. 
	 *  
	 * @param functions A list of functions of time, one for each dimension of the input. Functions  
	 * 		can include common mathematical operators, trigonometric functions, exp() and ln(), and 
	 * 		the constant 'pi'.  
	 * @return A ScriptingComponent representing the new input and its properties
	 * @throws ScriptingException if the given functions can not be interpreted
	 */
	public ScriptingComponent addExternalInput(String[] functions) throws ScriptingException;

	/**
	 * Adds a new neural ensemble to the network. 
	 * 
	 * @param n Number of neurons in the ensemble
	 * @param neuronType A code (drawn from static members of this interface with the suffix "TYPE") that 
	 * 		indicates the type of neuron model to use. 
	 * @return A ScriptingComponent representing the new ensemble and its properties
	 * @throws ScriptingException if the given neuron type is unknown
	 */
	public ScriptingComponent addEnsemble(int n, String neuronType) throws ScriptingException;
	
	/**
	 * Note that all ensembles have the MAIN_CHANNEL by default. All properties of an ensemble should be set before any 
	 * additional output channels are added to it. 
	 * 
	 * @param ensemble Name of ensemble to which to add output
	 * @param channelName Name of new output channel for this ensemble
	 * @param function Functions of state variables that constitute multi-dimensional output. State variables are referenced 
	 * 		within these functions as x0, x1, etc. See addExternalInput(...) for additional notes on function syntax.  
	 * @return A ScriptingComponent representing the new output and its properties
	 * @throws ScriptingException if the specified ensemble does not exist, or if it already has an output of the given name, 
	 * 		or if there is an error in the function syntax 
	 */
	public ScriptingComponent addOutputChannel(String ensemble, String channelName, String[] function) throws ScriptingException;
	
	/**
	 * @param ensemble Name of an ensemble 
	 * @param channelName Name of output channel on this ensemble to remove
	 * @throws ScriptingException If the specified ensemble or channel does not exist
	 */
	public void removeOutputChannel(String ensemble, String channelName) throws ScriptingException;
	
	/**
	 * @return A list of all nodes in the network
	 */
	public ScriptingComponent[] listNodes();

	/**
	 * @param nodeName Name of a node in the network
	 * @return A list of all output channels that arise from the specified node. 
	 * @throws ScriptingException If there is no node in the network with the given name 
	 */
	public ScriptingComponent[] listOutputs(String nodeName) throws ScriptingException;

	/**
	 * Removes a node (external input or ensemble) from the network, along with all its connections. 
	 * 
	 * @param nodeName Name of the node to remove
	 * @throws ScriptingException if the named node does not exist
	 */
	public void removeNode(String nodeName) throws ScriptingException;

	/**
	 * Creates a new connection between nodes (ie external inputs / ensembles) in the network. 
	 * 
	 * @param from Name of the node from which the connection is to arise
	 * @param outputChannel Name of the output channel on the 'from' node from which the connection is 
	 * 		to arise (this must be MAIN_CHANNEL for external inputs, but can be the name of another output for 
	 * 		an ensemble)
	 * @param to Name of the node on which the connection is to terminate
	 * @param transform A matrix that defines a linear transformation between the specified output channel and the input
	 * 		to the termination ensemble 
	 * @return A ScriptingComponent representing the new connection and its properties
	 * @throws ScriptingException If the number of columns in the given transform matrix does not match the dimension
	 * 		of the output channel, or if the number of rows does not match the dimension of the termination ensemble  
	 */
	public ScriptingComponent connect(String from, String outputChannel, String to, float[][] transform) throws ScriptingException;

	/**
	 * Removes a connection between nodes (ie external inputs or ensembles). 
	 *   
	 * @param from Name of the node from which the connection arises
	 * @param outputChannel Name of the output channel (this must be MAIN_CHANNEL for external inputs, but 
	 * 		can be the name of another output for an ensemble). 
	 * @param to Name of the node on which this connection terminates 
	 * @throws ScriptingException If no connection exists with the specified origin and termination  
	 */
	public void disconnect(String from, String outputChannel, String to) throws ScriptingException;
	
	/**
	 * @return A list of all connections between nodes in the network
	 */
	public ScriptingComponent[] listConnections();	
	
}
