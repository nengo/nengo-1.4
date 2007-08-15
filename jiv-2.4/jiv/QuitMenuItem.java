
// $Id: QuitMenuItem.java,v 1.1 2001/05/15 16:07:28 crisco Exp $
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
 * An application 'quit' menu item.
 *
 * @author Chris Cocosco (crisco@bic.mni.mcgill.ca)
 * @version $Id: QuitMenuItem.java,v 1.1 2001/05/15 16:07:28 crisco Exp $
 */
class QuitMenuItem extends MenuItem {

    /*private*/ Main main;

    QuitMenuItem( Main applet_root) { 

	super( "Quit");
	main= applet_root;
	addActionListener( new ActionListener() {
		public void actionPerformed( ActionEvent e) {
		    main.destroy();
		}
	    });
    }

} // end of class QuitMenuItem

