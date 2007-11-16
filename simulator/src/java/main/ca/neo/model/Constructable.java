package ca.neo.model;

public interface Constructable {
	
	public String getSerializedLocation() ;
	
	public Factory getFactory();
	
	public Configuration getConstructionParameters();

}
