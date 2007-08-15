
// $Id: InfoPopupWindow.java,v 1.1 2001/05/15 16:07:27 crisco Exp $
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

/**
 * An information popup window, complete with an Ok button to dismiss it. 
 * It is "modal" : the rest of the application won't get any input events
 * until this window is dismissed.
 * 
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: InfoPopupWindow.java,v 1.1 2001/05/15 16:07:27 crisco Exp $ 
 */
public class InfoPopupWindow extends Dialog 
    implements ActionListener, WindowListener {

    /*private*/ Button ok_button;
    {
	ok_button= new Button( "Ok");
	ok_button.addActionListener( this);
    }

    public InfoPopupWindow( Frame parent, MultiLineStringBuffer text ) {
	this( parent, "", text);
    }

    public InfoPopupWindow( Frame parent, 
			    String title, 
			    MultiLineStringBuffer text 
			    ) {
	super( parent, title, true /*modal*/);
	setResizable( true);
	addWindowListener( this);

	final int rows= 20;
	final int cols= 60; 
	TextArea ta= new TextArea( text.toString(), rows, cols, 
				   // text will auto-wrap as necessary
				   TextArea.SCROLLBARS_VERTICAL_ONLY
				   );
	ta.setEditable( false);
	add( ta, "Center");
	add( ok_button, "South");
	// FIXME: kbd input (eg a straight 'Enter') doesn't work...

	pack();
	show();
    }

    protected void processMouseEvent( MouseEvent e) {

	if( e.getID() == MouseEvent.MOUSE_ENTERED ) {
	    /* this is VERY IMPORTANT! otherwise you won't get any KeyEvent-s! */
	    requestFocus();
	    ok_button.requestFocus();
	}
	super.processMouseEvent( e);
    }

    public void actionPerformed( ActionEvent ae ) { dispose(); }

    public void windowActivated( WindowEvent e ) { }
    public void windowClosed( WindowEvent e ) { }
    public void windowClosing( WindowEvent e ) { dispose(); }
    public void windowDeactivated( WindowEvent e ) { }
    public void windowDeiconified( WindowEvent e ) { }
    public void windowIconified( WindowEvent e ) { }
    public void windowOpened( WindowEvent e ) { }

} // end of class InfoPopupWindow
