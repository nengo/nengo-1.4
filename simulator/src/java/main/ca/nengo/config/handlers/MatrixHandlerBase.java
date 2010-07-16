package ca.nengo.config.handlers;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ca.nengo.config.ui.ConfigurationChangeListener;
import ca.nengo.config.ui.MatrixEditor;

public abstract class MatrixHandlerBase extends BaseHandler {

	public MatrixHandlerBase(Class<?> c) {
		super(c);
	}

	public abstract MatrixEditor CreateMatrixEditor(Object o,
			final ConfigurationChangeListener configListener);

	@Override
	public final Component getEditor(Object o,
			final ConfigurationChangeListener configListener,
			final JComponent parent) {

		final MatrixEditor matrixEditor = CreateMatrixEditor(o, configListener);

		// Create asynchronous modal dialog to edit the matrix
		//
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				final Container parentContainer = parent.getRootPane().getParent();

				final JDialog dialog = createDialog(parentContainer, matrixEditor);

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

				// Set preferred size
				{
					int desiredWidth = Math.min(matrixEditor.getColumnCount() * 70 + 100, 1024);
					desiredWidth = Math.max(desiredWidth, 600);

					int desiredHeight = Math.min(matrixEditor.getRowCount() * 30 + 150, 768);
					dialog.setPreferredSize(new Dimension(desiredWidth, desiredHeight));
				}

				dialog.pack();
				if (parentContainer != null) {
					dialog.setLocationRelativeTo(parentContainer); // Center on
					// screen
				}
				// Set dialog model
				//
				dialog.setModal(true);
				dialog.setVisible(true);

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
	private static JDialog createDialog(Container parentContainer, JPanel panel) {
		final JDialog dialog;

		if (parentContainer instanceof Frame) {
			dialog = new JDialog((Frame) parentContainer, panel.getName());
		} else if (parentContainer instanceof Dialog) {
			dialog = new JDialog((Dialog) parentContainer, panel.getName());
		} else {
			dialog = new JDialog((JDialog) null, panel.getName());
		}

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.getContentPane().add(panel, BorderLayout.CENTER);

		return dialog;
	}
}
