package ca.neo.ui.models.views;

import java.awt.event.ActionEvent;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;

import ca.neo.model.Network;
import ca.neo.model.StructuralException;
import ca.neo.ui.models.PModel;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.IconWrapper;
import ca.neo.ui.models.proxies.PFunctionInput;
import ca.neo.ui.models.proxies.PNEFEnsemble;
import ca.neo.ui.models.proxies.PNetwork;
import ca.shu.ui.lib.handlers.IContextMenu;
import ca.shu.ui.lib.handlers.StatusBarHandler;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.MenuBuilder;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.util.Util;
import ca.shu.ui.lib.world.INamedObject;
import ca.shu.ui.lib.world.IWorld;
import ca.shu.ui.lib.world.impl.World;
import edu.umd.cs.piccolo.PRoot;
import edu.umd.cs.piccolo.event.PInputEvent;

public class NetworkView extends World implements INamedObject, IContextMenu {
	private static final long serialVersionUID = -3018937112672942653L;

	IconWrapper icon;

	Network network;

	PNetwork pNetwork;

	public NetworkView(PNetwork pNetwork, PRoot root) {
		super("", root);
		this.pNetwork = pNetwork;
		this.network = pNetwork.getModelNetwork();

		// getSky().setViewScale(0.5);

		setStatusBarHandler(new ModelStatusBarHandler(this));

		setFrameVisible(false);

	}

	public void addNode(PModelNode nodeProxy) {
		try {
			getModel().addNode(nodeProxy.getNode());

			NetworkView.this.getGround().catchObject(nodeProxy);

		} catch (StructuralException e) {
			Util.Warning(e.toString());
		}
	}

	public Window getFrame() {
		return (Window) getParent();
	}

	public Network getModel() {
		return network;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return "Network Viewer: " + pNetwork.getName();
	}

	@Override
	public void justDroppedInWorld() {
		// TODO Auto-generated method stub
		super.justDroppedInWorld();

		// this.animateToBounds(0, 0, 400, 300, 1000);
	}

	public void minimize() {
		getFrame().minimize();
	}

	public JPopupMenu showPopupMenu(PInputEvent event) {
		PopupMenuBuilder menu = new PopupMenuBuilder(getName());

		if (getParent() instanceof Window) {
			menu.addSection("View");
			menu.addAction(new MinimizeAction());
		}
		menu.addSection("Create new model");

		MenuBuilder nodesMenu = menu.createSubMenu("Nodes");
		MenuBuilder functionsMenu = menu.createSubMenu("Functions");

		/*
		 * Nodes
		 */
		nodesMenu.addAction(new AddModelAction(PNetwork.class));
		nodesMenu.addAction(new AddModelAction(PNEFEnsemble.class));

		/*
		 * Functions
		 */
		functionsMenu.addAction(new AddModelAction(PFunctionInput.class));

		return menu.getJPopupMenu();
	}

	class AddModelAction extends AbstractAction {

		private static final long serialVersionUID = 1L;

		@SuppressWarnings("unchecked")
		Class nc;

		public AddModelAction(Class nc) {
			super(nc.getSimpleName());
			this.nc = nc;
		}

		public void actionPerformed(ActionEvent e) {

			(new Thread() {
				public void run() {
					PModelNode nodeProxy;
					try {

						Class partypes[] = new Class[1];
						partypes[0] = Boolean.TYPE;

						Constructor ct = nc.getConstructor(partypes);
						Object arglist[] = new Object[1];
						arglist[0] = new Boolean(true);

						nodeProxy = (PModelNode) ct.newInstance(arglist);

						if (nodeProxy.isModelCreated())
							addNode(nodeProxy);
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}).start();

		}
	}

	class MinimizeAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public MinimizeAction() {
			super("Minimize");
		}

		public void actionPerformed(ActionEvent e) {
			minimize();
		}
	}
}

class ModelStatusBarHandler extends StatusBarHandler {

	public ModelStatusBarHandler(IWorld world) {
		super(world);
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getStatusStr(PInputEvent event) {
		PModel wo = (PModel) Util.getNodeFromPickPath(event, PModel.class);

		StringBuilder statuStr = new StringBuilder(getWorld().getName() + " | ");

		if (wo != null) {
			statuStr.append("Model name: " + wo.getName() + " ("
					+ wo.getTypeName() + ")");
		} else {
			statuStr.append("No Model Selected");
		}
		return statuStr.toString();
	}
}