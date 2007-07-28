package ca.neo.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import ca.neo.ui.models.viewers.NetworkView;
import ca.neo.ui.models.wrappers.PNEFEnsemble;
import ca.neo.ui.models.wrappers.PNetwork;
import ca.neo.ui.views.objects.configurable.managers.SavedFileConfigManager;
import ca.neo.ui.widgets.Toolbox;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.world.IWorldObject;
import ca.shu.ui.lib.world.impl.Frame;
import ca.shu.ui.lib.world.impl.WorldObjectImpl;

public class NeoGraphics extends Frame {

	public NeoGraphics(String title) {
		super(title);

		/*
		 * Only one instance of NeoWorld may be running at once
		 */
		Frame.setInstance(this);

	}

	private static final long serialVersionUID = 1L;

	public static NeoGraphics getInstance() {
		return (NeoGraphics) (Frame.getInstance());
	}

	public static void main(String[] args) {
		Frame.setInstance(new NeoGraphics("NEOWorld"));
	}

	Toolbox canvasView;

	@Override
	public void constructMenuBar(JMenuBar menuBar) {
		MenuBuilder menu = new MenuBuilder("Start");
		menuBar.add(menu.getJMenu());
		menu.addAction(new CreateNetworkAction());
	}

	public Toolbox getCanvasView() {
		return canvasView;
	}
	
	public void addObject(IWorldObject object) {
		getWorld().getGround().catchObject(object);
	}

	public void showCanvas() {
		if (canvasView == null) {
			System.out.println("Creating canvas");
			canvasView = new Toolbox();
			getWorld().getSky().addChild(canvasView);
		}
	}

	class CanvasAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CanvasAction() {
			super("Show Canvas");
		}

		public void actionPerformed(ActionEvent e) {
			showCanvas();
		}

	}

	class CreateNetworkAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public CreateNetworkAction() {
			super("New Network");
		}

		public void actionPerformed(ActionEvent e) {
			(new Thread() {
				public void run() {
					PNetwork network;
					network = new PNetwork(true);
					if (network.isModelCreated())
						getWorld().getGround().catchObject(network);
				}
			}).start();

		}
	}

}
