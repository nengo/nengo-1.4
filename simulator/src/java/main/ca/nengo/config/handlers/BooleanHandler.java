/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "BooleanHandler.java". Description: 
"ConfigurationHandler for Boolean values"

The Initial Developer of the Original Code is Bryan Tripp & Centre for Theoretical Neuroscience, University of Waterloo. Copyright (C) 2006-2008. All Rights Reserved.

Alternatively, the contents of this file may be used under the terms of the GNU 
Public License license (the GPL License), in which case the provisions of GPL 
License are applicable  instead of those above. If you wish to allow use of your 
version of this file only under the terms of the GPL License and not to allow 
others to use your version of this file under the MPL, indicate your decision 
by deleting the provisions above and replace  them with the notice and other 
provisions required by the GPL License.  If you do not delete the provisions above,
a recipient may use your version of this file under either the MPL or the GPL License.
*/

/*
 * Created on 17-Dec-07
 */
package ca.nengo.config.handlers;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import ca.nengo.config.ui.ConfigurationChangeListener;

/**
 * ConfigurationHandler for Boolean values. 
 * 
 * @author Bryan Tripp
 */
public class BooleanHandler extends BaseHandler {

	public BooleanHandler() {
		super(Boolean.class);
	}

	@Override
	public Component getEditor(Object o, ConfigurationChangeListener listener, JComponent parent) {
		JPanel result = new JPanel(new FlowLayout());
		result.setBackground(Color.WHITE);
		
		final JCheckBox cb = new JCheckBox("", ((Boolean) o).booleanValue());
		final JButton button = new JButton("OK");
		
		listener.setProxy(new ConfigurationChangeListener.EditorProxy() {
			public Object getValue() {
				return new Boolean(cb.isSelected());
			}
		});
		button.addActionListener(listener);

		result.add(cb);
		result.add(button);
		
		cb.setEnabled(false);
		button.setEnabled(false);
		Thread thread = new Thread() {
			public void run() {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {}
				cb.setEnabled(true);
				button.setEnabled(true);					
			}
		};
		thread.start();
		
		return result;
	}

	/**
	 * @see ca.nengo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return new Boolean(false);
	}
	
}
