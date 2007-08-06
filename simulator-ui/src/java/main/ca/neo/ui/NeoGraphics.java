package ca.neo.ui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JMenuBar;

import ca.neo.io.FileManager;
import ca.neo.model.Network;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.views.objects.configurable.managers.DialogConfig;
import ca.neo.ui.widgets.Toolbox;
import ca.neo.util.Environment;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.objects.widgets.TrackedActivity;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.GFrame;
import edu.umd.cs.piccolo.util.PDebug;

public class NeoGraphics extends GFrame {

	public static final JFileChooser FileChooser = new JFileChooser();
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		new NeoGraphics("NEOWorld");
	}

	Toolbox canvasView;

	public NeoGraphics(String title) {
		super(title + " - NEO Workspace");

		Environment.setUserInterface(true);

		// PDebug.debugPaintCalls = true;

	}

	/**
	 * 
	 * @param object
	 *            Object to be added to the top-level world
	 * @return the object being added
	 */
	public WorldObject addWorldObject(WorldObject object) {
		getWorld().getGround().catchObject(object);
		return object;
	}

	@Override
	public void constructMenuBar(JMenuBar menuBar) {
		MenuBuilder menu = new MenuBuilder("File");
		menuBar.add(menu.getJMenu());
		menu.addAction(new LoadNetworkAction("Load network from file"));
		menu.addAction(new CreateNetworkAction("New network"));
	}

	public Toolbox getCanvasView() {
		return canvasView;
	}

	public void showCanvas() {
		if (canvasView == null) {
			System.out.println("Creating canvas");
			canvasView = new Toolbox();
			getWorld().getSky().addChild(canvasView);
		}
	}

	class CreateNetworkAction extends ReversableAction {
		private static final long serialVersionUID = 1L;

		PNetwork network;

		public CreateNetworkAction(String actionName) {
			super("Create new network", actionName);
		}

		@Override
		protected void action() throws ActionException {

			network = new PNetwork();
			new DialogConfig(network);

			if (network.isModelCreated()) {
				getWorld().getGround().catchObject(network);
			} else {
				throw new ActionException("Could not configure network", false);
			}

		}

		@Override
		protected void undo() {
			network.destroy();
		}
	}

	class LoadNetworkAction extends ReversableAction {

		private static final long serialVersionUID = 1L;

		PNetwork networkUI;

		public LoadNetworkAction(String actionName) {
			super("Load network from file", actionName);
			// TODO Auto-generated constructor stub
		}

		File file;

		@Override
		protected void action() throws ActionException {
			int response = FileChooser.showOpenDialog(NeoGraphics.this);
			if (response == JFileChooser.APPROVE_OPTION) {
				file = FileChooser.getSelectedFile();

				TrackedActivity loadActivity = new TrackedActivity(
						"Loading network") {

					@Override
					public void doActivity() {
						FileManager fm = new FileManager();
						try {
							Network network = (Network) (fm.load(file));
							networkUI = new PNetwork(network);
							getWorld().getGround().catchObject(networkUI);
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

		@Override
		protected void undo() throws ActionException {
			networkUI.destroy();

		}
	}

}
