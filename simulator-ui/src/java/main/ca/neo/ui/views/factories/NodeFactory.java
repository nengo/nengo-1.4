package ca.neo.ui.views.factories;

import java.awt.event.ActionEvent;
import java.io.Serializable;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import ca.neo.ui.NeoWorld;
import ca.neo.ui.views.icons.EnsembleIcon;
import ca.neo.ui.views.icons.FunctionInputIcon;
import ca.neo.ui.views.icons.Icon;
import ca.neo.ui.views.icons.NetworkIcon;
import ca.neo.ui.views.objects.proxies.PEnsemble;
import ca.neo.ui.views.objects.proxies.PFunctionInput;
import ca.neo.ui.views.objects.proxies.PNetwork;
import ca.neo.ui.views.objects.proxies.ProxyGeneric;
import ca.sw.graphics.basics.GDefaults;
import ca.sw.graphics.nodes.WorldObject;
import ca.sw.graphics.world.World;
import ca.sw.util.Util;

public class NodeFactory {

	public static NodeType functionInput = new NodeType(FunctionInputIcon.class,
			PFunctionInput.class);

	public static NodeType ensemble = new NodeType(EnsembleIcon.class,
			PEnsemble.class);
	
	public static NodeType network = new NodeType(NetworkIcon.class,
			PNetwork.class);
	

	public static JMenuItem addActionToMenu(JMenu menu, AbstractAction action) {
		JMenuItem menuItem = new JMenuItem(action);
		// GDefaults.styleComponent(menuItem);
		menu.add(menuItem);

		return menuItem;
	}

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
		addNodeCreatorToMenu(functionsMenu, functionInput);
		
		return menu;
	}

	protected static void addNodeCreatorToMenu(JMenu menu, NodeType nc) {
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

	NodeType nc;

	public AddToCanvasAction(NodeType nc) {
		super("Add to canvas");
		this.nc = nc;
	}

	public void actionPerformed(ActionEvent e) {
		NeoWorld.getInstance().getCanvasView().addItem(nc.createIcon());
	}

}

class AddToWorldAction extends AbstractAction {

	private static final long serialVersionUID = 1L;

	NodeType nc;

	public AddToWorldAction(NodeType nc) {
		super("Add to world");
		this.nc = nc;
	}

	public void actionPerformed(ActionEvent e) {
		ProxyGeneric obj = nc.createNode();

		World world = NeoWorld.getInstance().getWorld();
		world.getGround().dropOnto(obj);

		obj.initProxy();

	}

}

class NodeType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Class iconClass;

	Class repClass;

	Class symbolClass;

	protected NodeType(Class iconClass, Class repClass) {
		this.iconClass = iconClass;
		this.repClass = repClass;
	}

	public WorldObject createIcon() {
		try {
			// Symbol symbol = new Symbol(this);

			return new Symbol((Icon) (iconClass.newInstance()), this);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public ProxyGeneric createNode() {
		try {
			ProxyGeneric obj = (ProxyGeneric) (repClass.newInstance());
			obj.setIcon((Icon) (iconClass.newInstance()));
			return obj;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

