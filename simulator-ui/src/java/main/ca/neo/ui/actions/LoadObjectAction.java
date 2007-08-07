package ca.neo.ui.actions;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;

import ca.neo.io.FileManager;
import ca.neo.ui.NeoGraphics;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.StandardAction;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.UIEnvironment;

public abstract class LoadObjectAction extends StandardAction {

	public LoadObjectAction(String description, String actionName) {
		super(description, actionName);

	}

	public LoadObjectAction(String description) {
		super(description);

	}

	File file;

	protected abstract void processObject(Object objLoaded);

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
						Object objLoaded = fm.load(file);
						processObject(objLoaded);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					} catch (ClassCastException e) {
						e.printStackTrace();
					}

				}

			};
			loadActivity.startThread(true);

		}

	}

}
