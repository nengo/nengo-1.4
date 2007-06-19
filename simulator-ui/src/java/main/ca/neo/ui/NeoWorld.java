package ca.neo.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ca.neo.ui.views.NeoToolbox;
import ca.neo.ui.views.factories.NodeFactory;
import ca.sw.graphics.world.WorldImpl;
import ca.sw.graphics.world.WorldFrame;
import ca.sw.util.Util;
import edu.umd.cs.piccolo.PNode;

public class NeoWorld extends WorldFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static NeoWorld instance;

	public static NeoWorld getInstance() {
		return instance;
	}

	public static WorldImpl getWorldInstance() {
		return instance.getWorld();
	}

	public static void main(String[] args) {
		instance = new NeoWorld("NEOWorld");
	}

	NeoToolbox canvasView;

	public NeoWorld(String title) {
		super(title);

	}

	@Override
	public void createMenu(JMenuBar menuBar) {
		JMenu menu;

		menu = addMenu(menuBar, "View");
		menu.setMnemonic(KeyEvent.VK_V);
		Util.addActionToMenu(menu, new CanvasAction());
		
		

		menuBar.add(NodeFactory.createNodeMenu());
	}

	public NeoToolbox getCanvasView() {
		return canvasView;
	}

	@Override
	public void initialize() {
		// int i= 0;
		showCanvas();
	}

	public void showCanvas() {
		if (canvasView == null) {
			 System.out.println("Creating canvas");
			canvasView = new NeoToolbox();
			// System.out.println("Finished Creating canvas");
			getWorld().addToSky(canvasView);
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

	class CustomMenu extends JMenu {
		private static final long serialVersionUID = 1L;

		@Override
		public Component add(Component c) {
			// TODO Auto-generated method stub
			return super.add(c);
		}

	}
}
