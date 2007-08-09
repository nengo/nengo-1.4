package ca.neo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import ca.neo.io.FileManager;
import ca.neo.ui.NeoGraphics;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.Util;

public abstract class LoadObjectAction extends StandardAction {

	public LoadObjectAction(String description, String actionName) {
		super(description, actionName);

	}

	public LoadObjectAction(String description) {
		super(description);

	}

	File file;

	protected abstract void processObject(Object objLoaded);

	Object objLoaded;

	@Override
	protected void action() throws ActionException {
		int response = NeoGraphics.FileChooser.showOpenDialog(UIEnvironment
				.getInstance());
		if (response == JFileChooser.APPROVE_OPTION) {
			file = NeoGraphics.FileChooser.getSelectedFile();

			TrackedActivity loadActivity = new TrackedActivity(
					"Loading network") {

				@Override
				public void doActivity() {
					FileManager fm = new FileManager();
					try {
						objLoaded = fm.load(file);
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								processObject(objLoaded);
							}
						});

					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (ClassCastException e) {
						e.printStackTrace();
					} finally {
						if (objLoaded == null) {
							Util.Error("Incorrect file type or wrong version");
						}
					}

				}

			};
			loadActivity.startThread();
		}

	}
}
