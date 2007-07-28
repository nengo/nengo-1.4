package ca.neo.ui.models.proxies;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import ca.neo.model.Node;
import ca.neo.model.impl.NetworkImpl;
import ca.neo.ui.models.PModelNode;
import ca.neo.ui.models.icons.NetworkIcon;
import ca.neo.ui.models.views.NetworkView;
import ca.neo.ui.views.objects.configurable.PTString;
import ca.neo.ui.views.objects.configurable.PropertySchema;
import ca.neo.ui.views.objects.configurable.managers.IConfigurationManager;
import ca.shu.ui.lib.objects.Window;
import ca.shu.ui.lib.util.PopupMenuBuilder;

public class PNetwork extends PModelNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	static PropertySchema pName = new PTString("Name");

	static final String typeName = "Network";

	static PropertySchema[] zProperties = { pName };

	public int i = 4;

	Window insideView;

	NetworkView networkView;

	public PNetwork(boolean useDefaultConfigManager) {
		super(useDefaultConfigManager);
		init();
	}

	public PNetwork(IConfigurationManager configManager) {
		super(configManager);
		init();
	}

	/**
	 * 
	 * @param name
	 */
	public PNetwork(String name) {
		super();
		setProperty(pName, name);
		setModel(createModel());
		init();
	}

	@Override
	public PopupMenuBuilder constructMenu() {
		PopupMenuBuilder menu = super.constructMenu();
		menu.addSection("View Network");

		if (insideView == null
				|| (insideView.getWindowState() == Window.WindowState.MINIMIZED)) {

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
					minimizeNetwork();
				}
			});

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
			return getModelNetwork().getName();
		}
	}

	@Override
	public PropertySchema[] getPropertiesSchema() {
		return zProperties;
	}

	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return typeName;
	}

	public void minimizeNetwork() {
		if (insideView != null)
			insideView.minimize();

	}

	/**
	 * 
	 * @return the network viewer which contains the nodes of the Network model
	 */
	public NetworkView openNetwork() {
		if (insideView == null) {

			networkView = new NetworkView(this, getRoot());

			insideView = new Window(this, networkView);
			// setFrameVisible(true);

			insideView.translate(0, this.getHeight());
			// insideView.setDraggable(false);
		}
		insideView.restore();

		return networkView;

	}

	private void init() {
		setIcon(new NetworkIcon(this));
	}

	@Override
	protected Node createModel() {
		NetworkImpl network = new NetworkImpl();
		network.setName((String) getProperty(pName));

		return network;
	}

}
