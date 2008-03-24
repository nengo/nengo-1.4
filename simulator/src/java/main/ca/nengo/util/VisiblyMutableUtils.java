/*
 * Created on 28-Jan-08
 */
package ca.nengo.util;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import ca.nengo.model.StructuralException;

/**
 * Utility methods for VisiblyMutable objects. 
 * 
 * @author Bryan Tripp
 */
public class VisiblyMutableUtils {
	
	private static Logger ourLogger = Logger.getLogger(VisiblyMutableUtils.class);

	/**
	 * Notifies listeners of a change to the given VisiblyMutable object.  
	 * 
	 * @param vm The changed VisiblyMutable object
	 * @param listeners List of things listening for changes
	 */
	public static void changed(final VisiblyMutable vm, List<VisiblyMutable.Listener> listeners) {
		VisiblyMutable.Event event = new VisiblyMutable.Event() {
			public VisiblyMutable getObject() {
				return vm;
			}
		};
		
		try {
			fire(event, listeners);
		} catch (StructuralException e) {
			ourLogger.error(e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * @param vm The changed VisiblyMutable object
	 * @param oldName The old (existing) name of the VisiblyMutable 
	 * @param newName The new (replacement) name of the VisiblyMutable
	 * @param listeners List of things listening for changes
	 * @throws StructuralException if the new name is invalid
	 */
	public static void nameChanged(final VisiblyMutable vm, final String oldName, final String newName, 
			List<VisiblyMutable.Listener> listeners) throws StructuralException {
		
		VisiblyMutable.NameChangeEvent event = new VisiblyMutable.NameChangeEvent() {

			public String getNewName() {
				return newName;
			}

			public String getOldName() {
				return oldName;
			}

			public VisiblyMutable getObject() {
				return vm;
			}
			
		};
		
		try {
			fire(event, listeners);			
		} catch (RuntimeException e) {
			throw new StructuralException(e.getMessage(), e);
		}
	}
	
	private static void fire(VisiblyMutable.Event event, List<VisiblyMutable.Listener> listeners) throws StructuralException {
		if (listeners != null) {
			Iterator<VisiblyMutable.Listener> iterator = listeners.iterator();
			while (iterator.hasNext()) {
				VisiblyMutable.Listener listener = iterator.next();
				listener.changed(event);
			}			
		}
	}
}
