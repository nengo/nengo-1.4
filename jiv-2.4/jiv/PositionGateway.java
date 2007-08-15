
// $Id: PositionGateway.java,v 1.2 2003/07/19 08:46:39 crisco Exp $
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

import java.util.Vector;

/**
 * Provides a gateway ("firewall") for exchanging
 * <code>PositionEvent</code>-s between two sets of
 * <code>PositionListener/PositionGenerator</code>-s.
 * This way, the two sets (called "internal" and "external") are not
 * aware of each other.  The two interfaces implemented by this class
 * are for the "internal" side of the gateway.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: PositionGateway.java,v 1.2 2003/07/19 08:46:39 crisco Exp $ 
 */
public final class PositionGateway extends PositionListenerAdapter 
    implements PositionGenerator {

    /*private*/ Vector internal_listeners= new Vector();
    /*private*/ Vector external_listeners= new Vector();

    synchronized final public void positionChanged( PositionEvent e) {
	_forwardEvent( external_listeners, e);
    }
	
    synchronized public void addPositionListener( PositionListener pl) {
	_addPositionListener( internal_listeners, pl);
    }

    synchronized public void removePositionListener( PositionListener pl) {
	_removePositionListener( internal_listeners, pl);
    }

    synchronized final public void positionChanged_External( PositionEvent e) {
	_forwardEvent( internal_listeners, e);
    }
	
    synchronized public void addPositionListener_External( PositionListener pl) {
	_addPositionListener( external_listeners, pl);
    }

    synchronized public void removePositionListener_External( PositionListener pl) {
	_removePositionListener( external_listeners, pl);
    }

    final /*private*/ void _forwardEvent( Vector destinations, PositionEvent event)
    {
	for( int i= 0; i < destinations.size(); ++i)
	    ((PositionListener)destinations.elementAt( i)).positionChanged( event);
    }

    /*private*/ void _addPositionListener( Vector vec, PositionListener pl) {

	if( null == pl || vec.contains( pl))
	    return;
	vec.addElement( pl);
    }
    /*private*/ void _removePositionListener( Vector vec, PositionListener pl) {

	if( null != vec)
	    vec.removeElement( pl);
    }
}

