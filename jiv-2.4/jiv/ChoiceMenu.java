
// $Id: ChoiceMenu.java,v 1.1 2001/04/08 00:04:27 cc Exp $
/* 
  This file is part of JIV.  
  Copyright (C) 2000, 2001 Chris A. Cocosco (crisco@bic.mni.mcgill.ca)

  JIV is free software; you can redistribute it and/or modify it under
  the terms of the GNU General Public License as published by the Free
  Software Foundation; either version 2 of the License, or (at your
  option) any later version.

  JIV is distributed in the hope that it will be useful, but WITHOUT
  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
  or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public
  License for more details.

  You should have received a copy of the GNU General Public License
  along with JIV; if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA, 
  or see http://www.gnu.org/copyleft/gpl.html
*/


package jiv;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

/** 
 * A choice menu that allows to select one item from it, radio-buttons
 * style.  This component is missing from the basic Java 1.1 AWT.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: ChoiceMenu.java,v 1.1 2001/04/08 00:04:27 cc Exp $ 
 */
public final class ChoiceMenu extends Menu {

    /*private*/ static final boolean	DEBUG= false;
    /** used for debugging the IE4/5 checkboxmenuitem problem */
    /*private*/ static final boolean	DEBUG_IE= false;

    /*private*/ Object[][] 		key_value;
    // 'private' here crashes javac
    /*private*/ Object			selection_value; 
    /*private*/ ActionListener 		external_action_listener;
    /*private*/ CheckboxMenuItem[]	menu_items;
    // 'private' here crashes javac
    /*private*/ CheckboxMenuItem	last_selected_menu_item; 

    public ChoiceMenu( String title, 
		       /** an array of arrays, the latter being key-value pairs
			   or key-value-flag triplets
			   -keys represent the displayed labels (should be String-s)
			   -values represent their associated values (doh!)
			   -flag, if present, indicates wheather this menu entry 
			    should be disabled (should be a Boolean) 
		       */
		       Object[][] key_value,
		       String default_selection,
		       /** it's notified whenever the selection changes 
			   (but it's given a null ActionEvent object)
		       */
		       ActionListener external_action_listener
		       ) {

	super( title, true); // a "tear-off" menu!

	this.key_value= key_value;
	this.external_action_listener= external_action_listener;
	// _containsKey also returns false if its arg is a "disabled" menu entry
	if( ! _containsKey( default_selection)) 
	    throw new IllegalArgumentException( "invalid default_selection: " + 
						default_selection);
	this.selection_value= _getValue( default_selection);
	menu_items= new CheckboxMenuItem[ key_value.length];

	ItemListener il= new ItemListener() {
	    public synchronized void itemStateChanged( ItemEvent e) {

		if( DEBUG_IE) System.out.println( this + ":" + e);
		CheckboxMenuItem selected_mi= (CheckboxMenuItem)e.getSource();
		/* a well-written AWT implementation should not
		   generate events for a "disabled" menu item; but
		   better to check anyway... */
		if( ! selected_mi.isEnabled()) {
		    System.err.println( "warning: event received from " + 
					"disabled menu entry: " + selected_mi);
		    // undo any damage that may have been done...
		    last_selected_menu_item.setState( true);
		    selected_mi.setState( false);
		    return;
		}
		last_selected_menu_item.setState( false);
		(last_selected_menu_item= selected_mi).setState( true);
		Object new_selection_value= _getValue( selected_mi.getLabel());
		if( new_selection_value != selection_value) {
		    selection_value= new_selection_value;
		    ChoiceMenu.this.external_action_listener.actionPerformed( null);
		}
	    }
	};
	boolean selected= false;
	for( int i= 0; i < key_value.length; ++i) {
	    String label= (String)(key_value[ i][ 0]);
	    CheckboxMenuItem cmi= menu_items[ i]= new CheckboxMenuItem( label, false);
	    if( label.equals( default_selection))
		if( selected)
		    System.err.println( "warning: duplicate labels " + 
					"in choice menu specification!");
		else
		    (last_selected_menu_item= cmi).setState( selected= true);
	    if( DEBUG) {
		System.out.println( label + " : " + cmi + " : " + 
				    _entryIsDisabled( key_value[ i]) );
	    }
	    cmi.setEnabled( ! _entryIsDisabled( key_value[ i]));
	    cmi.addItemListener( il);
	    add( cmi);
	}
    }

    public final Object getSelection() { return selection_value; }

    /*private*/ final boolean _entryIsDisabled( Object[] entry_spec) {
	return entry_spec.length >= 3 && ((Boolean)(entry_spec[ 2])).booleanValue();
    }

    /** also returns false if its argument is a "disabled" menu entry
     */
    /*private*/ final boolean _containsKey( String key) {

	try {
	    Object value= _getValue( key);
	    return true;
	}
	catch( IllegalArgumentException ex) {
	    return false;
	}
    }

    /*private*/ final Object _getValue( String key) throws IllegalArgumentException {

	for( int i= 0; i < key_value.length; ++i)
	    if( key.equals( key_value[ i][ 0])) {
		if( _entryIsDisabled( key_value[ i]) )
		    break; // i.e. throw the exception below
		return key_value[ i][ 1];
	    }
	throw new 
	    IllegalArgumentException( "cannot get the value associated with " + key);
    }
} // end of class ChoiceMenu

