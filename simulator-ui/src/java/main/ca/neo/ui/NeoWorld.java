package ca.neo.ui;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.SwingUtilities;

import ca.neo.ui.models.proxies.PNetwork;
import ca.neo.ui.widgets.Toolbox;
import ca.shu.ui.lib.world.impl.Frame;

public class NeoWorld extends Frame {

	public NeoWorld(String title) {
		super(title);

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				PNetwork network = new PNetwork();
				if (network.isModelCreated()) {
					getWorld().getGround().catchObject(network);
					network.openNetwork();

				}
			}
		});

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
	public void createMenu(JMenuBar menuBar) {
		JMenu menu;
		menu = addMenu(menuBar, "Start");
		Frame.addActionToMenu(menu, new CreateNetworkAction());
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
					network = new PNetwork();
					if (network.isModelCreated())
						getWorld().getGround().catchObject(network);
				}
			}).start();

		}
	}

}
