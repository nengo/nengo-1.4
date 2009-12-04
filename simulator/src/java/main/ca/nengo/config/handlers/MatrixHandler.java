/*
The contents of this file are subject to the Mozilla Public License Version 1.1 
(the "License"); you may not use this file except in compliance with the License. 
You may obtain a copy of the License at http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis, WITHOUT
WARRANTY OF ANY KIND, either express or implied. See the License for the specific 
language governing rights and limitations under the License.

The Original Code is "MatrixHandler.java". Description: 
"ConfigurationHandler for float[][] values"

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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import ca.nengo.config.IconRegistry;
import ca.nengo.config.ui.ConfigurationChangeListener;
import ca.nengo.config.ui.MatrixEditor;
import ca.nengo.util.MU.MatrixExpander;
import ca.nengo.util.MU.VectorExpander;

/**
 * ConfigurationHandler for float[][] values.
 * 
 * @author Bryan Tripp
 */
public class MatrixHandler extends BaseHandler {

	public MatrixHandler() {
		super(float[][].class);
	}

	@Override
	public Component getEditor(Object o,
			final ConfigurationChangeListener configListener,
			final JComponent parent) {

		/*
		 * The Matrix editor is created in a new JDialog.
		 */

		final float[][] matrix = (float[][]) o;

		// Create Matrix Editor
		final MatrixEditor matrixEditor;
		{

			float[][] copy = new float[matrix.length][];
			for (int i = 0; i < matrix.length; i++) {
				copy[i] = new float[matrix[i].length];
				System.arraycopy(matrix[i], 0, copy[i], 0, matrix[i].length);
			}
			matrixEditor = new MatrixEditor(copy, false, false);
		}

		// Setup config listener's proxy
		{
			configListener.setProxy(new ConfigurationChangeListener.EditorProxy() {
				public Object getValue() {
					return matrixEditor.getMatrix();
				}
			});
		}

		// Create asynchronous modal dialog to edit the matrix
		//
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final JDialog dialog = createDialog(parent, matrixEditor);

				// Create buttons
				//
				// The matrix editor will not use the configListener as a
				// listener, but
				// conversely notify it of changes. This is because the Config
				// listener only support
				// one type of "save" event.
				//
				JButton okButton, cancelButton;
				{
					matrixEditor.getControlPanel().add(new JSeparator(JSeparator.VERTICAL));

					okButton = new JButton("Save Changes");
					matrixEditor.getControlPanel().add(okButton);
					okButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							matrixEditor.finishEditing();
							configListener.commitChanges();
							dialog.setVisible(false);
						}
					});

					cancelButton = new JButton("Cancel");
					matrixEditor.getControlPanel().add(cancelButton);
					cancelButton.addActionListener(new java.awt.event.ActionListener() {
						public void actionPerformed(java.awt.event.ActionEvent e) {
							configListener.cancelChanges();
							dialog.setVisible(false);
						}
					});
				}

				// Set dialog model
				//
				dialog.setModal(true);
				dialog.setVisible(true);
				int desiredWidth = Math.max(matrix[0].length * 80 + 100, 1024);
				desiredWidth = Math.min(desiredWidth, 400);
				dialog.setPreferredSize(new Dimension(desiredWidth, 600));

				// Handle dialog close
				//
				if (!configListener.isChangeCommited() && !configListener.isChangeCancelled()) {
					configListener.cancelChanges();
				}
			}
		});

		return new JTextField("Editing...");
	}

	/**
	 * Shows a tree in which object properties can be edited.
	 * 
	 * @param o
	 *            The Object to configure
	 */
	private static JDialog createDialog(JComponent parent, JPanel panel) {
		final JDialog dialog;

		Container parentContainer = parent.getRootPane().getParent();

		if (parentContainer instanceof Frame) {
			dialog = new JDialog((Frame) parentContainer, panel.getName());
		} else if (parentContainer instanceof Dialog) {
			dialog = new JDialog((Dialog) parentContainer, panel.getName());
		} else {
			dialog = new JDialog((JDialog) null, panel.getName());
		}

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(panel, BorderLayout.CENTER);
		dialog.pack();

		if (parentContainer != null) {
			dialog.setLocationRelativeTo(parentContainer);// centers on screen
		}
		return dialog;
	}

	@Override
	public Component getRenderer(Object o) {
		JPanel result = new JPanel(new FlowLayout());

		float[][] matrix = (float[][]) o;
		String text = toString(matrix, '\t', "\r\n");
		result.add(new JLabel(IconRegistry.getInstance().getIcon(o)));
		result.add(new JTextArea(text));

		return result;
	}

	@Override
	public Object fromString(String s) {
		return fromString(s, ',', "\r\n");
	}

	@Override
	public String toString(Object o) {
		return toString((float[][]) o, ',', "\r\n");
	}

	/**
	 * @param s
	 *            A String representation of a matrix, eg from
	 *            toString(float[][], char, String)
	 * @param colDelim
	 *            The character used to delimit matrix columns in this string
	 * @param rowDelim
	 *            The string (can be >1 chars) used to delimit matrix rows in
	 *            this string
	 * @return The matrix represented by the string
	 */
	public static float[][] fromString(String s, char colDelim, String rowDelim) {
		String colDelimString = String.valueOf(colDelim);
		MatrixExpander result = new MatrixExpander();

		StringTokenizer rowTokenizer = new StringTokenizer(s, rowDelim, false);
		while (rowTokenizer.hasMoreTokens()) {
			StringTokenizer elemTokenizer = new StringTokenizer(rowTokenizer.nextToken(),
					colDelimString, false);
			VectorExpander row = new VectorExpander();
			while (elemTokenizer.hasMoreTokens()) {
				row.add(Float.parseFloat(elemTokenizer.nextToken()));
			}
			result.add(row.toArray());
		}
		return result.toArray();
	}

	/**
	 * @param matrix
	 *            A matrix
	 * @param colDelim
	 *            A character to be used to delimit matrix columns
	 * @param rowDelim
	 *            A String to be used to delimit matrix rows
	 * @return A String representation of the given matrix using the given
	 *         delimiters
	 */
	public static String toString(float[][] matrix, char colDelim, String rowDelim) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < matrix.length; i++) {
			for (int j = 0; j < matrix[i].length; j++) {
				result.append(matrix[i][j]);
				if (j < matrix[i].length - 1)
					result.append(colDelim);
			}
			if (i < matrix.length - 1)
				result.append(rowDelim);
		}
		return result.toString();
	}

	/**
	 * @see ca.nengo.config.ConfigurationHandler#getDefaultValue(java.lang.Class)
	 */
	public Object getDefaultValue(Class c) {
		return new float[0][];
	}

}
