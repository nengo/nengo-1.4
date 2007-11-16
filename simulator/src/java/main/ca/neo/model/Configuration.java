package ca.neo.model;

public interface Configuration {
	
	public String[] getParameterNames();
	
	public Class getType(String name);
	
	public boolean getMutable(String name);
	
	public Object getValue(String name);
	
	public void setValue(String name, Object value);
	
	public void registerListener(Configuration.Listener listener);
	
	public static interface Listener {
		
		public void configurationChange(String name, Object value);
		
	}

}
