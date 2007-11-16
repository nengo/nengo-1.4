package ca.neo.model;

public interface Factory extends Configurable {
	
	public Constructable make(Configuration parameters);

}
