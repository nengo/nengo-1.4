package ca.neo.ui.models.proxies;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenuItem;

import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.views.NetworkView;
import ca.neo.ui.views.objects.properties.PTString;
import ca.neo.ui.views.objects.properties.PropertySchema;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.PopupMenuBuilder;
import ca.shu.ui.lib.world.impl.WorldObject;

public class PNetwork extends PModelNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static String pDim = "Dimensions";

	static String pName = "Name";

	static PropertySchema[] zMetaProperties = { new PTString(pName) };

	public int i = 4;

	Window insideView;

	public PNetwork() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public PopupMenuBuilder constructPopup() {
		PopupMenuBuilder menu = super.constructPopup();
		JMenuItem item;

		menu.addSection("View Network");
		


		if (insideView == null || (insideView.getWindowState()== Window.WindowState.MINIMIZED)) {
			
			menu.addAction(new AbstractAction("Expand Network") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					openNetwork();
				}
			});
			
		} else {
			menu.addAction(new AbstractAction("Minimize Network") {
				private static final long serialVersionUID = 1L;

				public void actionPerformed(ActionEvent e) {
					shrinkNetwork();
				}
			} );
	
		}
		return menu;

	}

	public NetworkImpl getModelNetwork() {
		return (NetworkImpl) getModel();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		if (getModelNetwork() == null) {
			return super.getName();
		} else {
			return super.getName() + ": " + getModelNetwork().getName();
		}
	}

	@Override
	public PropertySchema[] getPropertiesSchema() {
		return zMetaProperties;
	}

	public void openNetwork() {
		if (insideView == null) {

			NetworkView networkView = new NetworkView(this, getRoot());

			insideView = new Window(this, networkView);
			// setFrameVisible(true);

			insideView.translate(0, this.getHeight());
			// insideView.setDraggable(false);
		}
		insideView.restore();

	}

	public void shrinkNetwork() {
		if (insideView != null)
			insideView.minimize();

	}

	@Override
	protected Node createModel() {
		NetworkImpl network = new NetworkImpl();
		network.setName((String) getProperty(pName));

		return network;
	}

	@Override
	protected WorldObject createIcon() {
		return new NetworkIcon();
	}

}
