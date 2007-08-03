package ca.neo.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuBar;

import ca.neo.model.Network;
import ca.neo.ui.models.nodes.PNetwork;
import ca.neo.ui.util.UIBuilder;
import ca.neo.ui.views.objects.configurable.managers.DialogConfig;
import ca.neo.ui.widgets.Toolbox;
import ca.neo.util.Environment;
import ca.shu.ui.lib.actions.ActionException;
import ca.shu.ui.lib.actions.ReversableAction;
import ca.shu.ui.lib.util.UIEnvironment;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.world.WorldObject;
import ca.shu.ui.lib.world.impl.GFrame;

public class NeoGraphics extends GFrame {

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		UIEnvironment.setInstance(new NeoGraphics("NEOWorld"));
	}

	Toolbox canvasView;

	public NeoGraphics(String title) {
		super(title + " - NEO Workspace");

		/*
		 * Only one instance of NeoWorld may be running at once
		 */
		UIEnvironment.setInstance(this);

		Environment.setUserInterface(true);

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
		MenuBuilder menu = new MenuBuilder("Start");
		menuBar.add(menu.getJMenu());
		menu.addAction(new CreateNetworkAction());
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

		public CreateNetworkAction() {
			super("Create new network");
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

}
