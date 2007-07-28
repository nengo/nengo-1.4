package ca.neo.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import ca.neo.ui.models.proxies.PNEFEnsemble;
import ca.neo.ui.models.proxies.PNetwork;
import ca.neo.ui.models.views.NetworkView;
import ca.neo.ui.views.objects.configurable.managers.SavedFileConfigManager;
import ca.neo.ui.widgets.Toolbox;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.world.impl.Frame;

public class NeoWorld extends Frame {

	public NeoWorld(String title) {
		super(title);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				runMacro();
			}
		});

	}

	public void runMacro() {
		PNetwork network = new PNetwork("Top Network");
		getWorld().getGround().catchObject(network);

		NetworkView networkViewer = network.openNetwork();

		PNEFEnsemble newEnsemble = new PNEFEnsemble("My Ensemble", 100, 1,
				"ensemble1");

		networkViewer.addNode(newEnsemble);

		newEnsemble.showAllOrigins();

	}

	private static final long serialVersionUID = 1L;

	public static NeoWorld getInstance() {
		return (NeoWorld) (Frame.getInstance());
	}

	public static void main(String[] args) {
		Frame.setInstance(new NeoWorld("NEOWorld"));
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
