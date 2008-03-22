package ca.neo.ui.configurable;

/**
 * Describes a object which can be configured by a IConfigurationManager
 * 
 * @author Shu Wu
 */
public interface IConfigurable {

	/**
	 * Called when configuration parameters have been set
	 * 
	 * @param props
	 *            A set of properties
	 */
	public void completeConfiguration(ConfigResult props) throws ConfigException;

	/**
	 * Called before full configuration to initialize and find errors.
	 * 
	 * @throws ConfigException
	 *             Exception thrown if there is an error during
	 *             pre-configuration.
	 */
	public void preConfiguration(ConfigResult props) throws ConfigException;

	/**
	 * @return An array of objects which describe what needs to be configured in
	 *         this object
	 */
	public ConfigSchema getSchema();

	/**
	 * @return Name given to this type of object
	 */
	public String getTypeName();

}
