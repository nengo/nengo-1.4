package ca.neo.ui.views.factories;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ca.neo.ui.NeoWorld;
import ca.neo.ui.views.icons.EnsembleIcon;
import ca.neo.ui.views.icons.FunctionIcon;
import ca.neo.ui.views.icons.NetworkIcon;
import ca.neo.ui.views.objects.ConstantFunctionProxy;
import ca.neo.ui.views.objects.EnsembleProxy;
import ca.neo.ui.views.objects.NetworkProxy;
import ca.neo.ui.views.objects.ProxyObject;
import ca.sw.graphics.basics.GDefaults;
import ca.sw.graphics.world.World;
import ca.sw.util.Util;

public class GNodeFactory {

	public static GNodeCreator network = new GNodeCreator(NetworkIcon.class,
			NetworkProxy.class);

	public static GNodeCreator ensemble = new GNodeCreator(EnsembleIcon.class,
			EnsembleProxy.class);
	
	public static GNodeCreator constantFunction = new GNodeCreator(FunctionIcon.class,
			ConstantFunctionProxy.class);
	

	public static JMenu createNodeMenu() {

		JMenu menu = new JMenu("Add Object");
		GDefaults.styleComponent(menu);

		
		JMenu nodesMenu = new JMenu("Nodes");
//		GDefaults.styleComponent(nodesMenu);
		menu.add(nodesMenu);
		
		JMenu functionsMenu = new JMenu("Functions");
//		GDefaults.styleComponent(nodesMenu);
		menu.add(functionsMenu);
		
		
		/*
		 * Nodes
		 */
		addNodeCreatorToMenu(nodesMenu, network);
		addNodeCreatorToMenu(nodesMenu, ensemble);

		/*
		 * Functions
		 */
		addNodeCreatorToMenu(functionsMenu, constantFunction);
		
		return menu;
	}

	public static JMenuItem addActionToMenu(JMenu menu, AbstractAction action) {
		JMenuItem menuItem = new JMenuItem(action);
		// GDefaults.styleComponent(menuItem);
		menu.add(menuItem);

		return menuItem;
	}

	protected static void addNodeCreatorToMenu(JMenu menu, GNodeCreator nc) {
		String name = nc.repClass.getSimpleName();
		JMenu ncMenu = new JMenu(name);
		// GDefaults.styleComponent(ncMenu);

		Util.addActionToMenu(ncMenu, new AddToWorldAction(nc));
		Util.addActionToMenu(ncMenu, new AddToCanvasAction(nc));
		menu.add(ncMenu);

	}

}

class AddToCanvasAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	GNodeCreator nc;

	public AddToCanvasAction(GNodeCreator nc) {
		super("Add to canvas");
		this.nc = nc;
	}

	public void actionPerformed(ActionEvent e) {
		NeoWorld.getInstance().getCanvasView().addItem(nc.createIcon());
	}

}

class AddToWorldAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	GNodeCreator nc;

	public AddToWorldAction(GNodeCreator nc) {
		super("Add to world");
		this.nc = nc;
	}

	public void actionPerformed(ActionEvent e) {
		ProxyObject obj = nc.createNode();

		World world = NeoWorld.getInstance().getWorld();
		world.getGround().dropOnto(obj);

		obj.initProxy();

	}

}
