package ca.neo.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import ca.neo.ui.views.NeoCanvas;
import ca.sw.graphics.world.WorldFrame;
import edu.umd.cs.piccolo.PNode;

public class NeoGraphics extends WorldFrame {
	public NeoGraphics(String title) {
		super(title);
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// (new FlickrScraper()).start();
		new NeoGraphics("NEOWorld");

	}


	
	@Override
	public void createMenu(JMenuBar menuBar) {
		JMenu menu;
		
//		 menu = new JMenu("File");
		// menuBar.add(menu);
		// menu.setMnemonic(KeyEvent.VK_V);
		//		
		// menu = new JMenu("Help");
		// menuBar.add(menu);
		// menu.setMnemonic(KeyEvent.VK_V);

		// Build the first menu.
		menu = addMenu(menuBar, "Create Object");		
				
		menu = addMenu(menuBar, "View");		
		menu.setMnemonic(KeyEvent.VK_V);
		addActionToMenu(menu, new CanvasAction());

		
	}

	PNode canvasView;

	public void showCanvas() {
		if (canvasView == null) {
			canvasView = new NeoCanvas();
			getWorld().addToSky(canvasView);
		}
	}

	@Override
	public void initialize() {
		showCanvas();
	}

	class CanvasAction extends AbstractAction {

		public CanvasAction() {
			super("Show Canvas");
		}

		public void actionPerformed(ActionEvent e) {
			showCanvas();
		}

	}

	class CustomMenu extends JMenu {

		@Override
		public Component add(Component c) {
			// TODO Auto-generated method stub
			return super.add(c);
		}

	}
}
