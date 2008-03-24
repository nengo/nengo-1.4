package ca.nengo.config;

/**
 * <p>This interface can be implemented by objects that wish to expose a customized Configuration.
 * If an object does not implement the getConfiguration() method, its properties are deduced 
 * by Java reflection.</p>
 * 
 * @author Bryan Tripp
 */
public interface Configurable {
	
	/**
	 * @return This Configurable's Configuration data 
	 */
	public Configuration getConfiguration();
	
}
