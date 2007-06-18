package ca.neo.ui.views.objects.proxies;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import sun.net.NetworkServer;

import ca.neo.model.Network;
import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.model.impl.NodeFactory;
import ca.neo.model.nef.NEFEnsemble;
import ca.neo.plot.Plotter;
import ca.neo.ui.views.icons.Icon;
import ca.neo.ui.views.objects.properties.PTInt;
import ca.neo.ui.views.objects.properties.PTString;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.sw.graphics.world.WorldObjectImpl;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.handles.PBoundsHandle;

public class PNetwork extends ProxyNode {

	static String pDim = "Dimensions";

	static String pName = "Name";

	static PropertySchema[] metaProperties = { new PTString(pName) };

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int i = 4;

	@Override
	public JPopupMenu createPopupMenu() {
		// TODO Auto-generated method stub
		JPopupMenu menu = super.createPopupMenu();
		JMenuItem item;
		item = new JMenuItem(new AbstractAction("Show Within") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				expand();
			}
		});
		menu.add(item);

		item = new JMenuItem(new AbstractAction("Hide Within") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				shrink();
			}
		});
		menu.add(item);

		return menu;

	}

	NetworkView expandedView;

	public void expand() {
		if (expandedView == null) {
			expandedView = new NetworkView(this);

			setFrameVisible(true);
			
			expandedView.translate(0, this.getHeight());
			expandedView.setDraggable(false);

			this.addChild(expandedView);
//			pack();
		}
	}

	public void shrink() {
		if (expandedView != null) {
			expandedView.removeFromParent();

//			pack();
		}
		
	}

	@Override
	public PropertySchema[] getMetaProperties() {
		return metaProperties;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		if (getNetworkProxy() == null) {
			return super.getName();
		} else {
			return super.getName() + ": " + getNetworkProxy().getName();
		}
	}

	public NetworkImpl getNetworkProxy() {
		return (NetworkImpl) getProxy();
	}

	@Override
	protected Node createProxy() {
		NetworkImpl network = new NetworkImpl();
		network.setName((String) getProperty(pName));
		return network;
	}

}

class NetworkView extends WorldObjectImpl {
	private static final long serialVersionUID = -3018937112672942653L;

	PNetwork pNetwork;

	Network network;

	Icon icon;

	public NetworkView(PNetwork pNetwork) {
		super();
		this.pNetwork = pNetwork;
		this.network = pNetwork.getNetworkProxy();
		// this.addChild((PNode)(pNetwork.getIcon().clone()));

		setFrameVisible(true);
//		setBounds(0, 0, 400, 300);
		PBoundsHandle.addBoundsHandlesTo(this);
		
		
	}

	@Override
	protected void initialize() {
		// TODO Auto-generated method stub
		super.initialize();
		
		this.animateToBounds(0,0, 400,300, 1000);
	}

}
