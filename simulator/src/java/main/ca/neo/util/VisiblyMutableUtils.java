/*
 * Created on 28-Jan-08
 */
package ca.neo.util;

import java.util.Iterator;
import java.util.List;

/**
 * Utility methods for VisiblyMutable objects. 
 * 
 * @author Bryan Tripp
 */
public class VisiblyMutableUtils {

	/**
	 * Notifies listeners of a change to the given VisiblyMutable object.  
	 * 
	 * @param vm The changed VisiblyMutable object
	 * @param listeners
	 */
	public static void changed(final VisiblyMutable vm, List<VisiblyMutable.Listener> listeners) {
		VisiblyMutable.Event event = new VisiblyMutable.Event() {
			public VisiblyMutable getObject() {
				return vm;
			}
		};
		
		if (listeners != null) {
			Iterator<VisiblyMutable.Listener> iterator = listeners.iterator();
			while (iterator.hasNext()) {
				VisiblyMutable.Listener listener = iterator.next();
				listener.changed(event);
			}			
		}
	}
}
