package ca.nengo.ui.configurable;

public enum Sign {
    Unconstrained ("Unconstrained"),
    Positive ("Positive"),
    Negative ("Negative");
    
    private final String name;
    private Sign(String name) {
    	this.name = name;
    }
    
    @Override public String toString() {
    	return name;
    }
}