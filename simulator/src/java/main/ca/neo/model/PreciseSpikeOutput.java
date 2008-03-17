package ca.neo.model;

/**
 * InstantaneousOutput consisting of spikes and the time since they occurred.
 *  
 * @author Terry Stewart
 */
public interface PreciseSpikeOutput extends SpikeOutput {
	/**
	 * @return The times when the spikes occurred, as offsets from the previous time step. 
	 * 		Values negative values indicate no spike. 
	 */
	public float[] getSpikeTimes();
}
